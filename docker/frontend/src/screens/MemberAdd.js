import React, { useState, useEffect } from 'react';
import DateTimePicker from '@react-native-community/datetimepicker';
import {
  ScrollView,
  View,
  Text,
  TextInput,
  Button,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  TouchableWithoutFeedback,
  Alert,
  Pressable
} from 'react-native';
import { useTheme } from '../config/ThemeContext';
import { Dropdown } from 'react-native-element-dropdown';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { api } from '../api/axiosApi';

const MemberAdd = (props) => {
  const { isDarkMode } = useTheme();

  const editingMember = props.route.params?.member || null;

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    gender: '',
    phone: '',
    email: '',
    street: '',
    number: '',
    postalcode: '',
    city: '',
    birthdate: new Date(),
    memberSince: new Date(),
    instruments: [],
    picture: '',
    management: false
  });

  const [instrumentOptions, setInstrumentOptions] = useState([]);
  const [isFocus, setIsFocus] = useState(false);

  const [showBirthdatePicker, setShowBirthdatePicker] = useState(false);
  const [showMemberSincePicker, setShowMemberSincePicker] = useState(false);

  const showAlert = (title, message, buttons = []) => {
    if (Platform.OS === 'web') {
      const [okButton] = buttons.filter(
        (button) => button.text === 'OK' || button.text === 'Delete'
      );
      if (okButton?.onPress) {
        if (window.confirm(message)) {
          okButton.onPress();
        }
      } else {
        window.alert(`${title}\n${message}`);
      }
    } else {
      Alert.alert(title, message, buttons);
    }
  };

  useEffect(() => {
    const fetchInstruments = async () => {
      try {
        const response = await api.get('/api/categories/instruments');
        const formattedData = response.data.map((item) => ({
          label: item,
          value: item,
        }));
        setInstrumentOptions(formattedData);
      } catch (error) {
        console.error('Error fetching instruments:', error);
        showAlert('Error', 'Failed to load instruments list.', [
          {
            text: 'Ok',
            style: 'cancel',
          }
        ]);
      }
    };

    fetchInstruments();
  }, []);

  // Load member data for editing
  useEffect(() => {
    if (editingMember) {
      setFormData({
        firstName: editingMember.name.first,
        lastName: editingMember.name.last,
        gender: editingMember.gender || '',
        phone: editingMember.phone,
        email: editingMember.email,
        street: editingMember.address.street,
        number: editingMember.address.number.toString(),
        postalcode: editingMember.address.postalcode.toString(),
        city: editingMember.address.city,
        birthdate: new Date(editingMember.birthdate),
        memberSince: new Date(editingMember.memberSince),
        instruments: editingMember.instruments || [],
        picture: editingMember.picture,
        management: editingMember.management ?? false
      });
    }
  }, [editingMember]);

  const handleChange = (field, value) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    const requiredFields = [
      { key: 'firstName', label: 'First Name' },
      { key: 'lastName', label: 'Last Name' },
      { key: 'gender', label: 'Gender' },
      { key: 'phone', label: 'Phone' },
      { key: 'email', label: 'E-mail' },
      { key: 'street', label: 'Street' },
      { key: 'number', label: 'Number' },
      { key: 'postalcode', label: 'Postal Code' },
      { key: 'city', label: 'City' },
    ];
  
    const missingFields = requiredFields.filter(
      (field) => !formData[field.key] || formData[field.key].trim() === ''
    );
  
    if (missingFields.length > 0) {
      const missingFieldLabels = missingFields.map((field) => field.label).join('\n- ');
      showAlert(
        'Missing Required Fields',
        `Please fill in the following fields:\n- ${missingFieldLabels}`,
        [
          {
            text: 'Ok',
            style: 'cancel',
          },
        ]
      );
      return;
    }
  
    const postalCodeNumber = parseInt(formData.postalcode, 10);
    if (isNaN(postalCodeNumber) || postalCodeNumber < 1000 || postalCodeNumber > 9999) {
      showAlert('Wrong input', 'Postal code must be a number between 1000 and 9999.', [
        {
          text: 'Ok',
          style: 'cancel',
        },
      ]);
      return;
    }
  
    if (formData.phone.length < 4 || formData.phone.length > 21) {
      showAlert('Wrong input', 'Phone number must be between 4 and 21 characters.', [
        {
          text: 'Ok',
          style: 'cancel',
        },
      ]);
      return;
    }
  
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      showAlert('Wrong input', 'Please enter a valid email address.', [
        {
          text: 'Ok',
          style: 'cancel',
        },
      ]);
      return;
    }
  
    const today = new Date();
    if (formData.birthdate > today) {
      showAlert('Invalid Birthdate', 'Birthdate must be a past date.', [
        {
          text: 'Ok',
          style: 'cancel',
        },
      ]);
      return;
    }
  
    const requestBody = {
      gender: formData.gender,
      name: { first: formData.firstName, last: formData.lastName },
      address: {
        street: formData.street,
        number: formData.number,
        city: formData.city,
        postalcode: postalCodeNumber,
      },
      email: formData.email || '',
      phone: formData.phone || '',
      birthdate: formData.birthdate.toISOString(),
      memberSince: formData.memberSince.toISOString(),
      instruments: formData.instruments,
      picture: formData.picture || '',
      isManagement: formData.management,
    };
  
    try {
      if (editingMember) {
        await api.put(`/api/members/${editingMember.id}`, requestBody);
        showAlert('Success', 'Member updated successfully!', [
          {
            text: 'Ok',
            style: 'cancel',
          },
        ]);
      } else {
        await api.post('/api/members', requestBody);
        showAlert('Success', 'Member added successfully!', [
          {
            text: 'Ok',
            style: 'cancel',
          },
        ]);
      }
  
      props.navigation.goBack();
    } catch (error) {
      console.error('Error saving member:', error);
  
      if (error.response) {
        const status = error.response.status;
        const message = error.response.data?.message || error.message;
  
        if (status === 400 || status === 500) {
          showAlert('Error', `Failed to save member: ${message}`, [
            {
              text: 'Ok',
              style: 'cancel',
            },
          ]);
          return;
        }
      }
  
      showAlert('Error', 'An unexpected error occurred. Please try again.', [
        {
          text: 'Ok',
          style: 'cancel',
        },
      ]);
    }
  };
  

  const genderOptions = [
    { label: 'Male', value: 'MALE' },
    { label: 'Female', value: 'FEMALE' },
    { label: 'X', value: 'X' },
  ];

  const renderDatePicker = (field, label, showPicker, setShowPicker) => {
    if (Platform.OS === 'web') {
      return (
        <View style={{ marginBottom: 10 }}>
          <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>{label}</Text>
          <input
            type="date"
            value={formData[field] instanceof Date && !isNaN(formData[field])
              ? formData[field].toISOString().split('T')[0]
              : ''}
            onChange={(e) => {
              const value = e.target.value;
              const parsedDate = new Date(value);

              if (!isNaN(parsedDate)) {
                handleChange(field, parsedDate);
              } else {
                handleChange(field, '');
              }
            }}
            style={{
              padding: 5,
              borderRadius: 4,
              border: '1px solid #ccc',
              fontSize: 14,
              width: '100%',
            }}
          />
        </View>
      );
    }

    return (
      <View>
        <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
            {label}
        </Text>
        <Pressable
          style={[
            styles.datePickerButton,
            { backgroundColor: isDarkMode ? '#555' : '#fff' },
          ]}
          onPress={() => setShowPicker(true)}
          accessibilityHint='Press to open date picker'
        >
          <Text style={{ color: isDarkMode ? '#fff' : '#000' }}>
            {`${
              formData[field] instanceof Date && !isNaN(formData[field])
                ? formData[field].toDateString()
                : 'Invalid Date'
            }`}
          </Text>
        </Pressable>
        {showPicker && (
          <DateTimePicker
            value={formData[field] instanceof Date && !isNaN(formData[field])
              ? formData[field]
              : new Date()}
            mode="date"
            display="default"
            onChange={(event, date) => {
              setShowPicker(false);
              if (date) handleChange(field, date);
            }}
          />
        )}
      </View>
    );
  };

  const CustomCheckbox = ({ value, onValueChange, label, isDarkMode }) => {
    return (
      <Pressable
        onPress={() => onValueChange(!value)}
        style={[
          styles.checkboxContainer,
          { backgroundColor: isDarkMode ? '#444444' : '#ffffff' },
        ]}
      >
        <Ionicons
          name={value ? 'checkbox' : 'square-outline'}
          size={24}
          color={isDarkMode ? '#ffffff' : '#333'}
        />
        <Text style={[styles.checkboxLabel, { color: isDarkMode ? '#ffffff' : '#333' }]}>
          {label}
        </Text>
      </Pressable>
    );
  };

  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <TouchableWithoutFeedback>
        <ScrollView
          contentContainerStyle={[
            styles.container,
            { backgroundColor: isDarkMode ? '#333333' : '#f5f5f5' },
          ]}
          keyboardShouldPersistTaps="handled"
        >
          <View
            style={[styles.section, { backgroundColor: isDarkMode ? '#444444' : '#ffffff' }]}
          >
            <Text style={[styles.sectionTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
              Personal Info
            </Text>

            {formData.firstName.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 First Name
               </Text>
             )}
            <TextInput
              style={[
                styles.input,
                { color: isDarkMode ? '#fff' : '#000' },
                isDarkMode && { backgroundColor: '#555' },
              ]}
              placeholder="First Name"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.firstName}
              onChangeText={(text) => handleChange('firstName', text)}
            />

            {formData.lastName.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 Last Name
               </Text>
             )}
            <TextInput
              style={[
                styles.input,
                { color: isDarkMode ? '#fff' : '#000' },
                isDarkMode && { backgroundColor: '#555' },
              ]}
              placeholder="Last Name"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.lastName}
              onChangeText={(text) => handleChange('lastName', text)}
            />
            <View style={styles.dropdownContainer} accessible={true}>
              <Dropdown
                style={[
                  styles.dropdown,
                  isFocus && { borderColor: '#007BFF' },
                  isDarkMode && { backgroundColor: '#555', borderColor: '#777' },
                ]}
                placeholderStyle={[
                  styles.placeholderStyle,
                  { color: isDarkMode ? '#888' : '#ccc' },
                ]}
                selectedTextStyle={[
                  styles.selectedTextStyle,
                  { color: isDarkMode ? '#fff' : '#000' },
                ]}
                iconStyle={styles.iconStyle}
                data={genderOptions}
                maxHeight={160}
                labelField="label"
                valueField="value"
                placeholder={!isFocus ? 'Select Gender' : '...'}
                value={formData.gender}
                onFocus={() => setIsFocus(true)}
                onBlur={() => setIsFocus(false)}
                onChange={(item) => {
                  handleChange('gender', item.value);
                  setIsFocus(false);
                }}
                accessibilityLabel='Gender dropdown, press to select gender'
                accessibilityHint='Press to select gender'
              />
            </View>

            {renderDatePicker('birthdate', 'Birthdate', showBirthdatePicker, setShowBirthdatePicker)}
            {editingMember && renderDatePicker('memberSince', 'Member Since', showMemberSincePicker, setShowMemberSincePicker)}


            {formData.picture.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 Picture URL
               </Text>
             )}
            <TextInput
              style={[
                styles.input,
                { color: isDarkMode ? '#fff' : '#000' },
                isDarkMode && { backgroundColor: '#555' },
              ]}
              placeholder="Picture url"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.picture}
              onChangeText={(text) => handleChange('picture', text)}
            />
          </View>

          <View
            style={[styles.section, { backgroundColor: isDarkMode ? '#444444' : '#ffffff' }]}
          >
            <Text style={[styles.sectionTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
              Contact Info
            </Text>

            {formData.phone.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 Phone Number
               </Text>
             )}
            <TextInput
              style={[styles.input, { color: isDarkMode ? '#fff' : '#000' }]}
              placeholder="Phone"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.phone}
              onChangeText={(text) => handleChange('phone', text)}
              accessibilityLabel='Phone number'
            />

            {formData.email.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 E-mail
               </Text>
             )}
            <TextInput
              style={[styles.input, { color: isDarkMode ? '#fff' : '#000' }]}
              placeholder="Email"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.email}
              onChangeText={(text) => handleChange('email', text)}
            />

            {formData.street.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 Street
               </Text>
             )}
            <TextInput
              style={[styles.input, { color: isDarkMode ? '#fff' : '#000' }]}
              placeholder="Street"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.street}
              onChangeText={(text) => handleChange('street', text)}
            />

            {formData.number.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 House number
               </Text>
             )}
            <TextInput
              style={[styles.input, { color: isDarkMode ? '#fff' : '#000' }]}
              placeholder="Number"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.number}
              onChangeText={(text) => handleChange('number', text)}
            />

            {formData.postalcode.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 Postal Code
               </Text>
             )}
            <TextInput
              style={[styles.input, { color: isDarkMode ? '#fff' : '#000' }]}
              placeholder="Postal Code"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.postalcode}
              onChangeText={(text) => handleChange('postalcode', text)}
            />

            {formData.city.trim() !== '' && (
               <Text style={[styles.label, { color: isDarkMode ? '#aaaaaa' : '#555' }]}>
                 City
               </Text>
             )}
            <TextInput
              style={[styles.input, { color: isDarkMode ? '#fff' : '#000' }]}
              placeholder="City"
              placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
              value={formData.city}
              onChangeText={(text) => handleChange('city', text)}
            />

            <CustomCheckbox
              value={formData.management}
              onValueChange={(newValue) => handleChange('management', newValue)}
              label="Is Management"
              isDarkMode={isDarkMode}
            />
          </View>

          <View
            style={[styles.section, { backgroundColor: isDarkMode ? '#444444' : '#ffffff' }]}
          >
            <Text style={[styles.sectionTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
              Instruments
            </Text>

            {formData.instruments.length > 0 && (
              <View style={styles.selectedInstrumentsContainer}>
                {formData.instruments.map((instrument, index) => (
                  <View key={index} style={[styles.instrumentTag, {backgroundColor: isDarkMode ? '#666666' : '#e0e0e0'}]}>
                    <Text style={{ color: isDarkMode ? '#fff' : '#000' }}>{instrument}</Text>
                    <Text
                      style={styles.removeButton}
                      onPress={() => {
                        const updatedInstruments = formData.instruments.filter(
                          (item) => item !== instrument
                        );
                        handleChange('instruments', updatedInstruments);
                      }}
                    >
                      ✕
                    </Text>
                  </View>
                ))}
              </View>
            )}

            <Dropdown
              style={[
                styles.dropdown,
                isFocus && { borderColor: '#007BFF' },
                isDarkMode && { backgroundColor: '#555', borderColor: '#777' },
              ]}
              placeholderStyle={[styles.placeholderStyle, { color: isDarkMode ? '#888' : '#ccc' }]}
              selectedTextStyle={[styles.selectedTextStyle, { color: isDarkMode ? '#fff' : '#000' }]}
              iconStyle={styles.iconStyle}
              data={instrumentOptions} // Data fetched from API
              maxHeight={300}
              labelField="label"
              valueField="value"
              placeholder={!isFocus ? 'Select Instruments' : '...'}
              value={null} // Set value to null so dropdown doesn't show the last selected
              onFocus={() => setIsFocus(true)}
              onBlur={() => setIsFocus(false)}
              onChange={(item) => {
                if (!formData.instruments.includes(item.value)) {
                  handleChange('instruments', [...formData.instruments, item.value]);
                }
                setIsFocus(false);
              }}
              dropdownPosition="top"
              accessibilityLabel='Instruments dropdown, press to select instrument'
            />
          </View>

          <Button
            title={editingMember ? 'Update Member' : 'Save Member'}
            onPress={handleSave}
            color={'#007BFF'}
          />
        </ScrollView>
      </TouchableWithoutFeedback>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    padding: 16,
  },
  section: {
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  input: {
    height: 40,
    borderWidth: 1,
    borderColor: '#ccc',
    marginBottom: 12,
    fontSize: 14,
    paddingHorizontal: 8,
    borderRadius: 4,
  },
  dropdownContainer: {
    marginBottom: 12,
  },
  dropdown: {
    height: 40,
    borderColor: '#ccc',
    borderWidth: 1,
    borderRadius: 4,
    paddingHorizontal: 8,
  },
  placeholderStyle: {
    fontSize: 14,
    color: '#ccc',
  },
  selectedTextStyle: {
    fontSize: 14,
    color: '#000',
  },
  iconStyle: {
    width: 20,
    height: 20,
  },
  selectedInstrumentsContainer: {
  flexDirection: 'row',
  flexWrap: 'wrap',
  marginBottom: 8,
},
instrumentTag: {
  flexDirection: 'row',
  alignItems: 'center',
  backgroundColor: '#e0e0e0',
  paddingVertical: 4,
  paddingHorizontal: 8,
  borderRadius: 16,
  marginRight: 8,
  marginBottom: 8,
  shadowColor: '#000',
  shadowOpacity: 0.1,
  shadowOffset: { width: 0, height: 1 },
  shadowRadius: 2,
  elevation: 2,
},
removeButton: {
  color: '#ff5555',
  fontWeight: 'bold',
  marginLeft: 8,
  fontSize: 16,
},
datePickerButton: {
  height: 40,
  borderWidth: 1,
  borderColor: '#ccc',
  justifyContent: 'center',
  paddingHorizontal: 8,
  borderRadius: 4,
  marginBottom: 12,
},
checkboxContainer: {
  flexDirection: 'row',
  alignItems: 'center',
  padding: 8,
  borderWidth: 1,
  borderColor: '#ccc',
  borderRadius: 4,
  marginBottom: 12,
},
checkboxLabel: {
  marginLeft: 8,
  fontSize: 16,
},
label: {
  fontSize: 12,
  marginBottom: 3,
}
});

export default MemberAdd;

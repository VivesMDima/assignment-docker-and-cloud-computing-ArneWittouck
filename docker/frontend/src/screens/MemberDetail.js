import React, { useState, useEffect, useCallback } from 'react';
import { ScrollView, View, Text, Image, StyleSheet, Pressable, Alert } from 'react-native';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { useTheme } from '../config/ThemeContext';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useFocusEffect } from '@react-navigation/native';
import { api } from '../api/axiosApi';

const MemberDetail = (props) => {
  const { isDarkMode } = useTheme();

  const memberId = props.route.params.member.id;

  const [member, setMember] = useState(props.route.params.member);

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-GB').format(date); // Formats as dd/mm/yyyy
  };

  const fetchMember = async () => {
    try {
      const response = await api.get(`/api/members/${memberId}`);
      setMember(response.data);
    } catch (error) {
      console.error('Error fetching member details:', error);
      Alert.alert('Error', 'Failed to fetch updated member details.');
    }
  };

  useFocusEffect(
    useCallback(() => {
      fetchMember();
    }, [])
  );

  const handleEdit = () => {
    props.navigation.navigate('MemberAdd', { member: member });
  };

  if (!member) {
    return (
      <SafeAreaView style={[styles.container, { backgroundColor: isDarkMode ? '#333333' : '#f5f5f5' }]}>
        <Text style={{ textAlign: 'center', marginTop: 20, color: isDarkMode ? '#ffffff' : '#333' }}>
          Loading member details...
        </Text>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={[styles.container, { backgroundColor: isDarkMode ? '#333333' : '#f5f5f5' }]}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View
          style={[
            styles.headerContainer,
            { backgroundColor: isDarkMode ? '#444444' : '#ffffff' },
          ]} accessible={true}
        >
          {member.picture && member.picture.trim() !== '' && (
            <Image source={{ uri: member.picture }} style={styles.avatar} accessibilityLabel='profile picture'/>
          )}
          <View style={styles.headerTextContainer}>
            <Text style={[styles.name, { color: isDarkMode ? '#ffffff' : '#333' }]}>
              {`${member.name.first} ${member.name.last}`}
            </Text>
            <Text style={[styles.memberSince, { color: isDarkMode ? '#bbbbbb' : '#666' }]}>
              {`Member since: ${formatDate(member.memberSince)}`}
            </Text>
            {member.management && (
              <Text
                style={[
                  styles.managementLabel,
                  { color: isDarkMode ? '#78c3fa' : '#225bb3' },
                ]}
              >
                Management
              </Text>
            )}
          </View>
          <Pressable onPress={handleEdit} style={styles.editButton} accessibilityLabel='Edit button' accessibilityHint='Press to edit member'>
            <Ionicons name="create-outline" size={24} color="#fff" />
          </Pressable>
        </View>

        <View
          style={[styles.section, { backgroundColor: isDarkMode ? '#444444' : '#ffffff' }]} accessible={true}
        >
          <Text style={[styles.sectionTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
            Contact Info
          </Text>
          <View style={styles.sectionContent}>
            <Text style={[styles.text, { color: isDarkMode ? '#cccccc' : '#555' }]}>
              {`Phone: ${member.phone}`}
            </Text>
            <Text style={[styles.text, { color: isDarkMode ? '#cccccc' : '#555' }]}>
              {`Email: ${member.email}`}
            </Text>
            <Text style={[styles.text, { color: isDarkMode ? '#cccccc' : '#555' }]}>
              {`Address: ${member.address.street} ${member.address.number}, ${member.address.postalcode} ${member.address.city}`}
            </Text>
          </View>
        </View>
        <View
          style={[styles.section, { backgroundColor: isDarkMode ? '#444444' : '#ffffff' }]} accessible={true}
        >
          <Text style={[styles.sectionTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
            Birthday
          </Text>
          <Text style={[styles.text, { color: isDarkMode ? '#cccccc' : '#555' }]}>
            {formatDate(member.birthdate)}
          </Text>
        </View>
        {member.instruments && member.instruments.length > 0 && (
          <View
            style={[styles.section, { backgroundColor: isDarkMode ? '#444444' : '#ffffff' }]} accessible={true}
          >
            <Text style={[styles.sectionTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
              Instruments
            </Text>
            {member.instruments.map((instrument, index) => (
              <Text
                key={index}
                style={[styles.text, { color: isDarkMode ? '#cccccc' : '#555' }]}
              >
                {instrument}
              </Text>
            ))}
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    padding: 16,
  },
  headerContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
    padding: 16,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 4,
    elevation: 4,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    marginRight: 16,
  },
  headerTextContainer: {
    flex: 1,
  },
  name: {
    fontSize: 20,
    fontWeight: 'bold',
  },
  memberSince: {
    fontSize: 14,
    marginTop: 4,
  },
  editButton: {
    marginLeft: 16,
    padding: 8,
    borderRadius: 8,
    backgroundColor: "#007BFF",
    alignItems: 'center',
    justifyContent: 'center',
  },
  section: {
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 4,
    elevation: 4,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  sectionContent: {
    marginTop: 8,
  },
  text: {
    fontSize: 14,
    marginBottom: 4,
  },
  managementLabel: {
    fontSize: 14,
    fontWeight: 'bold',
    marginTop: 4,
  }
});

export default MemberDetail;

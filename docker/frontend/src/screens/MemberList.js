import React, { useState, useEffect, useCallback } from 'react';
import { Text, Image, View, FlatList, StyleSheet, Pressable, TextInput, Alert } from 'react-native';
import { Platform } from 'react-native';
import { useTheme } from '../config/ThemeContext';
import { useSettings } from '../config/SettingsContext';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { api } from '../api/axiosApi';
import { useFocusEffect } from '@react-navigation/native';

const MemberList = (props) => {
  const { isDarkMode } = useTheme();
  const [searchText, setSearchText] = useState('');

  const [members, setMembers] = useState([]);
  const [filteredMembers, setFilteredMembers] = useState([]);

  const [selectionMode, setSelectionMode] = useState(false);
  const [selectedMembers, setSelectedMembers] = useState([]);

  const { showManagementFirst } = useSettings();

  const fetchMembers = async () => {
    try {
      const response = await api.get('/api/members');
      let fetchedMembers = response.data;
  
      if (showManagementFirst) {
        fetchedMembers.sort((a, b) => b.management - a.management);
      }
  
      setMembers(fetchedMembers);
      setFilteredMembers(fetchedMembers);
    } catch (error) {
      console.error('Error fetching members:', error);
    }
  };

  useFocusEffect(
    useCallback(() => {
      fetchMembers();
    }, [showManagementFirst])
  );

  const onViewDetails = (member) => {
    props.navigation.navigate('MemberDetail', { member: member });
  };

  const handleSearch = (text) => {
    setSearchText(text);
    const filtered = members.filter((member) =>
      `${member.name.first} ${member.name.last}`.toLowerCase().includes(text.toLowerCase())
    );
    setFilteredMembers(filtered);
  };

  const clearSearch = () => {
    setSearchText('');
    setFilteredMembers(members);
  };

  const handleAddMember = () => {
    props.navigation.navigate('MemberAdd');
  };

  const toggleSelect = () => {
    setSelectionMode(!selectionMode);
    setSelectedMembers([]); // Clear selection when toggling mode
  };

  const toggleMemberSelection = (id) => {
    setSelectedMembers((prev) =>
      prev.includes(id) ? prev.filter((memberId) => memberId !== id) : [...prev, id]
    );
  };

  const showAlert = (title, message, buttons = []) => {
    if (Platform.OS === 'web') {
      const [okButton] = buttons.filter(button => button.text === 'OK' || button.text === 'Delete');
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

  const handleDelete = () => {
    if (selectedMembers.length === 0) {
      showAlert('No Members Selected', 'Please select members to delete.', [
        {
          text: 'Ok',
          style: 'cancel',
        }
      ]);
      return;
    }
  
    showAlert(
      'Confirm Deletion',
      `Are you sure you want to delete ${selectedMembers.length} selected member(s)?`,
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Delete',
          onPress: async () => {
            try {
              for (const memberId of selectedMembers) {
                await api.delete(`/api/members/${memberId}`);
              }
              showAlert('Success', 'Selected members have been deleted.', [
                {
                  text: 'Ok',
                  style: 'cancel',
                }
              ]);
              fetchMembers();
              setSelectedMembers([]);
              setSelectionMode(false);
            } catch (error) {
              console.error('Error deleting members:', error);
              showAlert('Error', 'Failed to delete members. Please try again.');
            }
          },
          style: 'destructive',
        },
      ]
    );
  };
  

  const renderItem = ({ item }) => (
    <Pressable
      onPress={() => (selectionMode ? toggleMemberSelection(item.id) : onViewDetails(item))}
      activeOpacity={0.8}
    >
      <View
        style={[
          styles.itemContainer,
          item.management
            ? { backgroundColor: isDarkMode ? '#2A3B55' : '#DCEEFB' }
            : { backgroundColor: isDarkMode ? '#444444' : '#ffffff' },
          { borderColor: isDarkMode ? '#333' : '#ddd' },
        ]}
      >
        <Image
          source={
            item.picture && item.picture.trim() !== ''
              ? { uri: item.picture }
              : require('../../assets/icons/no-profile-picture.png')
          }
          style={styles.avatar}
        />
        <View style={styles.infoContainer}>
          <Text style={[styles.name, { color: isDarkMode ? '#ffffff' : '#333' }]}>
            {`${item.name.first} ${item.name.last}`}
          </Text>
          {item.instruments.map((instrument, index) => (
            <Text key={index} style={[styles.instrument, { color: isDarkMode ? '#ccc' : '#666' }]}>
              {instrument}
            </Text>
          ))}
        </View>
        {selectionMode && (
          <Ionicons
            name={selectedMembers.includes(item.id) ? 'checkbox' : 'square-outline'}
            size={24}
            color={isDarkMode ? '#ffffff' : '#333'}
            style={styles.checkbox}
          />
        )}
      </View>
    </Pressable>
  );
  
  

  return (
    <View style={[styles.container, { backgroundColor: isDarkMode ? '#333' : '#f5f5f5' }]}>
      <View style={styles.searchBarContainer}>
        <View style={[styles.searchBarWrapper, { flex: selectionMode ? 0.55 : 0.7 }]} accessible={true}>
          <Image
            source={require('../../assets/icons/magnifying-glass.png')}
            style={styles.searchIcon}
          />
          <TextInput
            style={[styles.searchBar]}
            placeholder="Search by name"
            placeholderTextColor={isDarkMode ? '#888' : '#ccc'}
            value={searchText}
            onChangeText={handleSearch}
            accessibilityLabel='Search bar'
            accessibilityHint='Search bar'
          />

          {searchText !== '' && (
            <Pressable onPress={clearSearch} style={styles.clearButton}>
              <Text style={{ color: isDarkMode ? '#fff' : '#000' }}>✕</Text>
            </Pressable>
          )}
        </View>
        <Pressable onPress={handleAddMember} style={styles.addButton} accessibilityLabel='Add member button'>
          <Ionicons name="add" size={24} color="#fff" />
        </Pressable>
        <Pressable onPress={toggleSelect} style={styles.selectButton} accessibilityLabel='Select member toggle button' accessibilityHint='After pressing this button, you can select members'>
          <Ionicons name="checkbox-outline" size={24} color="#fff" />
        </Pressable>
        {selectionMode && (
          <Pressable onPress={handleDelete} style={styles.deleteButton} accessibilityLabel='Delete member button'>
            <Ionicons name="trash-outline" size={24} color="#fff" />
          </Pressable>
        )}
      </View>
      <FlatList
        data={filteredMembers}
        renderItem={renderItem}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContainer}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  searchBarContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    margin: 16,
  },
  searchBarWrapper: {
    flex: 0.55,
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 8,
    paddingHorizontal: 16,
    borderWidth: 1,
    borderColor: '#ccc',
  },
  searchBar: {
    flex: 1,
    height: 40,
    fontSize: 16,
    marginLeft: 8,
    color: '#000'
  }, 
  searchIcon: {
    width: 20,
    height: 20,
    tintColor: '#888',
  },
  clearButton: {
    marginLeft: 8,
  },
  addButton: {
    flex: 0.15,
    marginLeft: 8,
    height: 40,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#007BFF',
  },
  selectButton: {
    flex: 0.15,
    marginLeft: 8,
    height: 40,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#777777',
  },
  deleteButton: {
    flex: 0.15,
    marginLeft: 8,
    height: 40,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FF5555',
  },
  listContainer: {
    padding: 16,
  },
  itemContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 16,
    borderWidth: 1,
    borderColor: '#ddd',
  },
  avatar: {
    width: 50,
    height: 50,
    borderRadius: 25,
    marginRight: 16,
  },
  infoContainer: {
    flex: 1,
  },
  name: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  instrument: {
    fontSize: 14,
    color: '#666',
  },
  separator: {
    height: 16,
  },
  checkbox: {
    marginLeft: 16,
  },
});

export default MemberList;

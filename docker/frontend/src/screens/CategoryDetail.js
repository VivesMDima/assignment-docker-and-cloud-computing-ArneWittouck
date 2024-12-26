import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, SectionList, Pressable, Image, ActivityIndicator } from 'react-native';
import { useTheme } from '../config/ThemeContext';
import { useFocusEffect } from '@react-navigation/native';
import { api } from '../api/axiosApi';

const CategoryDetail = (props) => {
  const { isDarkMode } = useTheme();
  const { title, description, instruments } = props.route.params.category;

  const [sections, setSections] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchDataForInstruments = async (instruments) => {
    try {
      const safeInstruments = Array.isArray(instruments) ? instruments : [];
      const newSections = [];

      for (const instrument of safeInstruments) {
        const response = await api.get(`/api/members/instrument/${instrument}`);
        newSections.push({
          title: instrument,
          data: response.data || [],
        });
      }

      setSections(newSections);
    } catch (error) {
      console.error('Error fetching data for instruments:', error);
    } finally {
      setLoading(false);
    }
  };

  useFocusEffect(
    useCallback(() => {
      setLoading(true);
      fetchDataForInstruments(instruments);
    }, [instruments])
  );

  const displayMemberDetails = (member) => {
    props.navigation.navigate('MemberDetail', { member: member });
  };

  if (loading) {
    return (
      <View style={[styles.loadingViewContainer, { backgroundColor: isDarkMode ? '#333333' : '#f5f5f5' }]}>
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color={isDarkMode ? '#fff' : '#000'} />
        </View>
      </View>
    );
  }

  return (
    <View style={styles.viewContainer}>
      <SectionList
        sections={sections}
        keyExtractor={(item, index) => item.id || index.toString()}
        renderItem={({ item }) => (
          <Pressable
            style={[styles.memberItem, { backgroundColor: isDarkMode ? '#272727' : '#fff' }]}
            onPress={() => displayMemberDetails(item)}
          >
            <Image
              source={{ uri: item.picture || 'default_placeholder_image_url' }}
              style={styles.memberImage}
            />
            <Text style={[styles.memberText, { color: isDarkMode ? '#ffffff' : '#333' }]}>
              {item.name?.first} {item.name?.last}
            </Text>
          </Pressable>
        )}
        renderSectionHeader={({ section: { title } }) => (
          <Text
            style={[
              styles.sectionHeader,
              { backgroundColor: isDarkMode ? '#444444' : '#e0e0e0' },
              { color: isDarkMode ? '#dddddd' : '#555555' },
            ]}
          >
            {title}
          </Text>
        )}
        ListHeaderComponent={
          <View style={[styles.section, { backgroundColor: isDarkMode ? '#444444' : '#fff' }]} accessible={true}>
            <Text style={[styles.title, { color: isDarkMode ? '#ffffff' : '#333' }]}>{title}</Text>
            <Text style={[styles.description, { color: isDarkMode ? '#dddddd' : '#000000' }]}>
              {description}
            </Text>
          </View>
        }
        contentContainerStyle={[styles.container, { backgroundColor: isDarkMode ? '#333333' : '#f5f5f5' }]}
        stickySectionHeadersEnabled={false}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#f5f5f5',
    flexGrow: 1
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  section: {
    backgroundColor: '#ffffff',
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 4,
    elevation: 4,
  },
  sectionHeader: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#555',
    backgroundColor: '#e0e0e0',
    padding: 12,
    borderRadius: 4,
    marginVertical: 4,
  },
  memberItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#ffffff',
    padding: 12,
    marginVertical: 4,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 1 },
    shadowRadius: 2,
    elevation: 2,
  },
  memberImage: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 12,
  },
  memberText: {
    fontSize: 16,
    fontWeight: '500',
    color: '#333',
  },
  viewContainer: {
    flex: 1
  },
  loadingViewContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingContainer: {
    justifyContent: 'center',
    alignItems: 'center',
  }
});

export default CategoryDetail;

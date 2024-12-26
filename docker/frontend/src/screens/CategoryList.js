import React, { useState, useEffect } from 'react';
import { Text, View, FlatList, StyleSheet, Dimensions, Pressable, Image, Platform } from 'react-native';
import { api } from '../api/axiosApi';
import { getIconMapping } from '../config/IconMapping';
import { useTheme } from '../config/ThemeContext';

const CategoryList = (props) => {
  const { isDarkMode } = useTheme();
  const iconMapping = getIconMapping(isDarkMode);

  const [data, setData] = useState(null);

  const fetchData = async () => {
    try {
      const response = await api.get('/api/categories');
      setData(response.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const windowWidth = Dimensions.get('window').width;

  const calculateTileWidth = () => {
    if (Platform.OS === 'web') {
      const tilesPerRow = 5;
      const margin = 30;
      const totalSpacing = margin * (tilesPerRow - 1);
      const availableWidth = windowWidth - totalSpacing;
      return availableWidth / tilesPerRow;
    }
    const margin = 10;
    const tilesPerRow = 2;
    const totalSpacing = margin * (tilesPerRow - 1);
    const availableWidth = windowWidth - totalSpacing - 40;
    return availableWidth / tilesPerRow;
  };

  const tileWidth = calculateTileWidth();

  const onViewDetails = (category) => {
    props.navigation.navigate('CategoryDetail', { category: category });
  };

  const renderItem = ({ item }) => (
    <Pressable onPress={() => onViewDetails(item)} activeOpacity={0.8}>
      <View
        style={[
          styles.tile,
          {
            backgroundColor: isDarkMode ? '#444444' : '#fff',
            width: tileWidth,
          },
        ]}
      >
        <Image source={iconMapping[item.icon]} style={styles.icon} />
        <Text style={[styles.tileText, { color: isDarkMode ? '#ffffff' : '#333' }]}>{item.title}</Text>
      </View>
    </Pressable>
  );

  return (
    <View style={[styles.container, { backgroundColor: isDarkMode ? '#333333' : '#f5f5f5' }]}>
      <FlatList
        data={data}
        renderItem={renderItem}
        keyExtractor={(item) => item.id}
        numColumns={Platform.OS === 'web' ? 5 : 2} // Adjust number of columns for web and mobile
        columnWrapperStyle={styles.row}
        contentContainerStyle={styles.listContainer}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  listContainer: {
    padding: 15,
  },
  row: {
    justifyContent: 'space-between',
  },
  tile: {
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 10,
    aspectRatio: 1,
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 3,
    shadowOffset: { width: 0, height: 2 },
    marginBottom: 20,
  },
  tileText: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  icon: {
    width: 50,
    height: 50,
    marginBottom: 10,
  },
});

export default CategoryList;

import React from 'react';
import { View, Text, StyleSheet, Switch, Pressable } from 'react-native';
import { useTheme } from '../config/ThemeContext';
import { useSettings } from '../config/SettingsContext';

const Settings = () => {
  const { isDarkMode, toggleTheme } = useTheme();
  const { showManagementFirst, toggleShowManagementFirst } = useSettings();

  return (
    <View style={[styles.container, { backgroundColor: isDarkMode ? '#333' : '#f5f5f5' }]}>
      <Text style={[styles.settingTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
        App Theme
      </Text>
      <View
        style={[
          styles.settingItemContainer,
          { backgroundColor: isDarkMode ? '#444444' : '#ffffff' },
          { borderColor: isDarkMode ? '#333' : '#ddd' },
        ]}
      >
        <Pressable
          style={[styles.themeSwitch, { backgroundColor: isDarkMode ? '#eeeeee' : '#444444' }]}
          onPress={toggleTheme}
        >
          <Text style={[styles.themeText, { color: isDarkMode ? '#000000' : '#ffffff' }]}>
            {isDarkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
          </Text>
        </Pressable>
      </View>

      <Text style={[styles.settingTitle, { color: isDarkMode ? '#ffffff' : '#333' }]}>
        Member Display
      </Text>
      <View
        style={[
          styles.settingItemContainer,
          { backgroundColor: isDarkMode ? '#444444' : '#ffffff' },
          { borderColor: isDarkMode ? '#333' : '#ddd' },
        ]}
      >
        <Text style={[styles.settingLabel, { color: isDarkMode ? '#ffffff' : '#333' }]}>
          Show Management First
        </Text>
        <Switch
          value={showManagementFirst}
          onValueChange={toggleShowManagementFirst}
          trackColor={{ false: '#767577', true: '#81b0ff' }}
          thumbColor={showManagementFirst ? '#007BFF' : '#f4f3f4'}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  settingItemContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 16,
    borderWidth: 1,
    borderColor: '#ddd',
    marginBottom: 8,
  },
  themeSwitch: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 12,
    marginVertical: 4,
    borderRadius: 8,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 1 },
    shadowRadius: 2,
    elevation: 2,
  },
  settingTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
    marginLeft: 8,
  },
  settingLabel: {
    fontSize: 16,
    flex: 1,
  },
  themeText: {
    fontSize: 20,
    fontWeight: 'bold',
  },
});

export default Settings;

// Minstens contrast van 4.5:1 bij kleurcontrasten. (Niet gecontrolleerd door gebrek aan tijd)
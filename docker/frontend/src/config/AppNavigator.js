import React from 'react';
import { View, StyleSheet } from 'react-native';
import { NavigationContainer, DefaultTheme, DarkTheme } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider, SafeAreaView, useSafeAreaInsets } from 'react-native-safe-area-context';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { useTheme } from '../config/ThemeContext.js';
import MemberList from '../screens/MemberList.js';
import CategoryList from '../screens/CategoryList.js';
import Settings from '../screens/Settings.js';
import MemberDetail from '../screens/MemberDetail.js';
import MemberAdd from '../screens/MemberAdd.js';
import CategoryDetail from '../screens/CategoryDetail.js';

const Tab = createBottomTabNavigator();

export default function AppNavigator() {
    const { isDarkMode } = useTheme();

    const screenOptions = {
        headerStyle: {
            backgroundColor: isDarkMode ? '#222' : '#fff',
        },
        headerTintColor: isDarkMode ? '#fff' : '#000',
        headerTitleStyle: {
            fontSize: 18,
        },
    };

    return (
        <SafeAreaProvider>
            <NavigationContainer theme={isDarkMode ? DarkTheme : DefaultTheme}>
                <Tab.Navigator
                    screenOptions={({ route }) => ({
                        tabBarStyle: [
                            {
                                height: 75, // Base height
                                paddingBottom: 20, // Extra padding
                                backgroundColor: isDarkMode ? '#222' : '#fff',
                            },
                        ],
                        tabBarLabelStyle: {
                            fontSize: 11,
                            color: isDarkMode ? '#fff' : '#000',
                        },
                        tabBarIconStyle: {
                            size: 30,
                        },
                        headerStyle: {
                            backgroundColor: isDarkMode ? '#222' : '#fff',
                        },
                        headerTintColor: isDarkMode ? '#fff' : '#000',
                    })}
                >
                    <Tab.Screen
                        name="Members"
                        component={MemberListNavigator}
                        options={{
                            tabBarLabel: 'Members',
                            tabBarIcon: ({ color, size }) => (
                                <Ionicons name="person" color={color} size={size} />
                            ),
                            headerShown: false,
                            tabBarAccessibilityLabel: 'Member list'
                        }}
                    />
                    <Tab.Screen
                        name="Categories"
                        component={CategoryListNavigator}
                        options={{
                            tabBarLabel: 'Categories',
                            tabBarIcon: ({ color, size }) => (
                                <Ionicons name="list" color={color} size={size} />
                            ),
                            headerShown: false,
                            tabBarAccessibilityLabel: 'Category list'
                        }}
                    />
                    <Tab.Screen
                        name="Settings"
                        component={Settings}
                        options={{
                            tabBarLabel: 'Settings',
                            tabBarIcon: ({ color, size }) => (
                                <Ionicons name="settings" color={color} size={size} />
                            ),
                            tabBarAccessibilityLabel: 'Settings'
                        }}
                    />
                </Tab.Navigator>
            </NavigationContainer>
        </SafeAreaProvider>
    );
}

const MemberListStack = createNativeStackNavigator();

export const MemberListNavigator = () => {
    const { isDarkMode } = useTheme();

    return (
        <MemberListStack.Navigator
            screenOptions={{
                headerStyle: { backgroundColor: isDarkMode ? '#222' : '#fff' },
                headerTintColor: isDarkMode ? '#fff' : '#000',
            }}
        >
            <MemberListStack.Screen
                name="MemberList"
                component={MemberList}
                options={{
                    headerTitle: 'Members',
                    headerAccessibilityLabel: 'Member list screen',
                }}
            />
            <MemberListStack.Screen
                name="MemberDetail"
                component={MemberDetail}
                options={{ title: 'Member Details' }}
            />
            <MemberListStack.Screen
                name="MemberAdd"
                component={MemberAdd}
                options={{ title: 'Add/Update Member' }}
            />
        </MemberListStack.Navigator>
    );
};

const CategoryListStack = createNativeStackNavigator();

export const CategoryListNavigator = () => {
    const { isDarkMode } = useTheme();

    return (
        <CategoryListStack.Navigator
            screenOptions={{
                headerStyle: { backgroundColor: isDarkMode ? '#222' : '#fff' },
                headerTintColor: isDarkMode ? '#fff' : '#000',
            }}
        >
            <CategoryListStack.Screen
                name="CategoryList"
                component={CategoryList}
                options={{ title: 'Categories' }}
            />
            <CategoryListStack.Screen
                name="CategoryDetail"
                component={CategoryDetail}
                options={{ title: 'Category Details' }}
            />
            <CategoryListStack.Screen
                name="MemberDetail"
                component={MemberDetail}
                options={{ title: 'Member Details' }}
            />
            <CategoryListStack.Screen
                name="MemberAdd"
                component={MemberAdd}
                options={{ title: 'Update Member' }}
            />
        </CategoryListStack.Navigator>
    );
};

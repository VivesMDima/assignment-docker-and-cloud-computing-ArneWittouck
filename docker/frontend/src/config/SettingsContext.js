import React, { createContext, useState, useContext } from 'react';

const SettingsContext = createContext();

export const SettingsProvider = ({ children }) => {
  const [showManagementFirst, setShowManagementFirst] = useState(false);

  const toggleShowManagementFirst = () => {
    setShowManagementFirst((prev) => !prev);
  };

  return (
    <SettingsContext.Provider value={{ showManagementFirst, toggleShowManagementFirst }}>
      {children}
    </SettingsContext.Provider>
  );
};

export const useSettings = () => useContext(SettingsContext);

import AppNavigator from "./src/config/AppNavigator";
import { SettingsProvider } from './src/config/SettingsContext';
import { ThemeProvider } from "./src/config/ThemeContext";

export default function App() {
  return (
    <SettingsProvider>
      <ThemeProvider>
        <AppNavigator/>
      </ThemeProvider>
    </SettingsProvider>
  );
}


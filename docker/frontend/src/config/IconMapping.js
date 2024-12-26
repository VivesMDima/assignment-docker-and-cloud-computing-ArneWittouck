import { useTheme } from '../config/ThemeContext';

export const getIconMapping = () => {
  const { isDarkMode } = useTheme();
  if (isDarkMode) {
    return {
      violin: require('../../assets/icons/violin_white.png'),
      trumpet: require('../../assets/icons/trumpet_white.png'),
      drum: require('../../assets/icons/drum_white.png'),
      flute: require('../../assets/icons/flute_white.png'),
      keyboard: require('../../assets/icons/keyboard_white.png'),
      management: require('../../assets/icons/management_white.png'),
    };
  } else {
    return {
      violin: require('../../assets/icons/violin.png'),
      trumpet: require('../../assets/icons/trumpet.png'),
      drum: require('../../assets/icons/drum.png'),
      flute: require('../../assets/icons/flute.png'),
      keyboard: require('../../assets/icons/keyboard.png'),
      management: require('../../assets/icons/management.png'),
    };
  }
};
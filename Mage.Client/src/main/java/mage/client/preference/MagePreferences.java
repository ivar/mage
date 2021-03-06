package mage.client.preference;

import java.util.prefs.Preferences;
import mage.client.MageFrame;

// TODO: Move all preference related logic from MageFrame and PreferencesDialog to this class.
public class MagePreferences {

    private static final String KEY_SERVER_ADDRESS = "serverAddress";
    private static final String KEY_SERVER_PORT = "serverPort";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AUTO_CONNECT = "autoConnect";

    private static Preferences prefs() {
        // TODO: Move MageFrame.prefs to this class.
        return MageFrame.getPreferences();
    }

    public static String getServerAddress() {
        return prefs().get(KEY_SERVER_ADDRESS, "");
    }

    public static String getServerAddressWithDefault(String defaultValue) {
        return prefs().get(KEY_SERVER_ADDRESS, defaultValue);
    }

    public static void setServerAddress(String serverAddress) {
        prefs().put(KEY_SERVER_ADDRESS, serverAddress);
    }

    public static int getServerPort() {
        return prefs().getInt(KEY_SERVER_PORT, 0);
    }

    public static int getServerPortWithDefault(int defaultValue) {
        return prefs().getInt(KEY_SERVER_PORT, defaultValue);
    }

    public static void setServerPort(int port) {
        prefs().putInt(KEY_SERVER_PORT, port);
    }

    private static String prefixedKey(String prefix, String key) {
        return prefix + '/' + key;
    }

    public static String getUserName(String serverAddress) {
        String userName = prefs().get(prefixedKey(serverAddress, KEY_USER_NAME), "");
        if (!userName.isEmpty()) {
            return userName;
        }
        // For clients older than 1.4.7, userName is stored without a serverAddress prefix.
        return prefs().get(KEY_USER_NAME, "");
    }

    public static void setUserName(String serverAddress, String userName) {
        prefs().put(prefixedKey(serverAddress, KEY_USER_NAME), userName);
    }

    public static String getPassword(String serverAddress) {
        return prefs().get(prefixedKey(serverAddress, KEY_PASSWORD), "");
    }

    public static void setPassword(String serverAddress, String password) {
        prefs().put(prefixedKey(serverAddress, KEY_PASSWORD), password);
    }

    public static String getEmail(String serverAddress) {
        return prefs().get(prefixedKey(serverAddress, KEY_EMAIL), "");
    }

    public static void setEmail(String serverAddress, String userName) {
        prefs().put(prefixedKey(serverAddress, KEY_EMAIL), userName);
    }

    public static boolean getAutoConnect() {
        return prefs().getBoolean(KEY_AUTO_CONNECT, false);
    }

    public static void setAutoConnect(boolean autoConnect) {
        prefs().putBoolean(KEY_AUTO_CONNECT, autoConnect);
    }
}

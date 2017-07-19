package edu.cmu.chimps.iamhome.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.exceptions.PSException;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.device.WifiAp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.chimps.iamhome.MyApplication;

public class WifiUtils {

    public static final String KEY_WIFI_SENSING = "key_wifi_sensing";
    public static final String KEY_USER_HOME_BSSID_LIST  = "key_wifi_bssid_list";

    /**
     * Store all user's wifi BSSIDs.
     */

    public static void storeUsersHomeWifi() throws PSException {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEY_WIFI_SENSING, Context.MODE_PRIVATE);
        preferences.edit().putStringSet(KEY_USER_HOME_BSSID_LIST, new HashSet<>(getBSSIDList())).apply();
    }

    /**
     * Get all BSSIDs that are associated with user home wifi.
     * @return
     */
    public static Set<String> getUsersHomeWifiList(){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEY_WIFI_SENSING, Context.MODE_PRIVATE);
        return preferences.getStringSet(KEY_USER_HOME_BSSID_LIST, new HashSet<String>());
    }

    //

    /**
     * Get the connected WiFi BSSID
     * @return the BSSID
     * @throws PSException
     */
    public String getConnectedWifiBSSID() throws PSException {
        UQI uqi = new UQI(MyApplication.getContext());
        Item wifiItem = uqi.getData(WifiAp.getScanResults(), Purpose.FEATURE("Get Connected Wifi BSSID"))
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .limit(1).getFirst().asItem();
        if(wifiItem !=null){
            return wifiItem.getValueByField(WifiAp.BSSID).toString();
        }

        return null;
    }

    /**
     * Check whether user has connected to a wifi
     * @return
     * @throws PSException
     */
    public static Boolean isConnectedToWifi() throws PSException {
        UQI uqi = new UQI(MyApplication.getContext());
        return uqi.getData(WifiAp.getScanResults(), Purpose.FEATURE("Check Whether the Phone is Connected "))
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .count() > 0;
    }

    /**
     * Get all related BSSIDs of the user connected wifi;
     * @throws PSException
     */
    public static List<String> getBSSIDList() throws PSException {

        UQI uqi = new UQI(MyApplication.getContext());
        String ssid = uqi.getData(WifiAp.getScanResults(), Purpose.FEATURE("Get access to the SSID of connected Wifi"))
                .filter(WifiAp.STATUS, WifiAp.STATUS_CONNECTED)
                .getFirst().getField(WifiAp.SSID).toString();

        return uqi.getData(WifiAp.getScanResults(), Purpose.FEATURE("Get access to all related BSSIDs of the connected Wifi"))
                .filter(WifiAp.SSID, ssid).asList(WifiAp.BSSID);
    }

}
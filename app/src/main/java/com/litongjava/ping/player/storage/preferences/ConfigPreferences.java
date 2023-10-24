package com.litongjava.ping.player.storage.preferences;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.litongjava.ping.player.constants.PreferenceName;
import com.litongjava.ping.player.services.DarkModeService;
import com.litongjava.ping.player.ui.R;


/**
 * SharedPreferences工具类
 * Created by wcy on 2015/11/28.
 */
public class ConfigPreferences {

  private static final IPreferencesFile preferencesFile = new PreferencesFile(Utils.getApp(), PreferenceName.CONFIG, false);

  public static String getFilterSize() {
    return preferencesFile.getString(StringUtils.getString(R.string.setting_key_filter_size), "0");
  }

  public static void setFilterSize(String value) {
    preferencesFile.putString(StringUtils.getString(R.string.setting_key_filter_size), value);
  }

  public static String getFilterTime() {
    return preferencesFile.getString(StringUtils.getString(R.string.setting_key_filter_time), "0");
  }

  public static void setFilterTime(String value) {
    preferencesFile.putString(StringUtils.getString(R.string.setting_key_filter_time), value);
  }

  public static String getDarkMode() {
    return preferencesFile.getString("dark_mode", DarkModeService.DarkMode.Auto.getValue());
  }

  public static void setDarkMode(String value) {
    preferencesFile.putString("dark_mode", value);
  }

  public static int getPlayMode() {
    return preferencesFile.getInt("play_mode", 0);
  }

  public static void setPlayMode(int value) {
    preferencesFile.putInt("play_mode", value);
  }

  public static String getCurrentSongId() {
    return preferencesFile.getString("current_song_id", "");
  }

  public static void setCurrentSongId(String value) {
    preferencesFile.putString("current_song_id", value);
  }

  public static String getApiDomain() {
    return preferencesFile.getString("api_domain", "");
  }

  public static void setApiDomain(String value) {
    preferencesFile.putString("api_domain", value);
  }
}

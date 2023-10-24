package com.litongjava.ping.player.services;

import androidx.appcompat.app.AppCompatDelegate;

import com.blankj.utilcode.util.ActivityUtils;
import com.litongjava.ping.player.storage.preferences.ConfigPreferences;

import android.content.res.Configuration;


public class DarkModeService {


  public DarkModeService() {
  }

  public void init() {
    setDarkModeInternal(DarkMode.fromValue(ConfigPreferences.getDarkMode()));
  }

  public void setDarkMode(DarkMode mode) {
    if (!mode.getValue().equals(ConfigPreferences.getDarkMode())) {
      ConfigPreferences.setDarkMode(mode.getValue());
      setDarkModeInternal(mode);
    }
  }

  private void setDarkModeInternal(DarkMode mode) {
    AppCompatDelegate.setDefaultNightMode(mode.getSystemValue());
  }

  public boolean isDarkMode() {
    android.content.Context context = ActivityUtils.getTopActivity();
    if (context == null) {
      return false;
    }
    int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
  }

  public enum DarkMode {
    Auto("0", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    Light("1", AppCompatDelegate.MODE_NIGHT_NO),
    Dark("2", AppCompatDelegate.MODE_NIGHT_YES);

    private final String value;
    private final int systemValue;

    DarkMode(String value, int systemValue) {
      this.value = value;
      this.systemValue = systemValue;
    }

    public String getValue() {
      return value;
    }

    public int getSystemValue() {
      return systemValue;
    }

    public static DarkMode fromValue(String value) {
      switch (value) {
        case "1":
          return Light;
        case "2":
          return Dark;
        default:
          return Auto;
      }
    }
  }
}

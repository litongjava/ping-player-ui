package com.litongjava.ping.player.constants;

/**
 * Created by wangchenyan.top on 2023/4/19.
 */
public class PreferenceName {

  public static final String ACCOUNT = assemble("account");
  public static final String CONFIG = assemble("config");
  public static final String SEARCH = assemble("search");

  private static String assemble(String input) {
    return "music_" + input;
  }

  // Prevent instantiation
  private PreferenceName() {
  }
}

package com.litongjava.ping.player.storage.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.litongjava.ping.player.utils.DES;
import com.litongjava.ping.player.utils.GsonUtils;
import com.litongjava.ping.player.utils.Md5Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PreferencesFile implements IPreferencesFile {

  private SharedPreferences sp;
  private boolean encrypt;

  public PreferencesFile(Context context, String name, boolean encrypt) {
    this.encrypt = encrypt;
    this.sp = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
  }


  @Override
  public void remove(String key) {
    String realKey = encrypt ? Md5Utils.md5(key,true) : key;
    sp.edit().remove(realKey).apply();
  }

  @Override
  public void remove(List<String> keys) {
    Set<String> realKeys = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      realKeys = encrypt ? keys.stream().map(Md5Utils::md5).collect(Collectors.toSet()) : new HashSet<>(keys);
    }
    SharedPreferences.Editor editor = sp.edit();
    for (String key : realKeys) {
      editor.remove(key);
    }
    editor.apply();
  }

  @Override
  public void removeExcept(List<String> exceptKeys) {
    Set<String> realExceptKeys = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      realExceptKeys = encrypt ? exceptKeys.stream().map(Md5Utils::md5).collect(Collectors.toSet()) : new HashSet<>(exceptKeys);
    }
    Set<String> keys = sp.getAll().keySet();
    SharedPreferences.Editor editor = sp.edit();
    for (String key : keys) {
      if (!realExceptKeys.contains(key)) {
        editor.remove(key);
      }
    }
    editor.apply();
  }

  @Override
  public void clear() {
    sp.edit().clear().apply();
  }

  @Override
  public boolean getBoolean(String key, boolean defValue) {
    return get(key, defValue, Boolean.class);
  }

  @Override
  public void putBoolean(String key, boolean value) {
    put(key, value, Boolean.class);
  }

  @Override
  public int getInt(String key, int defValue) {
    return get(key, defValue, Integer.class);
  }

  @Override
  public void putInt(String key, int value) {
    put(key, value, Integer.class);
  }

  @Override
  public long getLong(String key, long defValue) {
    return sp.getLong(key, defValue); // You'll need to add encryption support if required.
  }

  @Override
  public void putLong(String key, long value) {
    sp.edit().putLong(key, value).apply(); // You'll need to add encryption support if required.
  }

  @Override
  public float getFloat(String key, float defValue) {
    return sp.getFloat(key, defValue); // You'll need to add encryption support if required.
  }

  @Override
  public void putFloat(String key, float value) {
    sp.edit().putFloat(key, value).apply(); // You'll need to add encryption support if required.
  }

  @Override
  public String getString(String key, String defValue) {
    return sp.getString(key, defValue); // You'll need to add encryption support if required.
  }

  @Override
  public void putString(String key, String value) {
    sp.edit().putString(key, value).apply(); // You'll need to add encryption support if required.
  }

  @Override
  public <T> T getModel(String key, Class<T> clazz) {
    String json = getString(key, "");
    if (!json.isEmpty()) {
      return GsonUtils.fromJson(json, clazz); // Assuming you have a fromJson method in GsonUtils.
    }
    return null;
  }

  @Override
  public <T> void putModel(String key, T t) {
    if (t == null) {
      remove(key);
    } else {
      putString(key, GsonUtils.toJson(t)); // Assuming you have a toJson method in GsonUtils.
    }
  }

  @Override
  public <T> List<T> getList(String key, Class<T> clazz) {
    String json = getString(key, "");
    if (!json.isEmpty()) {
      return GsonUtils.fromJsonList(json, clazz); // Assuming you have a fromJsonList method in GsonUtils.
    }
    return null;
  }

  @Override
  public <T> void putList(String key, List<T> list) {
    putModel(key, list);
  }

  @SuppressWarnings("unchecked")
  private <T> T get(String key, T defValue, Class<T> type) {
    if (encrypt) {
      String encryptValue = sp.getString(Md5Utils.md5(key, true), null);
      if (encryptValue == null || encryptValue.isEmpty()) {
        return defValue;
      }
      String decryptValue = DES.decrypt(encryptKey, encryptValue);
      try {
        if (type == Boolean.class) {
          return (T) Boolean.valueOf(decryptValue);
        } else if (type == Integer.class) {
          return (T) Integer.valueOf(decryptValue);
        } else if (type == Long.class) {
          return (T) Long.valueOf(decryptValue);
        } else if (type == Float.class) {
          return (T) Float.valueOf(decryptValue);
        } else if (type == String.class) {
          return (T) decryptValue;
        } else {
          throw new IllegalArgumentException("不支持的类型");
        }
      } catch (NumberFormatException e) {
        e.printStackTrace();
        return defValue;
      }
    } else {
      if (type == Boolean.class) {
        return (T) Boolean.valueOf(sp.getBoolean(key, (Boolean) defValue));
      } else if (type == Integer.class) {
        return (T) Integer.valueOf(sp.getInt(key, (Integer) defValue));
      } else if (type == Long.class) {
        return (T) Long.valueOf(sp.getLong(key, (Long) defValue));
      } else if (type == Float.class) {
        return (T) Float.valueOf(sp.getFloat(key, (Float) defValue));
      } else if (type == String.class) {
        return (T) sp.getString(key, (String) defValue);
      } else {
        throw new IllegalArgumentException("不支持的类型");
      }
    }
  }

  private <T> void put(String key, T value, Class<T> type) {
    if (encrypt) {
      String encryptValue;
      if (type == Boolean.class || type == Integer.class || type == Long.class || type == Float.class || type == String.class) {
        encryptValue = DES.encrypt(encryptKey, value.toString());
      } else {
        throw new IllegalArgumentException("不支持的类型");
      }
      sp.edit().putString(Md5Utils.md5(key, true), encryptValue).apply();
    } else {
      if (type == Boolean.class) {
        sp.edit().putBoolean(key, (Boolean) value).apply();
      } else if (type == Integer.class) {
        sp.edit().putInt(key, (Integer) value).apply();
      } else if (type == Long.class) {
        sp.edit().putLong(key, (Long) value).apply();
      } else if (type == Float.class) {
        sp.edit().putFloat(key, (Float) value).apply();
      } else if (type == String.class) {
        sp.edit().putString(key, (String) value).apply();
      } else {
        throw new IllegalArgumentException("不支持的类型");
      }
    }
  }

  private static final String encryptKey = getEncryptKey();

  private static String getEncryptKey() {
    String androidId = DeviceUtils.getAndroidID();
    String key = (androidId == null || androidId.isEmpty()) ? AppUtils.getAppPackageName() : androidId;
    return Md5Utils.md5(key, true).substring(0, 8);
  }
}

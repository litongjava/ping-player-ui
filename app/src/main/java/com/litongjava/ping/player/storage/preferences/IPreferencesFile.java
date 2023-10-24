package com.litongjava.ping.player.storage.preferences;

import java.util.List;

public interface IPreferencesFile {

  void remove(String key);

  void remove(List<String> keys);

  void removeExcept(List<String> exceptKeys);

  void clear();

  boolean getBoolean(String key, boolean defValue);

  void putBoolean(String key, boolean value);

  int getInt(String key, int defValue);

  void putInt(String key, int value);

  long getLong(String key, long defValue);

  void putLong(String key, long value);

  float getFloat(String key, float defValue);

  void putFloat(String key, float value);

  String getString(String key, String defValue);

  void putString(String key, String value);

  <T> T getModel(String key, Class<T> clazz);

  <T> void putModel(String key, T t);

  <T> List<T> getList(String key, Class<T> clazz);

  <T> void putList(String key, List<T> list);

  class BooleanProperty {
    private final String key;
    private final boolean defValue;

    public BooleanProperty(String key, boolean defValue) {
      this.key = key;
      this.defValue = defValue;
    }

    public boolean getValue(IPreferencesFile thisRef) {
      return thisRef.getBoolean(key, defValue);
    }

    public void setValue(IPreferencesFile thisRef, boolean value) {
      thisRef.putBoolean(key, value);
    }
  }
  class IntProperty {
    private final String key;
    private final int defValue;

    public IntProperty(String key, int defValue) {
      this.key = key;
      this.defValue = defValue;
    }

    public int getValue(IPreferencesFile thisRef) {
      return thisRef.getInt(key, defValue);
    }

    public void setValue(IPreferencesFile thisRef, int value) {
      thisRef.putInt(key, value);
    }
  }

  class LongProperty {
    private final String key;
    private final long defValue;

    public LongProperty(String key, long defValue) {
      this.key = key;
      this.defValue = defValue;
    }

    public long getValue(IPreferencesFile thisRef) {
      return thisRef.getLong(key, defValue);
    }

    public void setValue(IPreferencesFile thisRef, long value) {
      thisRef.putLong(key, value);
    }
  }

  class FloatProperty {
    private final String key;
    private final float defValue;

    public FloatProperty(String key, float defValue) {
      this.key = key;
      this.defValue = defValue;
    }

    public float getValue(IPreferencesFile thisRef) {
      return thisRef.getFloat(key, defValue);
    }

    public void setValue(IPreferencesFile thisRef, float value) {
      thisRef.putFloat(key, value);
    }
  }

  class StringProperty {
    private final String key;
    private final String defValue;

    public StringProperty(String key, String defValue) {
      this.key = key;
      this.defValue = defValue;
    }

    public String getValue(IPreferencesFile thisRef) {
      return thisRef.getString(key, defValue);
    }

    public void setValue(IPreferencesFile thisRef, String value) {
      thisRef.putString(key, value);
    }
  }

  class ObjectProperty<T> {
    private final String key;
    private final Class<T> clazz;

    public ObjectProperty(String key, Class<T> clazz) {
      this.key = key;
      this.clazz = clazz;
    }

    public T getValue(IPreferencesFile thisRef) {
      return thisRef.getModel(key, clazz);
    }

    public void setValue(IPreferencesFile thisRef, T value) {
      thisRef.putModel(key, value);
    }
  }

  class ListProperty<T> {
    private final String key;
    private final Class<T> clazz;

    public ListProperty(String key, Class<T> clazz) {
      this.key = key;
      this.clazz = clazz;
    }

    public List<T> getValue(IPreferencesFile thisRef) {
      return thisRef.getList(key, clazz);
    }

    public void setValue(IPreferencesFile thisRef, List<T> value) {
      thisRef.putList(key, value);
    }
  }


}

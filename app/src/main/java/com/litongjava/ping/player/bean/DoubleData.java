package com.litongjava.ping.player.bean;

public class DoubleData<T1, T2> {
  private T1 t1;
  private T2 t2;

  public DoubleData() {
  }

  public DoubleData(T1 t1, T2 t2) {
    this.t1 = t1;
    this.t2 = t2;
  }

  public T1 getT1() {
    return t1;
  }

  public T2 getT2() {
    return t2;
  }

  public void setT1(T1 t1) {
    this.t1 = t1;
  }

  public void setT2(T2 t2) {
    this.t2 = t2;
  }

  @Override
  public String toString() {
    return "DoubleData{" +
      "t1=" + t1 +
      ", t2=" + t2 +
      '}';
  }
}

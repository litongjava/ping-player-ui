package com.litongjava.ping.player.model;

public class CommonResult<T> {

  private int code;
  private String msg;
  private T data;

  public CommonResult(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  public T getData() {
    return data;
  }

  public boolean isSuccess() {
    return code == 200;
  }

  public boolean isSuccessWithData() {
    return code == 200 && data != null;
  }

  public T getDataOrThrow() {
    if (data == null) {
      throw new NullPointerException("Data is null");
    }
    return data;
  }

  public static <T> CommonResult<T> success(T data) {
    return new CommonResult<>(200, null, data);
  }

  public static <T> CommonResult<T> fail() {
    return fail(-1, null);
  }

  public static <T> CommonResult<T> fail(int code, String msg) {
    int correctedCode = (code == 200) ? -1 : code;
    return new CommonResult<>(correctedCode, msg, null);
  }
}

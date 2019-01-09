package org.knowm.xchange.nicehash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NicehashException extends RuntimeException {

  private final int code;

  public NicehashException(@JsonProperty("code") int code, @JsonProperty("msg") String msg) {
    super(msg);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}

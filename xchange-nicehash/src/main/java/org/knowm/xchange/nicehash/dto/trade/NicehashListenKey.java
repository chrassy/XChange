package org.knowm.xchange.nicehash.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NicehashListenKey {

  private String listenKey;

  public NicehashListenKey(@JsonProperty("listenKey") String listenKey) {
    this.listenKey = listenKey;
  }

  public String getListenKey() {
    return listenKey;
  }

  public void setListenKey(String listenKey) {
    this.listenKey = listenKey;
  }
}

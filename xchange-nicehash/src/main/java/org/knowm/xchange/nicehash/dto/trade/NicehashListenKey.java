package org.knowm.xchange.nicehash.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Created by cyril on 11-Oct-17. */
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

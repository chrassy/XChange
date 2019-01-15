package org.knowm.xchange.nicehash.dto.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class NicehashTime {

  @JsonProperty private long serverTime;

  public Date getServerTime() {
    return new Date(serverTime);
  }
}

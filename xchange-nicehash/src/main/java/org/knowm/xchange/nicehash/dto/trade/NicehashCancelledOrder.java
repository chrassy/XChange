package org.knowm.xchange.nicehash.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class NicehashCancelledOrder {

  public final String symbol;
  public final String origClientOrderId;
  public final long orderId;
  public final String clientOrderId;

  public NicehashCancelledOrder(
      @JsonProperty("symbol") String symbol,
      @JsonProperty("origClientOrderId") String origClientOrderId,
      @JsonProperty("orderId") long orderId,
      @JsonProperty("clientOrderId") String clientOrderId) {
    super();
    this.symbol = symbol;
    this.origClientOrderId = origClientOrderId;
    this.orderId = orderId;
    this.clientOrderId = clientOrderId;
  }
}

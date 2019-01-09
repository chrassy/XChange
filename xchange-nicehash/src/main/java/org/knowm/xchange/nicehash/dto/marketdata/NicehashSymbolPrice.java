package org.knowm.xchange.nicehash.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class NicehashSymbolPrice {

  public final String symbol;
  public final BigDecimal price;

  public NicehashSymbolPrice(
      @JsonProperty("symbol") String symbol, @JsonProperty("price") BigDecimal price) {
    this.symbol = symbol;
    this.price = price;
  }
}

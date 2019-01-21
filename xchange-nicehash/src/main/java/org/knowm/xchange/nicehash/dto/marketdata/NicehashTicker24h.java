package org.knowm.xchange.nicehash.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.nicehash.NicehashAdapters;

public final class NicehashTicker24h {

  private final BigDecimal priceChange;
  private final BigDecimal priceChangePercent;
  private final BigDecimal weightedAvgPrice;
  private final BigDecimal prevClosePrice;
  private final BigDecimal bidPrice;
  private final BigDecimal bidQuantity;
  private final BigDecimal askPrice;
  private final BigDecimal askQuantity;
  private final BigDecimal openPrice;
  private final BigDecimal highPrice;
  private final BigDecimal lowPrice;
  private final BigDecimal volume;
  private final BigDecimal quoteVolume;
  private final long openTime;
  private final long closeTime;
  private final long firstNumber;
  private final long lastNumber;
  private final long count;
  private final String symbol;

  // The curency pair that is unfortunately not returned in the response
  private CurrencyPair pair;

  // The cached ticker
  private Ticker ticker;

  public NicehashTicker24h(
      @JsonProperty("priceChange") BigDecimal priceChange,
      @JsonProperty("priceChangePercent") BigDecimal priceChangePercent,
      @JsonProperty("weightedAvgPrice") BigDecimal weightedAvgPrice,
      @JsonProperty("prevClosePrice") BigDecimal prevClosePrice,
      @JsonProperty("bidPrice") BigDecimal bidPrice,
      @JsonProperty("bidQuantity") BigDecimal bidQuantity,
      @JsonProperty("askPrice") BigDecimal askPrice,
      @JsonProperty("askQuantity") BigDecimal askQuantity,
      @JsonProperty("openPrice") BigDecimal openPrice,
      @JsonProperty("highPrice") BigDecimal highPrice,
      @JsonProperty("lowPrice") BigDecimal lowPrice,
      @JsonProperty("volume") BigDecimal volume,
      @JsonProperty("quoteVolume") BigDecimal quoteVolume,
      @JsonProperty("openTime") long openTime,
      @JsonProperty("closeTime") long closeTime,
      @JsonProperty("firstNumber") long firstNumber,
      @JsonProperty("lastNumber") long lastNumber,
      @JsonProperty("count") long count,
      @JsonProperty("symbol") String symbol) {
    this.priceChange = priceChange;
    this.priceChangePercent = priceChangePercent;
    this.weightedAvgPrice = weightedAvgPrice;
    this.prevClosePrice = prevClosePrice;
    this.bidPrice = bidPrice;
    this.bidQuantity = bidQuantity;
    this.askPrice = askPrice;
    this.askQuantity = askQuantity;
    this.openPrice = openPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.volume = volume;
    this.quoteVolume = quoteVolume;
    this.openTime = openTime;
    this.closeTime = closeTime;
    this.firstNumber = firstNumber;
    this.lastNumber = lastNumber;
    this.count = count;
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  public CurrencyPair getCurrencyPair() {
    return pair;
  }

  public void setCurrencyPair(CurrencyPair pair) {
    this.pair = pair;
  }

  public BigDecimal getPriceChange() {
    return priceChange;
  }

  public BigDecimal getPriceChangePercent() {
    return priceChangePercent;
  }

  public BigDecimal getWeightedAvgPrice() {
    return weightedAvgPrice;
  }

  public BigDecimal getPrevClosePrice() {
    return prevClosePrice;
  }

  public BigDecimal getBidPrice() {
    return bidPrice;
  }

  public BigDecimal getBidQuantity() {
    return bidQuantity;
  }

  public BigDecimal getAskPrice() {
    return askPrice;
  }

  public BigDecimal getAskQuantity() {
    return askQuantity;
  }

  public BigDecimal getOpenPrice() {
    return openPrice;
  }

  public BigDecimal getHighPrice() {
    return highPrice;
  }

  public BigDecimal getLowPrice() {
    return lowPrice;
  }

  public BigDecimal getVolume() {
    return volume;
  }

  public BigDecimal getQuoteVolume() {
    return quoteVolume;
  }

  public long getFirstTradeId() {
    return firstNumber;
  }

  public long getLastTradeId() {
    return lastNumber;
  }

  public long getTradeCount() {
    return count;
  }

  public Date getOpenTime() {
    return new Date(openTime);
  }

  public Date getCloseTime() {
    return new Date(closeTime);
  }

  public synchronized Ticker toTicker() {
    CurrencyPair currencyPair = pair;
    if (currencyPair == null) {
      currencyPair = NicehashAdapters.adaptSymbol(symbol);
    }
    if (ticker == null) {
      ticker =
          new Ticker.Builder()
              .currencyPair(currencyPair)
              .open(openPrice)
              .ask(askPrice)
              .bid(bidPrice)
              .high(highPrice)
              .low(lowPrice)
              .volume(volume)
              .vwap(weightedAvgPrice)
              .askSize(askQuantity)
              .bidSize(bidQuantity)
              .quoteVolume(quoteVolume)
              .build();
    }
    return ticker;
  }
}

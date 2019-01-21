package org.knowm.xchange.nicehash.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.nicehash.NicehashAdapters;
import org.knowm.xchange.nicehash.dto.marketdata.*;
import org.knowm.xchange.utils.StreamUtils;

public class NicehashMarketDataServiceRaw extends NicehashBaseService {

  protected NicehashMarketDataServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public void ping() throws IOException {
    nicehash.ping();
  }

  public NicehashOrderbook getNicehashOrderbook(CurrencyPair pair, Integer limit)
      throws IOException {
    return nicehash.depth(NicehashAdapters.toSymbol(pair), limit);
  }

  public List<NicehashAggTrades> aggTrades(
      CurrencyPair pair, Long fromId, Long startTime, Long endTime, Integer limit)
      throws IOException {
    return nicehash.aggTrades(NicehashAdapters.toSymbol(pair), fromId, startTime, endTime, limit);
  }

  public NicehashKline lastKline(CurrencyPair pair, KlineInterval interval) throws IOException {
    return klines(pair, interval, 1, null, null).stream().collect(StreamUtils.singletonCollector());
  }

  public List<NicehashKline> klines(CurrencyPair pair, KlineInterval interval) throws IOException {
    return klines(pair, interval, null, null, null);
  }

  public List<NicehashKline> klines(
      CurrencyPair pair, KlineInterval interval, Integer limit, Long startTime, Long endTime)
      throws IOException {
    List<Object[]> raw =
        nicehash.klines(
            NicehashAdapters.toSymbol(pair), interval.code(), limit, startTime, endTime);
    return raw.stream()
        .map(obj -> new NicehashKline(pair, interval, obj))
        .collect(Collectors.toList());
  }

  public List<NicehashTicker24h> ticker24h() throws IOException {
    List<NicehashTicker24h> nicehashTicker24hList = nicehash.ticker24h();
    return nicehashTicker24hList;
  }

  public NicehashTicker24h ticker24h(CurrencyPair pair) throws IOException {
    NicehashTicker24h ticker24h = nicehash.ticker24h(NicehashAdapters.toSymbol(pair));
    ticker24h.setCurrencyPair(pair);
    return ticker24h;
  }

  public NicehashPrice tickerPrice(CurrencyPair pair) throws IOException {
    return tickerAllPrices()
        .stream()
        .filter(p -> p.getCurrencyPair().equals(pair))
        .collect(StreamUtils.singletonCollector());
  }

  public List<NicehashPrice> tickerAllPrices() throws IOException {
    return nicehash.tickerAllPrices(null);
  }

  public List<NicehashPriceQuantity> tickerAllBookTickers() throws IOException {
    return nicehash.tickerAllBookTickers();
  }
}

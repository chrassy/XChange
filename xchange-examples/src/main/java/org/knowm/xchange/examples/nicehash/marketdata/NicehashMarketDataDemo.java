package org.knowm.xchange.examples.nicehash.marketdata;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.examples.nicehash.NicehashDemoUtils;
import org.knowm.xchange.nicehash.NicehashExchange;
import org.knowm.xchange.nicehash.dto.marketdata.NicehashTicker24h;
import org.knowm.xchange.nicehash.dto.meta.exchangeinfo.NicehashExchangeInfo;
import org.knowm.xchange.nicehash.service.NicehashBaseService;
import org.knowm.xchange.nicehash.service.NicehashMarketDataService;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NicehashMarketDataDemo {

  public static void main(String[] args) throws IOException {

    Exchange exchange = NicehashDemoUtils.createExchange();

    /* create a data service from the exchange */
    MarketDataService marketDataService = exchange.getMarketDataService();

    generic(exchange, marketDataService);
    raw((NicehashExchange) exchange, (NicehashMarketDataService) marketDataService);

    Long time = ((NicehashMarketDataService) marketDataService).getTimestamp();
    System.out.println("server time: " + time);


  }

  public static void generic(Exchange exchange, MarketDataService marketDataService)
      throws IOException {}

  public static void raw(NicehashExchange exchange, NicehashMarketDataService marketDataService)
      throws IOException {

    List<NicehashTicker24h> tickers = new ArrayList<>();
    for (CurrencyPair cp : exchange.getExchangeMetaData().getCurrencyPairs().keySet()) {
      if (cp.counter == Currency.USDT) {
        tickers.add(marketDataService.ticker24h(cp));
      }
    }

    Collections.sort(
        tickers,
        new Comparator<NicehashTicker24h>() {
          @Override
          public int compare(NicehashTicker24h t1, NicehashTicker24h t2) {
            return t2.getPriceChangePercent().compareTo(t1.getPriceChangePercent());
          }
        });

    tickers
        .stream()
        .forEach(
            t -> {
              System.out.println(
                  t.getCurrencyPair()
                      + " => "
                      + String.format("%+.2f%%", t.getPriceChangePercent()));
            });
    System.out.println("raw out end");
  }

  public static void rawAll(NicehashExchange exchange, NicehashMarketDataService marketDataService)
      throws IOException {

    List<NicehashTicker24h> tickers = new ArrayList<>();
    tickers.addAll(marketDataService.ticker24h());
    Collections.sort(
        tickers,
        new Comparator<NicehashTicker24h>() {
          @Override
          public int compare(NicehashTicker24h t1, NicehashTicker24h t2) {
            return t2.getPriceChangePercent().compareTo(t1.getPriceChangePercent());
          }
        });

    tickers
        .stream()
        .forEach(
            t -> {
              System.out.println(
                  t.getSymbol() + " => " + String.format("%+.2f%%", t.getLastPrice()));
            });
  }
}

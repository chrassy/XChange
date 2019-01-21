package org.knowm.xchange.examples.nicehash.marketdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.nicehash.NicehashExchange;
import org.knowm.xchange.nicehash.dto.marketdata.KlineInterval;
import org.knowm.xchange.nicehash.dto.marketdata.NicehashTicker24h;
import org.knowm.xchange.nicehash.service.NicehashAccountService;
import org.knowm.xchange.nicehash.service.NicehashMarketDataService;
import org.knowm.xchange.nicehash.service.NicehashTradeService;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;

public class NicehashMarketDataDemo {

  public static void main(String[] args) throws IOException {

    //Init exchange and services
    Exchange nicehash = ExchangeFactory.INSTANCE.createExchange(NicehashExchange.class.getName());
    AccountService accountService = nicehash.getAccountService();
    MarketDataService marketDataService = nicehash.getMarketDataService();
    TradeService tradeService = nicehash.getTradeService();

    System.out.println("server time: " + ((NicehashMarketDataService) marketDataService).getTimestamp());
   // System.out.println("Prices: \n" + ((NicehashMarketDataService) marketDataService).tickerAllPrices());
    System.out.println("acc info: " + ((NicehashAccountService) accountService).getAccountInfo());

    //ticker for all currencies
    //rawAll((NicehashExchange) nicehash, (NicehashMarketDataService) marketDataService);

  }

  public static void generic(Exchange exchange, MarketDataService marketDataService)
      throws IOException {}

  public static void raw(NicehashExchange exchange, NicehashMarketDataService marketDataService)
      throws IOException {

    List<NicehashTicker24h> tickers = new ArrayList<>();
    for (CurrencyPair cp : exchange.getExchangeMetaData().getCurrencyPairs().keySet()) {
      if (cp.counter == Currency.TBTC) {
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
                  t.getSymbol() + " => " + String.format("price change %+.2f%%", t.getPriceChangePercent()));
            });
  }
}

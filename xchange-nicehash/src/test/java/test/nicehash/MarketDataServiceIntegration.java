package test.nicehash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.*;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.nicehash.NicehashExchange;
import org.knowm.xchange.nicehash.dto.marketdata.NicehashTicker24h;
import org.knowm.xchange.nicehash.service.NicehashMarketDataService;
import org.knowm.xchange.service.marketdata.MarketDataService;

public class MarketDataServiceIntegration {

  static Exchange exchange;
  static MarketDataService marketService;

  @BeforeClass
  public static void beforeClass() {
    exchange = ExchangeFactory.INSTANCE.createExchange(NicehashExchange.class.getName());
    marketService = exchange.getMarketDataService();
  }

  @Before
  public void before() {
    Assume.assumeNotNull(exchange.getExchangeSpecification().getApiKey());
  }

  @Test
  public void testTimestamp() throws Exception {

    NicehashMarketDataService marketDataService =
        (NicehashMarketDataService) exchange.getMarketDataService();
    long serverTime = marketDataService.getTimestamp();
    Assert.assertTrue(0 < serverTime);
  }

  @Test
  public void testNicehashTicker24h() throws Exception {

    List<NicehashTicker24h> tickers = new ArrayList<>();
    for (CurrencyPair cp : exchange.getExchangeMetaData().getCurrencyPairs().keySet()) {
      if (cp.counter == Currency.USDT) {
        tickers.add(getNicehashTicker24h(cp));
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
  }

  private NicehashTicker24h getNicehashTicker24h(CurrencyPair pair) throws IOException {
    NicehashMarketDataService service = (NicehashMarketDataService) marketService;
    return service.ticker24h(pair);
  }
}

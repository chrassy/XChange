package test.nicehash;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.nicehash.NicehashExchange;
import org.knowm.xchange.nicehash.service.NicehashAccountService;
import org.knowm.xchange.nicehash.service.NicehashMarketDataService;
import org.knowm.xchange.nicehash.service.NicehashTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeServiceIntegration {

  static Logger LOG = LoggerFactory.getLogger(TradeServiceIntegration.class);

  static Exchange exchange;
  static NicehashTradeService tradeService;
  static NicehashMarketDataService marketService;
  static NicehashAccountService accountService;

  @BeforeClass
  public static void beforeClass() {
    exchange = ExchangeFactory.INSTANCE.createExchange(NicehashExchange.class.getName());
    marketService = (NicehashMarketDataService) exchange.getMarketDataService();
    accountService = (NicehashAccountService) exchange.getAccountService();
    tradeService = (NicehashTradeService) exchange.getTradeService();
  }

  @Before
  public void before() {
    Assume.assumeNotNull(exchange.getExchangeSpecification().getApiKey());
  }

  @Test
  public void openOrders() throws Exception {

    CurrencyPair pair = CurrencyPair.XRP_BTC;

    /*  // OpenOrders orders = tradeService.placeLimitOrder(new LimitOrder(OrderType.LIMIT, new BigDecimal(1), CurrencyPair.BTC_LTC), "idx", tradeService.getTimestamp(), new BigDecimal(0.12));
    LimitOrder order = orders.getOpenOrders().stream().collect(StreamUtils.singletonCollector());
    if (order != null) {
      System.out.println(order);
    }*/
  }
}

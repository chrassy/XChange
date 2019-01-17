package org.knowm.xchange.nicehash;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.nicehash.dto.meta.exchangeinfo.NicehashExchangeInfo;
import org.knowm.xchange.nicehash.dto.meta.exchangeinfo.Symbol;
import org.knowm.xchange.nicehash.service.NicehashAccountService;
import org.knowm.xchange.nicehash.service.NicehashMarketDataService;
import org.knowm.xchange.nicehash.service.NicehashTradeService;
import org.knowm.xchange.utils.AuthUtils;
import org.knowm.xchange.utils.nonce.AtomicLongCurrentTimeIncrementalNonceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.SynchronizedValueFactory;

public class NicehashExchange extends BaseExchange {

  private static final Logger LOG = LoggerFactory.getLogger(NicehashExchange.class);

  private static final int DEFAULT_PRECISION = 8;

  private SynchronizedValueFactory<Long> nonceFactory =
      new AtomicLongCurrentTimeIncrementalNonceFactory();
  private NicehashExchangeInfo exchangeInfo;
  private Long deltaServerTimeExpire;
  private Long deltaServerTime;

  @Override
  protected void initServices() {

    this.marketDataService = new NicehashMarketDataService(this);
    this.tradeService = new NicehashTradeService(this);
    this.accountService = new NicehashAccountService(this);
  }

  @Override
  public SynchronizedValueFactory<Long> getNonceFactory() {

    return nonceFactory;
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {

    ExchangeSpecification spec = new ExchangeSpecification(this.getClass().getCanonicalName());
    spec.setSslUri("https://api-test.nicehash.com/exchange/");
    spec.setHost("www.nicehash.com");
    spec.setPort(80);
    spec.setExchangeName("Nicehash");
    spec.setExchangeDescription("Nicehash Exchange.");
    /**     * Temporary api and api secret of testing acc for development purposes    * **/
      spec.setSecretKey("108a013e-a342-4b41-bb2b-ddc32c0ee84f61d32497-eb9d-41b0-98c1-9f81c671df9a");
      spec.setApiKey("c466544e-bcb0-43f7-a602-10c5b463622e");
    //AuthUtils.setApiAndSecretKey(spec, "nicehash");
    return spec;
  }

  public NicehashExchangeInfo getExchangeInfo() {

    return exchangeInfo;
  }

  @Override
  public void remoteInit() {

    try {
      // populate currency pair keys only, exchange does not provide any other metadata for download
      Map<CurrencyPair, CurrencyPairMetaData> currencyPairs = exchangeMetaData.getCurrencyPairs();
      Map<Currency, CurrencyMetaData> currencies = exchangeMetaData.getCurrencies();

      NicehashMarketDataService marketDataService =
          (NicehashMarketDataService) this.marketDataService;
      exchangeInfo = marketDataService.getExchangeInfo();
      Symbol[] symbols = exchangeInfo.getSymbols();

      /* for (NicehashPrice price : marketDataService.tickerAllPrices()) {
        CurrencyPair pair = price.getCurrencyPair();

        for (Symbol symbol : symbols) {
          if (symbol
              .getSymbol()
              .equals(pair.base.getCurrencyCode() + pair.counter.getCurrencyCode())) {

            int basePrecision = Integer.parseInt(symbol.getBaseAssetPrecision());
            int counterPrecision = Integer.parseInt(symbol.getQuotePrecision());
            int pairPrecision = 8;
            int amountPrecision = 8;

            BigDecimal minQty = null;
            BigDecimal maxQty = null;

            Filter[] filters = symbol.getFilters();

            for (Filter filter : filters) {
              if (filter.getFilterType().equals("PRICE_FILTER")) {
                pairPrecision = Math.min(pairPrecision, numberOfDecimals(filter.getTickSize()));
              } else if (filter.getFilterType().equals("LOT_SIZE")) {
                amountPrecision = Math.min(amountPrecision, numberOfDecimals(filter.getMinQty()));
                minQty = new BigDecimal(filter.getMinQty()).stripTrailingZeros();
                maxQty = new BigDecimal(filter.getMaxQty()).stripTrailingZeros();
              }
            }

            currencyPairs.put(
                price.getCurrencyPair(),
                new CurrencyPairMetaData(
                    new BigDecimal("0.1"), // Trading fee at Nicehash is 0.1 %
                    minQty, // Min amount
                    maxQty, // Max amount
                    pairPrecision, // precision
                    null
      ));
            currencies.put(
                pair.base,
                new CurrencyMetaData(
                    basePrecision,
                    currencies.containsKey(pair.base)
                        ? currencies.get(pair.base).getWithdrawalFee()
                        : null));
            currencies.put(
                pair.counter,
                new CurrencyMetaData(
                    counterPrecision,
                    currencies.containsKey(pair.counter)
                        ? currencies.get(pair.counter).getWithdrawalFee()
                        : null));
          }
        }
      }*/
    } catch (Exception e) {
      throw new ExchangeException("Failed to initialize: " + e.getMessage(), e);
    }
  }

  private int numberOfDecimals(String value) {

    return new BigDecimal(value).stripTrailingZeros().scale();
  }

  public void clearDeltaServerTime() {

    deltaServerTime = null;
  }

  public long deltaServerTime() throws IOException {

    if (deltaServerTime == null || deltaServerTimeExpire <= System.currentTimeMillis()) {

      // Do a little warm up
      Nicehash nicehash =
          RestProxyFactory.createProxy(Nicehash.class, getExchangeSpecification().getSslUri());
      Date serverTime = new Date(nicehash.time().getServerTime().getTime());

      // Assume that we are closer to the server time when we get the repose
      Date systemTime = new Date(System.currentTimeMillis());

      // Expire every 10min
      deltaServerTimeExpire = systemTime.getTime() + TimeUnit.MINUTES.toMillis(10);
      deltaServerTime = serverTime.getTime() - systemTime.getTime();

      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
      LOG.trace(
          "deltaServerTime: {} - {} => {}",
          df.format(serverTime),
          df.format(systemTime),
          deltaServerTime);
    }

    return deltaServerTime;
  }
}

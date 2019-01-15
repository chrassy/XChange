package org.knowm.xchange.nicehash.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.nicehash.NicehashAdapters;
import org.knowm.xchange.nicehash.dto.NicehashException;
import org.knowm.xchange.nicehash.dto.trade.*;

public class NicehashTradeServiceRaw extends NicehashBaseService {

  protected NicehashTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public List<NicehashOrder> openOrders(Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    return nicehash.openOrders(null, recvWindow, timestamp, super.apiKey, super.signatureCreator);
  }

  public List<NicehashOrder> openOrders(CurrencyPair pair, Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    return nicehash.openOrders(
        NicehashAdapters.toSymbol(pair),
        recvWindow,
        timestamp,
        super.apiKey,
        super.signatureCreator);
  }

  public NicehashNewOrder newOrder(
      CurrencyPair pair,
      OrderSide side,
      OrderType type,
      TimeInForce timeInForce,
      BigDecimal quantity,
      BigDecimal price,
      String newClientOrderId,
      BigDecimal stopPrice,
      BigDecimal icebergQty,
      Long recvWindow,
      long timestamp)
      throws IOException, NicehashException {
    return nicehash.newOrder(
        NicehashAdapters.toSymbol(pair),
        side,
        type,
        timeInForce,
        quantity,
        price,
        newClientOrderId,
        stopPrice,
        icebergQty,
        recvWindow,
        timestamp,
        super.apiKey,
        super.signatureCreator);
  }

  public void testNewOrder(
      CurrencyPair pair,
      OrderSide side,
      OrderType type,
      TimeInForce timeInForce,
      BigDecimal quantity,
      BigDecimal price,
      String newClientOrderId,
      BigDecimal stopPrice,
      BigDecimal icebergQty,
      Long recvWindow,
      long timestamp)
      throws IOException, NicehashException {
    nicehash.testNewOrder(
        NicehashAdapters.toSymbol(pair),
        side,
        type,
        timeInForce,
        quantity,
        price,
        newClientOrderId,
        stopPrice,
        icebergQty,
        recvWindow,
        timestamp,
        super.apiKey,
        super.signatureCreator);
  }

  public NicehashOrder orderStatus(
      CurrencyPair pair, long orderId, String origClientOrderId, Long recvWindow, long timestamp)
      throws IOException, NicehashException {
    return nicehash.orderStatus(
        NicehashAdapters.toSymbol(pair),
        orderId,
        origClientOrderId,
        recvWindow,
        timestamp,
        super.apiKey,
        super.signatureCreator);
  }

  public NicehashCancelledOrder cancelOrder(
      CurrencyPair pair,
      long orderId,
      String origClientOrderId,
      String newClientOrderId,
      Long recvWindow,
      long timestamp)
      throws IOException, NicehashException {
    return nicehash.cancelOrder(
        NicehashAdapters.toSymbol(pair),
        orderId,
        origClientOrderId,
        newClientOrderId,
        recvWindow,
        timestamp,
        super.apiKey,
        super.signatureCreator);
  }

  public List<NicehashOrder> allOrders(
      CurrencyPair pair, Long orderId, Integer limit, Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    return nicehash.allOrders(
        NicehashAdapters.toSymbol(pair),
        orderId,
        limit,
        recvWindow,
        timestamp,
        super.apiKey,
        super.signatureCreator);
  }

  public List<NicehashTrade> myTrades(
      CurrencyPair pair, Integer limit, Long fromId, Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    return nicehash.myTrades(
        NicehashAdapters.toSymbol(pair),
        limit,
        fromId,
        recvWindow,
        timestamp,
        super.apiKey,
        super.signatureCreator);
  }

  public NicehashListenKey startUserDataStream() throws IOException {
    return nicehash.startUserDataStream(apiKey);
  }

  public void keepAliveDataStream(String listenKey) throws IOException {
    nicehash.keepAliveUserDataStream(apiKey, listenKey);
  }

  public void closeDataStream(String listenKey) throws IOException {
    nicehash.closeUserDataStream(apiKey, listenKey);
  }
}

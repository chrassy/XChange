package org.knowm.xchange.nicehash;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.nicehash.dto.trade.NicehashOrder;
import org.knowm.xchange.nicehash.dto.trade.OrderSide;
import org.knowm.xchange.nicehash.dto.trade.OrderStatus;
import org.knowm.xchange.nicehash.service.NicehashTradeService.NicehashOrderFlags;

public class NicehashAdapters {

  private NicehashAdapters() {}

  public static String toSymbol(CurrencyPair pair) {
    return pair.base.getCurrencyCode() + pair.counter.getCurrencyCode();
  }

  public static String toSymbol(Currency currency) {
    return currency.getSymbol();
  }

  public static OrderType convert(OrderSide side) {
    switch (side) {
      case BUY:
        return OrderType.BID;
      case SELL:
        return OrderType.ASK;
      default:
        throw new RuntimeException("Not supported order side: " + side);
    }
  }

  public static OrderSide convert(OrderType type) {
    switch (type) {
      case ASK:
        return OrderSide.SELL;
      case BID:
        return OrderSide.BUY;
      default:
        throw new RuntimeException("Not supported order type: " + type);
    }
  }

  public static CurrencyPair convert(String symbol) {
    // Iterate by base currency priority at nicehash.
    for (Currency base : Arrays.asList(Currency.BTC, Currency.ETH, Currency.BNB, Currency.USDT)) {
      if (symbol.contains(base.toString())) {
        String counter = symbol.replace(base.toString(), "");
        return new CurrencyPair(base, new Currency(counter));
      }
    }
    throw new IllegalArgumentException("Could not parse currency pair from '" + symbol + "'");
  }

  public static long id(String id) {
    try {
      return Long.valueOf(id);
    } catch (Throwable e) {
      throw new RuntimeException("Nicehash id must be a valid long number.", e);
    }
  }

  public static Order.OrderStatus adaptOrderStatus(OrderStatus orderStatus) {
    switch (orderStatus) {
      case NEW:
        return Order.OrderStatus.NEW;
      case FILLED:
        return Order.OrderStatus.FILLED;
      case EXPIRED:
        return Order.OrderStatus.EXPIRED;
      case CANCELED:
        return Order.OrderStatus.CANCELED;
      case REJECTED:
        return Order.OrderStatus.REJECTED;
      case PENDING_CANCEL:
        return Order.OrderStatus.PENDING_CANCEL;
      case PARTIALLY_FILLED:
        return Order.OrderStatus.PARTIALLY_FILLED;
      default:
        return Order.OrderStatus.UNKNOWN;
    }
  }

  public static OrderType convertType(boolean isBuyer) {
    return isBuyer ? OrderType.BID : OrderType.ASK;
  }

  public static CurrencyPair adaptSymbol(String symbol) {
    int pairLength = symbol.length();
    if (symbol.endsWith("USDT")) {
      return new CurrencyPair(symbol.substring(0, pairLength - 4), "USDT");
    } else {
      return new CurrencyPair(
          symbol.substring(0, pairLength - 3), symbol.substring(pairLength - 3));
    }
  }

  public static Order adaptOrder(NicehashOrder order) {
    OrderType type = convert(order.side);
    CurrencyPair currencyPair = adaptSymbol(order.symbol);

    Order.OrderStatus orderStatus = adaptOrderStatus(order.status);
    final BigDecimal averagePrice;
    if (order.executedQty.signum() == 0
        || order.type.equals(org.knowm.xchange.nicehash.dto.trade.OrderType.MARKET)) {
      averagePrice = BigDecimal.ZERO;
    } else {
      averagePrice = order.price;
    }

    Order result;
    if (order.type.equals(org.knowm.xchange.nicehash.dto.trade.OrderType.MARKET)) {
      result =
          new MarketOrder(
              type,
              order.origQty,
              currencyPair,
              Long.toString(order.orderId),
              order.getTime(),
              averagePrice,
              order.executedQty,
              BigDecimal.ZERO,
              orderStatus);
    } else if (order.type.equals(org.knowm.xchange.nicehash.dto.trade.OrderType.LIMIT)
        || order.type.equals(org.knowm.xchange.nicehash.dto.trade.OrderType.LIMIT_MAKER)) {
      result =
          new LimitOrder(
              type,
              order.origQty,
              currencyPair,
              Long.toString(order.orderId),
              order.getTime(),
              order.price,
              averagePrice,
              order.executedQty,
              BigDecimal.ZERO,
              orderStatus);
    } else {
      result =
          new StopOrder(
              type,
              order.origQty,
              currencyPair,
              Long.toString(order.orderId),
              order.getTime(),
              order.stopPrice,
              averagePrice,
              order.executedQty,
              orderStatus);
    }
    Set<Order.IOrderFlags> flags = new HashSet<>();
    if (order.clientOrderId != null) {
      flags.add(
          new NicehashOrderFlags() {
            @Override
            public String getClientId() {
              return order.clientOrderId;
            }
          });
    }
    result.setOrderFlags(flags);
    return result;
  }
}
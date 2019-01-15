package org.knowm.xchange.examples.nicehash;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.nicehash.NicehashExchange;

public class NicehashDemoUtils {

  public static Exchange createExchange() {

    Exchange exchange = ExchangeFactory.INSTANCE.createExchange(NicehashExchange.class.getName());
    return exchange;
  }
}

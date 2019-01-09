package org.knowm.xchange.nicehash;

import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.exceptions.CurrencyPairNotValidException;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.nicehash.dto.NicehashException;

/** @author walec51 */
public final class NicehashErrorAdapter {

  private NicehashErrorAdapter() {}

  public static ExchangeException adapt(NicehashException e) {
    String message = e.getMessage();
    if (StringUtils.isEmpty(message)) {
      message = "Operation failed without any error message";
    }
    switch (e.getCode()) {
      case -1121:
        return new CurrencyPairNotValidException(message, e);
    }
    return new ExchangeException(message, e);
  }
}

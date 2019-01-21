package org.knowm.xchange.nicehash.service;

import java.io.IOException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.nicehash.dto.NicehashException;
import org.knowm.xchange.nicehash.dto.account.*;
import org.knowm.xchange.nicehash.dto.account.NicehashAccountInformation;

public class NicehashAccountServiceRaw extends NicehashBaseService {

  public NicehashAccountServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public NicehashAccountInformation account(Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    return nicehash.account(recvWindow, timestamp, super.apiKey, super.signatureCreator);
  }
}

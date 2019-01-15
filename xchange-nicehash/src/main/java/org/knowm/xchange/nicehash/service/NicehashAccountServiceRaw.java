package org.knowm.xchange.nicehash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.nicehash.NicehashAdapters;
import org.knowm.xchange.nicehash.dto.account.*;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.nicehash.dto.NicehashException;
import org.knowm.xchange.nicehash.dto.account.NicehashAccountInformation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class NicehashAccountServiceRaw extends NicehashBaseService {

  public NicehashAccountServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public NicehashAccountInformation account(Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    return nicehash.account(recvWindow, timestamp, super.apiKey, super.signatureCreator);
  }

  // the /wapi endpoint of nicehash is not stable yet and can be changed in future, there is also a
  // lack of current documentation

  public String withdraw(String asset, String address, BigDecimal amount)
      throws IOException, NicehashException {
    // the name parameter seams to be mandatory
    String name = address.length() <= 10 ? address : address.substring(0, 10);
    return withdraw(asset, address, amount, name, null, getTimestamp());
  }

  public String withdraw(String asset, String address, String addressTag, BigDecimal amount)
      throws IOException, NicehashException {
    // the name parameter seams to be mandatory
    String name = address.length() <= 10 ? address : address.substring(0, 10);
    Long recvWindow =
        (Long) exchange.getExchangeSpecification().getExchangeSpecificParametersItem("recvWindow");
    return withdraw(asset, address, addressTag, amount, name, recvWindow, getTimestamp());
  }

  private String withdraw(
      String asset, String address, BigDecimal amount, String name, Long recvWindow, long timestamp)
      throws IOException, NicehashException {
    WithdrawRequest result =
        nicehash.withdraw(
            asset,
            address,
            null,
            amount,
            name,
            recvWindow,
            timestamp,
            super.apiKey,
            super.signatureCreator);
    checkWapiResponse(result);
    return result.getData();
  }

  private String withdraw(
      String asset,
      String address,
      String addressTag,
      BigDecimal amount,
      String name,
      Long recvWindow,
      long timestamp)
      throws IOException, NicehashException {
    WithdrawRequest result =
        nicehash.withdraw(
            asset,
            address,
            addressTag,
            amount,
            name,
            recvWindow,
            timestamp,
            super.apiKey,
            super.signatureCreator);
    checkWapiResponse(result);
    return result.getData();
  }

  public DepositAddress requestDepositAddress(Currency currency) throws IOException {
    Long recvWindow =
        (Long) exchange.getExchangeSpecification().getExchangeSpecificParametersItem("recvWindow");
    return nicehash.depositAddress(
        NicehashAdapters.toSymbol(currency),
        recvWindow,
        getTimestamp(),
        apiKey,
        super.signatureCreator);
  }

  public List<DepositList.NicehashDeposit> depositHistory(
      String asset, Long startTime, Long endTime, Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    DepositList result =
        nicehash.depositHistory(
            asset, startTime, endTime, recvWindow, timestamp, super.apiKey, super.signatureCreator);
    return checkWapiResponse(result);
  }

  public List<WithdrawList.NicehashWithdraw> withdrawHistory(
      String asset, Long startTime, Long endTime, Long recvWindow, long timestamp)
      throws NicehashException, IOException {
    WithdrawList result =
        nicehash.withdrawHistory(
            asset, startTime, endTime, recvWindow, timestamp, super.apiKey, super.signatureCreator);
    return checkWapiResponse(result);
  }

  private <T> T checkWapiResponse(WapiResponse<T> result) {
    if (!result.success) {
      NicehashException exception;
      try {
        exception = new ObjectMapper().readValue(result.msg, NicehashException.class);
      } catch (Throwable e) {
        exception = new NicehashException(-1, result.msg);
      }
      throw exception;
    }
    return result.getData();
  }
}

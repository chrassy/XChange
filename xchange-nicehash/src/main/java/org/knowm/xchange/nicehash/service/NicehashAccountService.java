package org.knowm.xchange.nicehash.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.FundingRecord;
import org.knowm.xchange.dto.account.FundingRecord.Status;
import org.knowm.xchange.dto.account.FundingRecord.Type;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.nicehash.NicehashErrorAdapter;
import org.knowm.xchange.nicehash.dto.NicehashException;
import org.knowm.xchange.nicehash.dto.account.NicehashAccountInformation;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.params.*;

public class NicehashAccountService extends NicehashAccountServiceRaw implements AccountService {

  public NicehashAccountService(Exchange exchange) {
    super(exchange);
  }

  /** (0:Email Sent,1:Cancelled 2:Awaiting Approval 3:Rejected 4:Processing 5:Failure 6Completed) */
  private static Status withdrawStatus(int status) {
    switch (status) {
      case 0:
      case 2:
      case 4:
        return Status.PROCESSING;
      case 1:
        return Status.CANCELLED;
      case 3:
      case 5:
        return Status.FAILED;
      case 6:
        return Status.COMPLETE;
      default:
        throw new RuntimeException("Unknown binance withdraw status: " + status);
    }
  }

  /** (0:pending,1:success) */
  private static Status depositStatus(int status) {
    switch (status) {
      case 0:
        return Status.PROCESSING;
      case 1:
        return Status.COMPLETE;
      default:
        throw new RuntimeException("Unknown binance deposit status: " + status);
    }
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {
    try {
      Long recvWindow =
          (Long)
              exchange.getExchangeSpecification().getExchangeSpecificParametersItem("recvWindow");
      NicehashAccountInformation acc = super.account(recvWindow, getTimestamp());
      List<Balance> balances =
          acc.balances
              .stream()
              .map(b -> new Balance(b.getCurrency(), b.getTotal(), b.getAvailable()))
              .collect(Collectors.toList());
      return new AccountInfo(new Wallet(balances));
    } catch (NicehashException e) {
      throw NicehashErrorAdapter.adapt(e);
    }
  }

  @Override
  public String withdrawFunds(Currency currency, BigDecimal amount, String address)
      throws IOException {
    try {
      return super.withdraw(currency.getCurrencyCode(), address, amount);
    } catch (NicehashException e) {
      throw NicehashErrorAdapter.adapt(e);
    }
  }

  @Override
  public String withdrawFunds(WithdrawFundsParams params) throws IOException {
    try {
      if (!(params instanceof DefaultWithdrawFundsParams)) {
        throw new IllegalArgumentException("DefaultWithdrawFundsParams must be provided.");
      }
      String id = null;
      if (params instanceof RippleWithdrawFundsParams) {
        RippleWithdrawFundsParams rippleParams = null;
        rippleParams = (RippleWithdrawFundsParams) params;
        id =
            super.withdraw(
                rippleParams.getCurrency().getCurrencyCode(),
                rippleParams.getAddress(),
                rippleParams.getTag(),
                rippleParams.getAmount());
      } else {
        DefaultWithdrawFundsParams p = (DefaultWithdrawFundsParams) params;
        id = super.withdraw(p.getCurrency().getCurrencyCode(), p.getAddress(), p.getAmount());
      }
      return id;
    } catch (NicehashException e) {
      throw NicehashErrorAdapter.adapt(e);
    }
  }

  @Override
  public String requestDepositAddress(Currency currency, String... args) throws IOException {
    try {
      return super.requestDepositAddress(currency).address;
    } catch (NicehashException e) {
      throw NicehashErrorAdapter.adapt(e);
    }
  }

  @Override
  public TradeHistoryParams createFundingHistoryParams() {
    return new NicehashFundingHistoryParams();
  }

  @Override
  public List<FundingRecord> getFundingHistory(TradeHistoryParams params) throws IOException {
    try {
      String asset = null;
      if (params instanceof TradeHistoryParamCurrency) {
        TradeHistoryParamCurrency cp = (TradeHistoryParamCurrency) params;
        if (cp.getCurrency() != null) {
          asset = cp.getCurrency().getCurrencyCode();
        }
      }
      Long recvWindow =
          (Long)
              exchange.getExchangeSpecification().getExchangeSpecificParametersItem("recvWindow");

      boolean withdrawals = true;
      boolean deposits = true;

      Long startTime = null;
      Long endTime = null;
      if (params instanceof TradeHistoryParamsTimeSpan) {
        TradeHistoryParamsTimeSpan tp = (TradeHistoryParamsTimeSpan) params;
        if (tp.getStartTime() != null) {
          startTime = tp.getStartTime().getTime();
        }
        if (tp.getEndTime() != null) {
          endTime = tp.getEndTime().getTime();
        }
      }

      if (params instanceof HistoryParamsFundingType) {
        HistoryParamsFundingType f = (HistoryParamsFundingType) params;
        if (f.getType() != null) {
          withdrawals = f.getType() == Type.WITHDRAWAL;
          deposits = f.getType() == Type.DEPOSIT;
        }
      }

      List<FundingRecord> result = new ArrayList<>();
      if (withdrawals) {
        super.withdrawHistory(asset, startTime, endTime, recvWindow, getTimestamp())
            .forEach(
                w -> {
                  result.add(
                      new FundingRecord(
                          w.address,
                          new Date(w.applyTime),
                          Currency.getInstance(w.asset),
                          w.amount,
                          w.id,
                          w.txId,
                          Type.WITHDRAWAL,
                          withdrawStatus(w.status),
                          null,
                          null,
                          null));
                });
      }

      if (deposits) {
        super.depositHistory(asset, startTime, endTime, recvWindow, getTimestamp())
            .forEach(
                d -> {
                  result.add(
                      new FundingRecord(
                          d.address,
                          new Date(d.insertTime),
                          Currency.getInstance(d.asset),
                          d.amount,
                          null,
                          d.txId,
                          Type.DEPOSIT,
                          depositStatus(d.status),
                          null,
                          null,
                          null));
                });
      }

      return result;
    } catch (NicehashException e) {
      throw NicehashErrorAdapter.adapt(e);
    }
  }
}

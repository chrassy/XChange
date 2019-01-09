package org.knowm.xchange.nicehash.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public final class DepositList extends WapiResponse<List<DepositList.NicehashDeposit>> {

  private final NicehashDeposit[] depositList;

  public DepositList(
      @JsonProperty("depositList") NicehashDeposit[] depositList,
      @JsonProperty("success") boolean success,
      @JsonProperty("msg") String msg) {
    super(success, msg);
    this.depositList = depositList;
  }

  @Override
  public List<NicehashDeposit> getData() {
    return Arrays.asList(depositList);
  }

  @Override
  public String toString() {
    return "DepositList [depositList=" + Arrays.toString(depositList) + "]";
  }

  public static final class NicehashDeposit {
    /*
    {
             "insertTime":1509505623000,
             "amount":32.000000000000000000,
             "asset":"BTC",
             "status":1
                   }
             */

    public final long insertTime;
    public final BigDecimal amount;
    public final String asset;
    public final String txId;
    public final String address;

    /** (0:pending,1:success) */
    public final int status;

    public NicehashDeposit(
        @JsonProperty("insertTime") long insertTime,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("asset") String asset,
        @JsonProperty("txId") String txId,
        @JsonProperty("address") String address,
        @JsonProperty("status") int status) {
      super();
      this.insertTime = insertTime;
      this.amount = amount;
      this.asset = asset;
      this.status = status;
      this.txId = txId;
      this.address = address;
    }

    @Override
    public String toString() {
      return "NicehashDeposit [insertTime="
          + insertTime
          + ", amount="
          + amount
          + ", asset="
          + asset
          + ", txId="
          + txId
          + ", address="
          + address
          + ", status="
          + status
          + "]";
    }
  }
}

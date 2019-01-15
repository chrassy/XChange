package org.knowm.xchange.nicehash.dto.meta;

import java.math.BigDecimal;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.FeeTier;

/** @author ujjwal on 26/02/18. */
public class NicehashCurrencyPairMetaData extends CurrencyPairMetaData {
  private final BigDecimal minNotional;

  /**
   * Constructor
   *
   * @param tradingFee Trading fee (fraction)
   * @param minimumAmount Minimum trade amount
   * @param maximumAmount Maximum trade amount
   * @param priceScale Price scale
   */
  public NicehashCurrencyPairMetaData(
      BigDecimal tradingFee,
      BigDecimal minimumAmount,
      BigDecimal maximumAmount,
      Integer priceScale,
      BigDecimal minNotional,
      FeeTier[] feeTiers) {
    super(tradingFee, minimumAmount, maximumAmount, priceScale, feeTiers);
    this.minNotional = minNotional;
  }

  public BigDecimal getMinNotional() {
    return minNotional;
  }

  @Override
  public String toString() {
    return "NicehashCurrencyPairMetaData{" + "minNotional=" + minNotional + "} " + super.toString();
  }
}

package org.knowm.xchange.nicehash.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.nicehash.NicehashAuthenticated;
import org.knowm.xchange.nicehash.NicehashExchange;
import org.knowm.xchange.nicehash.dto.meta.exchangeinfo.NicehashExchangeInfo;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NicehashBaseService extends BaseExchangeService implements BaseService {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected final String apiKey;
    protected final NicehashAuthenticated nicehash;
    protected final ParamsDigest signatureCreator;

    protected NicehashBaseService(Exchange exchange) {

        super(exchange);
        this.nicehash =
                RestProxyFactory.createProxy(
                        NicehashAuthenticated.class,
                        exchange.getExchangeSpecification().getSslUri(),
                        getClientConfig());
        this.apiKey = exchange.getExchangeSpecification().getApiKey();
        this.signatureCreator =
               NicehashHmacDigest.createInstance(exchange.getExchangeSpecification().getSecretKey());
    }

    public long getTimestamp() throws IOException {

        long deltaServerTime = ((NicehashExchange) exchange).deltaServerTime();
        Date systemTime = new Date(System.currentTimeMillis());
        Date serverTime = new Date(systemTime.getTime() + deltaServerTime);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        LOG.trace(
                "getTimestamp: {} + {} => {}",
                df.format(systemTime),
                deltaServerTime,
                df.format(serverTime));
        return serverTime.getTime();
    }

    /**
     * After period of time, the deltaServerTime may not accurate again. Need to catch the "Timestamp
     * for this request was 1000ms ahead" exception and refresh the deltaServerTime.
     */
    public void refreshTimestamp() {
        ((NicehashExchange) exchange).clearDeltaServerTime();
    }

    public NicehashExchangeInfo getExchangeInfo() throws IOException {

        return nicehash.exchangeInfo();
    }
}

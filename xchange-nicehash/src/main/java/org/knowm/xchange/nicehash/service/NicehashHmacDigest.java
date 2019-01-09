package org.knowm.xchange.nicehash.service;

import static org.knowm.xchange.utils.DigestUtils.bytesToHex;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import javax.crypto.Mac;
import javax.ws.rs.QueryParam;
import org.knowm.xchange.nicehash.NicehashAuthenticated;
import org.knowm.xchange.service.BaseParamsDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.Params;
import si.mazi.rescu.RestInvocation;

public class NicehashHmacDigest extends BaseParamsDigest {

    private static final Logger LOG = LoggerFactory.getLogger(NicehashHmacDigest.class);

    private final Field invocationUrlField;

    private NicehashHmacDigest(String secretKeyBase64) {
        super(secretKeyBase64, HMAC_SHA_256);

        try {
            invocationUrlField = RestInvocation.class.getDeclaredField("invocationUrl");
            invocationUrlField.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static NicehashHmacDigest createInstance(String secretKeyBase64) {
        return secretKeyBase64 == null ? null : new NicehashHmacDigest(secretKeyBase64);
    }

    /** @return the query string except of the "signature" parameter */
    private static String getQuery(RestInvocation restInvocation) {
        final Params p = Params.of();
        restInvocation
                .getParamsMap()
                .get(QueryParam.class)
                .asHttpHeaders()
                .entrySet()
                .stream()
                .filter(e -> !NicehashAuthenticated.SIGNATURE.equals(e.getKey()))
                .forEach(e -> p.add(e.getKey(), e.getValue()));
        return p.asQueryString();
    }

    @Override
    public String digestParams(RestInvocation restInvocation) {
        try {
            final String input;

            if (restInvocation.getPath().startsWith("wapi/")) {
                // little dirty hack for /wapi methods
                input = getQuery(restInvocation);
            } else {
                switch (restInvocation.getHttpMethod()) {
                    case "GET":
                    case "DELETE":
                        input = getQuery(restInvocation);
                        break;
                    case "POST":
                        input = restInvocation.getRequestBody();
                        break;
                    default:
                        throw new RuntimeException(
                                "Not support http method: " + restInvocation.getHttpMethod());
                }
            }

            Mac mac = getMac();
            mac.update(input.getBytes("UTF-8"));
            String printBase64Binary = bytesToHex(mac.doFinal());

            String invocationUrl = restInvocation.getInvocationUrl();

            final String sig = "signature=";
            int idx = invocationUrl.indexOf(sig);
            String newInvocationUrl = invocationUrl.substring(0, idx + sig.length()) + printBase64Binary;
            try {
                invocationUrlField.set(restInvocation, newInvocationUrl);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return printBase64Binary;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Illegal encoding, check the code.", e);
        }
    }
}

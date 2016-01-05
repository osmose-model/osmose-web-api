package com.github.jhpoelen.fbob;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TraitFinderTest {

    @Test
    public void disableSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    @Test
    public void findLifeSpan() throws IOException, URISyntaxException {
        String htlGroupName = "Scomberomorus" + "Cavalla";
        String urls = "https://fishbase.ropensci.org/species?Genus=Scomberomorus&Species=cavalla";
        Map<String, String> speciesProperties = new HashMap<String, String>();
        final JsonNode jsonNode = new ObjectMapper().readTree(IOUtils.toString(new URI(urls), "UTF-8"));
        final JsonNode data = jsonNode.get("data");
        if (data != null && data.isArray() && data.size() > 0) {
            final JsonNode firstHit = data.get(0);
            final JsonNode longevityWild = firstHit.get("LongevityWild");
            if (longevityWild != null && longevityWild.isNumber()) {
                speciesProperties.put("species.lifespan", longevityWild.asText());
            }
        }
        assertThat(speciesProperties.get("species.lifespan"), is("14"));
    }
}

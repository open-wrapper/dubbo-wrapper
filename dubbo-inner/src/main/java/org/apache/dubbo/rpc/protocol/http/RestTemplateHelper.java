package org.apache.dubbo.rpc.protocol.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

class RestTemplateHelper {

    private static RestTemplate restTemplate;

    public static synchronized RestTemplate getInstance() {
        if (restTemplate != null) {
            return restTemplate;
        }
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(1000);
        connectionManager.setDefaultMaxPerRoute(1000);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(connectionManager);
        HttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        try {
            requestFactory.setConnectTimeout(1000);
            requestFactory.setReadTimeout(3000);
        } catch (UnsupportedOperationException e) {
            //spring老版本不支持httpClient新版本，同时也不建议使用HttpProtocol的consumer
        }
        restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
}

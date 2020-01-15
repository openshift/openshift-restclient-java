package com.openshift.internal.restclient;

import static com.openshift.restclient.http.IHttpConstants.MEDIATYPE_APPLICATION_JSON;
import static com.openshift.restclient.http.IHttpConstants.PROPERTY_ACCEPT;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openshift.internal.restclient.okhttp.ResponseCodeInterceptor;
import com.openshift.restclient.OpenShiftException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

abstract class RequestingSupplier<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClient.class);

    private String url;
    protected String description;
    private OkHttpClient client;

    private boolean requested = false;
    private T value;

    protected RequestingSupplier(String url, String description, OkHttpClient client) {
        this.url = url;
        this.description = description;
        this.client = client;
        this.value = getDefaultValue();
    }

    public T get() {
        return requestIfRequired();
    }

    private T requestIfRequired() {
        try {
            if (!requested) {
                this.value = request(url);
            }
            return value;
        } catch (IOException e) {
            throw new OpenShiftException(e, "Unable to execute request to request url %s", url);
        }
    }

    protected T request(String url) throws IOException {
        Request request = new Builder()
                .url(url)
                .header(PROPERTY_ACCEPT, MEDIATYPE_APPLICATION_JSON)
                .tag(new ResponseCodeInterceptor.Ignore() {})
                .build();
        try (Response response = client.newCall(request).execute()) {
            this.requested = true;
            if (response != null
                    && response.isSuccessful()) {
                this.value = extractValue(response.body().string());
            } else {
                LOGGER.error("Failed to determine {}: got {}", description, 
                        response == null ? "null" : response.code());
            }
        }
        return this.value;
    }

    protected abstract T getDefaultValue();

    protected abstract T extractValue(String response);
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.integration.helper;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.*;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
import com.ning.http.client.providers.netty.NettyResponse;

/**
 *
 * @author dw
 */
public class AsyncHttpClientHelper {

    private AsyncHttpClient client = null;

    public AsyncHttpClientHelper() {
        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        client = new AsyncHttpClient(builder.build());
    }

    public String simpleGet(String url) {
        try {
            Future future = client.prepareGet(url).execute();
            NettyResponse nettyResponse = (NettyResponse) future.get();
            return nettyResponse.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String simplePost(String url, String body) {
        try {
            Request req = new RequestBuilder("POST")
                    .setUrl(url)
                    .addHeader("Content-Type", "application/json")
                    .setBody(body)
                    .build();
            Future future = client.prepareRequest(req).execute();
            NettyResponse nettyResponse = (NettyResponse) future.get();
            return nettyResponse.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package CustomerScenarios.helper;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.*;
import com.ning.http.client.providers.netty.NettyResponse;

/**
 * Created by Jason on 2/26/14.
 */
public class AsyncHttpClientHelper {

    private final String requestHeaderName = "Content-Type";
    private final String requestHeaderValue = "application/json";

    private AsyncHttpClient client = null;

    public AsyncHttpClientHelper() {
        AsyncHttpClientConfig.Builder builder = new Builder();
        client = new AsyncHttpClient(builder.build());
    }

    public String UserGet(String url) {
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

    public String UserPost(String url, String body) {

        System.out.println("The POST request URL is: " + url);
        System.out.println("The POST request body is: " + body);

        try {
            Request req = new RequestBuilder("POST")
                    .setUrl(url)
                    .addHeader(requestHeaderName, requestHeaderValue)
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

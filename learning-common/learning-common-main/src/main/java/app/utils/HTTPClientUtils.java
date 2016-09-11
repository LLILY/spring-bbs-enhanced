package app.utils;

import app.main.Logger;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

/**
 * Created by lili19289 on 2016/8/31.
 */
public class HTTPClientUtils {

    public static String getMethod(String URL,
                                   Map<String, Collection<String>> params) {
        final AsyncHttpClient client = new AsyncHttpClient();
        try {
            final FluentStringsMap paramsMap = new FluentStringsMap(params);
            final Response response = client.prepareGet(URL).setQueryParams(paramsMap)
                    .execute().get();
            final String responseBody = response.getResponseBody("utf8");
            if (responseBody == null) {
                Logger.error("[EndServer has not started.]");
            } else {
                return responseBody;
            }
        } catch (IllegalArgumentException | InterruptedException
                | ExecutionException | IOException e) {
            Logger.error(e, e.getMessage());
        } finally {
            client.close();
        }
        return null;
    }

    public static String postMethod(String URL,
                                    Map<String, Collection<String>> params) {
        final AsyncHttpClient client = new AsyncHttpClient();
        try {
            final FluentStringsMap paramsMap = new FluentStringsMap(params);
            final Response response = client.preparePost(URL)
                    .setQueryParams(paramsMap).execute().get();
            final String responseBody = response.getResponseBody("utf8");
            if (responseBody == null) {
                Logger.error("[EndServer has not started.]");
            } else {
                return responseBody;
            }
        } catch (IllegalArgumentException | InterruptedException
                | ExecutionException | IOException e) {
            Logger.error(e, e.getMessage());
        } finally {
            client.close();
        }
        return null;
    }

    public static class Builder {
        Map<String, Collection<String>> params;

        public Builder() {
            params = new HashMap<String, Collection<String>>();
        }

        public Builder addParam(String key, String value) {
            params.put(key, Arrays.asList(value));
            return this;
        }

        public Map<String, Collection<String>> end() {
            return params;
        }
    }
}

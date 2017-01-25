package com.hanoldery.pierre.scaneat.helpers;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by KansonD on 08/12/2015.
 */
public class urlClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {

        client.setCookieStore(inFactCookieStore.getInstance(context));
        Log.e("Client",url);
        client.get(url, params, responseHandler);

    }

    public static void post(Context context, String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        log.v("TEST", url + " \n  " + params);
        client.setCookieStore(inFactCookieStore.getInstance(context));
        client.post(url, params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, String basicAuth,
                            AsyncHttpResponseHandler responseHandler) {

        client.addHeader("Authorization", "Basic " + basicAuth);
        client.setCookieStore(inFactCookieStore.getInstance(context));
        client.post(url, params, responseHandler);
    }

    public static void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        client.setCookieStore(inFactCookieStore.getInstance(context));
        Log.v("Client",url);
        client.get(url, responseHandler);
    }

    public static void post(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        client.setCookieStore(inFactCookieStore.getInstance(context));
        client.post(url, responseHandler);
    }

    public static void delete(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setCookieStore(inFactCookieStore.getInstance(context));
        client.delete(url, params, responseHandler);
    }

    public static void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setCookieStore(inFactCookieStore.getInstance(context));
        client.put(url, params, responseHandler);
    }
}

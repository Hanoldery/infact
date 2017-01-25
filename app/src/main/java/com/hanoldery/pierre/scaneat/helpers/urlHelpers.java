package com.hanoldery.pierre.scaneat.helpers;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hanoldery.pierre.scaneat.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by Pierre on 24/01/2017.
 */

public class urlHelpers {


        private static Activity context;
        private boolean withProgressDialog;
        private ProgressDialog progress;

    private String URL_BASE = "https://fr.openfoodfacts.org/api/v0/produit/";

        private urlHelpers(Activity c) {

            context = c;
        }

        public static urlHelpers with(Activity c) {

            context = c;
            return new urlHelpers(c);

        }

        public urlHelpers withProgressDialog(boolean withProgressDialog) {
            this.withProgressDialog = withProgressDialog;

            return this;
        }

    public static Boolean checkConnectivityState() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            log.v("Connectivity"," OK");
            return true;
        } else {
            Toast.makeText(context, "Echec, veuillez vérifier votre connexion internet et ré-essayer.",
                    Toast.LENGTH_SHORT).show();
            log.v("Connectivity", " FAILED");
            return false;
        }
    }

        private void showProgressDialog() {

            if (!withProgressDialog) return;

            progress = new ProgressDialog(context);
            progress.setTitle(R.string.app_name);
            progress.setMessage("Loading");
            progress.setCanceledOnTouchOutside(false);
            progress.show();

        }

        private void dismissProgress() {

            if (progress != null) {
                try{
                    progress.dismiss();
                } catch(IllegalArgumentException e) {
                    e.printStackTrace();
                }
                progress = null;
            }
        }

        private void handlePostRequest(String action, RequestParams params, final AsyncHttpResponseHandler handler) {
            if (checkConnectivityState()) {
                showProgressDialog();

                signPostRequest(params);

                Log.d("UrlHelper", params.toString());
                Log.d("UrlHelper POST", URL_BASE + action);

                String urlToPost = URL_BASE + action;


                urlClient.post(context, urlToPost, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (handler != null) handler.onSuccess(statusCode, headers, responseBody);
                        dismissProgress();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (handler != null)
                            handler.onFailure(statusCode, headers, responseBody, error);
                        if (responseBody != null)
                            inFactHelper.handleErrorJSON(context, new String(responseBody));
                        error.printStackTrace();
                        dismissProgress();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (handler != null) handler.onFinish();
                        dismissProgress();
                    }
                });
            }
            else
                dismissProgress();
        }

        private void handleDeleteRequest(String action, RequestParams params, final AsyncHttpResponseHandler handler) {

            showProgressDialog();

            signPostRequest(params);

            Log.d("UrlHelper DELETE", params.toString());
            Log.d("UrlHelper DELETE", URL_BASE + action);

            urlClient.delete(context, URL_BASE + action, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("UrlHelper DELETE", "onSuccess");
                    if (handler != null) handler.onSuccess(statusCode, headers, responseBody);
                    dismissProgress();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("UrlHelper DELETE", "onFailure");
                    if (handler != null) handler.onFailure(statusCode, headers, responseBody, error);
                    if (responseBody != null)
                        inFactHelper.handleErrorJSON(context, new String(responseBody));
                    dismissProgress();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (handler != null) handler.onFinish();
                    dismissProgress();
                }
            });
        }

        private void handlePostRequest(String action, String auth, RequestParams params, final AsyncHttpResponseHandler handler) {
            if (checkConnectivityState()) {
                //signPostRequest(params);
                showProgressDialog();

                Log.d("UrlHelper POST", URL_BASE + action);

                urlClient.post(context, URL_BASE + action, params, auth, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (handler != null) handler.onSuccess(statusCode, headers, responseBody);
                        dismissProgress();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (handler != null) handler.onFailure(statusCode, headers, responseBody, error);
                        if (responseBody != null)
                            inFactHelper.handleErrorJSON(context, new String(responseBody));
                        dismissProgress();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (handler != null) handler.onFinish();
                        dismissProgress();
                    }
                });
            }
            else
                dismissProgress();

        }

        private void handleGetRequest(String action, String params, final AsyncHttpResponseHandler handler) {
            showProgressDialog();

            params = signGetRequest(params);

            String urlToPost = URL_BASE + action + "?" + params;

            Log.d("UrlHelper GET", URL_BASE + action + "?" + params);

            urlClient.get(context, urlToPost + params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (handler != null) handler.onSuccess(statusCode, headers, responseBody);
                    dismissProgress();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (handler != null) handler.onFailure(statusCode, headers, responseBody, error);
                    if (responseBody != null)
                        inFactHelper.handleErrorJSON(context, new String(responseBody));
                    dismissProgress();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (handler != null) handler.onFinish();
                    dismissProgress();
                }
            });

        }
/*
        private void handlePutRequest(String action, RequestParams params, final AsyncHttpResponseHandler handler) {

            showProgressDialog();

            signPostRequest(params);

            Log.d("UrlHelper", params.toString());
            Log.d("UrlHelper PUT", URL_BASE + action);

            urlClient.put(context, URL_BASE + action, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (handler != null) handler.onSuccess(statusCode, headers, responseBody);
                    dismissProgress();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (handler != null) handler.onFailure(statusCode, headers, responseBody, error);
                    if (responseBody != null)
                        inFactHelper.handleErrorJSON(context, new String(responseBody));
                    dismissProgress();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (handler != null) handler.onFinish();
                    dismissProgress();
                }
            });
        }
*/

    // Signing
    //
    private void signPostRequest(RequestParams params) {

        long timeInterval = System.currentTimeMillis();

        String signature = generateKey(timeInterval);

        //params.put(TOKEN_TAG, timeInterval);
        params.put("token", signature);
    }

    private String signGetRequest(String params) {

        if (params.length() > 0) {
            params += "&";
        }

        long timeInterval = System.currentTimeMillis();

        String signature = generateKey(timeInterval);

        //params += "token=" + signature + "&" + TOKEN_TAG + "=" + timeInterval;

        return params;
    }

    private String generateKey(long timeInterval) {

        //String result = TOKEN_TAG + "=" + timeInterval + TOKEN_SECRET;
        String result = "test";
        return toMD5(result);

    }

    public final String toMD5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru !
        }
    }








    // URL HELPERS

    public void getDetailsProduct(String orderId, AsyncHttpResponseHandler handler) {
        handleGetRequest(orderId, "", handler);
    }
}

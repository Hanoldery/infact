package com.hanoldery.pierre.scaneat.helpers;

import android.content.Context;

import com.loopj.android.http.PersistentCookieStore;

/**
 * Created by Pierre on 24/01/2017.
 */

public class inFactCookieStore extends PersistentCookieStore {

    private static inFactCookieStore instance;

    private inFactCookieStore(Context context) {
        super(context);
    }

    public static inFactCookieStore getInstance(Context context) {
        if (instance == null) instance = new inFactCookieStore(context);

        return instance;
    }

}

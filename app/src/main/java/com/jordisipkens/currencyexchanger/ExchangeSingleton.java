package com.jordisipkens.currencyexchanger;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ExchangeSingleton {
    private static ExchangeSingleton instance;
    private RequestQueue requestQueue;

    private ExchangeSingleton() {
        requestQueue = getRequestQueue();
    }

    public static synchronized ExchangeSingleton getInstance() {
        if(instance == null) {
            instance = new ExchangeSingleton();
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

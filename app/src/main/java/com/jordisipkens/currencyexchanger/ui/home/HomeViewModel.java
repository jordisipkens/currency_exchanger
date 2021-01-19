package com.jordisipkens.currencyexchanger.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jordisipkens.currencyexchanger.ExchangeSingleton;
import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.network.GsonRequest;

import java.util.HashMap;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        super();
        mText = new MutableLiveData<>();
        loadCurrencies();
    }

    public LiveData<String> getText() {
        return mText;
    }

    private void loadCurrencies() {
        ExchangeSingleton.getInstance(MyApplication.getAppContext()).getRequestQueue().add(new StringRequest(Request.Method.GET,
                "https://api.exchangeratesapi.io/latest", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mText.setValue(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mText.setValue("Er ging iets fout");
            }
        }));
    }
}
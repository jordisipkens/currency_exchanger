package com.jordisipkens.currencyexchanger.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jordisipkens.currencyexchanger.ExchangeSingleton;
import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.network.GsonRequest;
import com.jordisipkens.currencyexchanger.network.objects.CurrencyRates;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<CurrencyRates> rates;

    public HomeViewModel() {
        super();
        mText = new MutableLiveData<>();
        rates = new MutableLiveData<>();
        loadCurrencies();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<CurrencyRates> getRates() {
        return rates;
    }

    private void loadCurrencies() {
        ExchangeSingleton.getInstance(MyApplication.getAppContext()).getRequestQueue().add(new GsonRequest<CurrencyRates>(
                "https://api.exchangeratesapi.io/latest", CurrencyRates.class, null, new Response.Listener<CurrencyRates>() {
            @Override
            public void onResponse(CurrencyRates response) {
                if(response != null) {
                    rates.setValue(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mText.setValue("Er ging iets fout");
            }
        }));
    }
}
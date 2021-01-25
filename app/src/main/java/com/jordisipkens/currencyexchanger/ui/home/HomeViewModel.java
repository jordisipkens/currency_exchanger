package com.jordisipkens.currencyexchanger.ui.home;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.NetworkError;
import com.jordisipkens.currencyexchanger.ExchangeSingleton;
import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.network.GsonRequest;
import com.jordisipkens.currencyexchanger.network.objects.CurrencyRates;

import java.util.Currency;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<CurrencyRates> rates = new MutableLiveData<>();
    private final MutableLiveData<String> baseCurrency = new MutableLiveData<>();
    private final MutableLiveData<String> givenCurrency = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showAll = new MutableLiveData<>();
    private final MutableLiveData<Double> baseAmount = new MutableLiveData<>();

    public HomeViewModel() {
        super();
        showAll.setValue(true);
        baseAmount.setValue(0.0);
    }

    public MutableLiveData<CurrencyRates> getRates() {
        return rates;
    }

    public void selectBaseCurrency(String currency) throws NetworkError {
        baseCurrency.setValue(currency);
        loadCurrencies();
    }

    public LiveData<String> getBaseCurrency() {
        return baseCurrency;
    }

    public MutableLiveData<String> getGivenCurrency() {
        return givenCurrency;
    }

    public MutableLiveData<Boolean> getShowAll() {
        return showAll;
    }

    public MutableLiveData<Double> getBaseAmount() {
        return baseAmount;
    }

    // If baseCurrency is not selected from the UI, use system base locale;
    public void loadCurrencies() throws NetworkError {
        MyApplication application = MyApplication.getInstance();
        if (application.isNetworkConnected()) { // check first if call is possible
            Locale current = application.getResources().getConfiguration().locale;
            String base = baseCurrency.getValue() != null ? baseCurrency.getValue() : Currency.getInstance(current).getCurrencyCode();

            ExchangeSingleton.getInstance().getRequestQueue().add(new GsonRequest<>(
                    "https://api.exchangeratesapi.io/latest?base=" + base, CurrencyRates.class, null, response -> {
                if (response != null) {
                    rates.setValue(response);
                }
            }, null));
        } else {
            throw (new NetworkError(new Throwable("Can't connect to host")));
        }
    }
}
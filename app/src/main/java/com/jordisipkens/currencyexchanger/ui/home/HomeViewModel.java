package com.jordisipkens.currencyexchanger.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.NetworkError;
import com.jordisipkens.currencyexchanger.BuildConfig;
import com.jordisipkens.currencyexchanger.ExchangeSingleton;
import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.network.GsonRequest;
import com.jordisipkens.currencyexchanger.network.objects.CurrencyRates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<CurrencyRates> rates = new MutableLiveData<>();
    private final MutableLiveData<String> baseCurrency = new MutableLiveData<>();
    private final MutableLiveData<String> givenCurrency = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showAll = new MutableLiveData<>();
    private final MutableLiveData<Double> baseAmount = new MutableLiveData<>();
    private final MutableLiveData<Date> date = new MutableLiveData<>();

    public HomeViewModel() {
        super();
        showAll.setValue(false);
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

    public MutableLiveData<Date> getDate() {
        return date;
    }

    // If baseCurrency is not selected from the UI, use system base locale;
    public void loadCurrencies() throws NetworkError {
        MyApplication application = MyApplication.getInstance();
        if (application.isNetworkConnected()) { // check first if call is possible
            Locale current = application.getResources().getConfiguration().locale;
            String base = baseCurrency.getValue() != null ? baseCurrency.getValue() : Currency.getInstance(current).getCurrencyCode();
            String url = BuildConfig.Base_URL + (date.getValue() != null ? formatDate() : "latest") + "?base=" + base;

            ExchangeSingleton.getInstance().getRequestQueue().add(new GsonRequest<>(
                    url, CurrencyRates.class, null, response -> {
                if (response != null) {
                    rates.setValue(response);
                }
            }, null));
        } else {
            throw (new NetworkError(new Throwable("Can't connect to host")));
        }
    }

    private String formatDate() {
        String dateFormat = "yyyy-MM-dd"; // format for the API
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault()); // use locale default
        Locale current = MyApplication.getAppContext().getResources().getConfiguration().locale;

        if(date.getValue() == null) {
            return sdf.format(Calendar.getInstance(current).getTime()); // format today to a readable string
        } else {
            return sdf.format(date.getValue().getTime()); // format chosen date to a readable string
        }
    }
}
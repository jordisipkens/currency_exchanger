package com.jordisipkens.currencyexchanger.ui.home;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Currency;

public class CurrencyAdapter extends ArrayAdapter<String> {

    public CurrencyAdapter(@NonNull Context context, int resource, String[] items) {
        super(context, resource, items);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        String item = super.getItem(position);
        Currency cInstance = Currency.getInstance(item);
        return cInstance.getSymbol();
    }
}

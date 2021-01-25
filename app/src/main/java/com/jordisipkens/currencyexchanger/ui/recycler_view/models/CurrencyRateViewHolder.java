package com.jordisipkens.currencyexchanger.ui.recycler_view.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.R;

import java.util.Currency;
import java.util.Locale;

public class CurrencyRateViewHolder extends RecyclerView.ViewHolder {

    public CurrencyRateViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bindView(@NonNull String given, @NonNull Double result){
        Locale locale = MyApplication.getAppContext().getResources().getConfiguration().locale;
        Currency cInstance = Currency.getInstance(given);

        String label = cInstance.getDisplayName(locale) + ": ";
        String formattedResult = cInstance.getSymbol() + " " + String.format(locale, "%."+ cInstance.getDefaultFractionDigits() + "f", (result));

        ((TextView) itemView.findViewById(R.id.text_base)).setText(label);
        ((TextView) itemView.findViewById(R.id.converted_rate_result)).setText(formattedResult);
    }
}

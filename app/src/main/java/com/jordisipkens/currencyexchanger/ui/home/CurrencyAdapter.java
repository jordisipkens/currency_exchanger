package com.jordisipkens.currencyexchanger.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.R;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

// Simple class to show the symbols corresponding to the currency
public class CurrencyAdapter extends ArrayAdapter<String> {

    public CurrencyAdapter(@NonNull Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            Locale locale = MyApplication.getAppContext().getResources().getConfiguration().locale;
            String item = super.getItem(position);
            Currency cInstance = Currency.getInstance(item);

            CurrencyViewHolder holder = new CurrencyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_search_view, parent, false));
            holder.bindView(cInstance.getDisplayName(locale) + ": " + cInstance.getSymbol());
            view = holder.itemView;
        }

        return view;
    }
}

class CurrencyViewHolder extends RecyclerView.ViewHolder {

    public CurrencyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bindView(@NonNull String input) {
        ((TextView) itemView.findViewById(R.id.text_value)).setText(input);
    }
}

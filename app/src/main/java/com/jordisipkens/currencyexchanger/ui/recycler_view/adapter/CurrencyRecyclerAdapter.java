package com.jordisipkens.currencyexchanger.ui.recycler_view.adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jordisipkens.currencyexchanger.R;
import com.jordisipkens.currencyexchanger.network.objects.CurrencyRates;
import com.jordisipkens.currencyexchanger.ui.recycler_view.models.CurrencyRateViewHolder;

import java.util.ArrayList;

public class CurrencyRecyclerAdapter extends RecyclerView.Adapter<CurrencyRateViewHolder> {

    @NonNull
    private CurrencyRates rates;
    @NonNull
    private String base;
    @NonNull
    private String given;
    private boolean showAll;
    @NonNull
    private Double baseAmount;

    public CurrencyRecyclerAdapter(@NonNull CurrencyRates rates, @NonNull String base, @NonNull String given, boolean showAll, @NonNull Double baseAmount) {
        this.rates = rates;
        this.base = base;
        this.given = given;
        this.showAll = showAll;
        this.baseAmount = baseAmount;
    }

    @NonNull
    @Override
    public CurrencyRateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CurrencyRateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyRateViewHolder holder, int position) {
        ArrayList<String> currencies = new ArrayList<>(rates.getRates().keySet());
        String viewHolderGiven = showAll ? currencies.get(position) : given;
        holder.bindView(viewHolderGiven, rates.calculateResult(baseAmount, base, viewHolderGiven));
    }

    @Override
    public int getItemCount() {
        return showAll ? rates.rates.size() : 1;
    }

    public void updateCurrencyRates(@NonNull CurrencyRates rates) {
        this.rates = rates;
        this.notifyDataSetChanged(); // Redraw recycler view because results have changed
    }

    public void setBaseAmount(@NonNull Double baseAmount) {
        this.baseAmount = baseAmount;
        this.notifyDataSetChanged(); // Redraw recycler view because results have changed
    }

    public void setBase(@NonNull String base) {
        this.base = base;
        this.notifyDataSetChanged(); // Redraw recycler view because results have changed
    }

    public void setGiven(@NonNull String given) {
        this.given = given;
        this.notifyDataSetChanged(); // Redraw recycler view because results have changed
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
        this.notifyDataSetChanged(); // Redraw recycler view because results have changed
    }
}

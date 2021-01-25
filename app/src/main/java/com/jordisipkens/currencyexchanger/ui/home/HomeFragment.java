package com.jordisipkens.currencyexchanger.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.R;
import com.jordisipkens.currencyexchanger.ui.recycler_view.adapter.CurrencyRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    // Create two datasets for two different adapters
    private final List<String> bCurrencies = new ArrayList<>();
    private final List<String> gCurrencies = new ArrayList<>();
    private CurrencyRecyclerAdapter recyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHomeViewObservers(root);
        setupViews(root);

        return root;
    }

    private void setHomeViewObservers(@NonNull View root) {
        homeViewModel.getRates().observe(getViewLifecycleOwner(), currencyRates -> {
            bCurrencies.clear();
            gCurrencies.clear();

            bCurrencies.addAll(currencyRates.rates.keySet());
            gCurrencies.addAll(currencyRates.rates.keySet());

            // Somehow when the base is EUR, the EUR isn't included in the rates
            // With every other base, the base itself is included in the rates with value 1.
            // However, make sure EUR is always on position 0 even if its already included;
            if (!currencyRates.base.equals("EUR")) {
                bCurrencies.remove("EUR");
            }
            bCurrencies.add(0, "EUR");
            gCurrencies.remove(currencyRates.base); // Remove the base, bit obvious that 5 USD = 5 USD for example;

            if (recyclerAdapter != null) {
                recyclerAdapter.updateCurrencyRates(currencyRates);
            }

            Spinner sCurrencies = root.findViewById(R.id.base_currency);
            CurrencyAdapter bAdapter = (CurrencyAdapter) sCurrencies.getAdapter();
            Spinner gsCurrencies = root.findViewById(R.id.given_currency);
            CurrencyAdapter gAdapter = (CurrencyAdapter) gsCurrencies.getAdapter();

            if (bAdapter == null || gAdapter == null) {
                setupBaseCurrencyAdapter();
                setupGivenCurrencyAdapter();
            } else {
                bAdapter.notifyDataSetChanged();
                gAdapter.notifyDataSetChanged();

                //Reselect if a value from before the current selected in the list is removed because it changed the base currency.
                gsCurrencies.setSelection(gCurrencies.indexOf(homeViewModel.getGivenCurrency().getValue()));
            }
        });

        homeViewModel.getBaseCurrency().observe(getViewLifecycleOwner(), base -> {
            if (recyclerAdapter != null) {
                recyclerAdapter.setBase(base);
            }
        });

        homeViewModel.getGivenCurrency().observe(getViewLifecycleOwner(), given -> {
            if (recyclerAdapter != null) {
                recyclerAdapter.setGiven(given);
            }
        });

        homeViewModel.getShowAll().observe(getViewLifecycleOwner(), showAll -> {
            if (recyclerAdapter != null) {
                recyclerAdapter.setShowAll(showAll);
            }
        });

        homeViewModel.getBaseAmount().observe(getViewLifecycleOwner(), baseAmount -> {
            if (recyclerAdapter != null) {
                recyclerAdapter.setBaseAmount(baseAmount);
            }
        });
    }

    private void setupViews(@NonNull View root) {
        EditText amountEditText = root.findViewById(R.id.amount);
        CheckBox showAllCheckBox = root.findViewById(R.id.convert_all_checkbox);
        Button exchangeButton = root.findViewById(R.id.exchange_button);

        Locale locale = MyApplication.getAppContext().getResources().getConfiguration().locale;

        showAllCheckBox.setChecked(homeViewModel.getShowAll().getValue());
        amountEditText.setText(String.format(locale, "%.2f", homeViewModel.getBaseAmount().getValue()));

        exchangeButton.setOnClickListener(v -> setupRecyclerView(root));

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(""))
                    homeViewModel.getBaseAmount().setValue(Double.parseDouble(s.toString().replace(",", ".")));
                else
                    homeViewModel.getBaseAmount().setValue(0.0);
            }
        });

        showAllCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> homeViewModel.getShowAll().setValue(isChecked));
    }

    private void setupRecyclerView(@NonNull View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerAdapter = new CurrencyRecyclerAdapter(homeViewModel.getRates().getValue(),
                homeViewModel.getBaseCurrency().getValue(), homeViewModel.getGivenCurrency().getValue(),
                homeViewModel.getShowAll().getValue(), homeViewModel.getBaseAmount().getValue());
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void setupBaseCurrencyAdapter() {
        View root = getView();
        Activity activity = getActivity();
        if (root != null && activity != null) {
            // Setup adapter for base currency
            CurrencyAdapter bCurrencyAdapter = new CurrencyAdapter(activity, android.R.layout.simple_spinner_item, bCurrencies);
            bCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner bCurrency = root.findViewById(R.id.base_currency); // base currency
            bCurrency.setAdapter(bCurrencyAdapter);

            bCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    homeViewModel.selectBaseCurrency(bCurrencies.get(position));
                    bCurrency.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    bCurrency.setSelection(0);
                }
            });

            // Set position based on viewmodel data
            // If data not set yet, use base of the device through the api data.
            String base = homeViewModel.getBaseCurrency().getValue() != null ? homeViewModel.getBaseCurrency().getValue() : homeViewModel.getRates().getValue().base;
            bCurrency.setSelection(bCurrencies.indexOf(base)); // Default it to the base currency
        }
    }

    private void setupGivenCurrencyAdapter() {
        View root = getView();
        Activity activity = getActivity();
        if (root != null && activity != null) {
            // Setup adapter for converting to given currency
            CurrencyAdapter gCurrencyAdapter = new CurrencyAdapter(activity, android.R.layout.simple_spinner_item, gCurrencies);
            gCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner gsCurrencies = root.findViewById(R.id.given_currency); // givenCurrency
            gsCurrencies.setAdapter(gCurrencyAdapter);

            gsCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    homeViewModel.getGivenCurrency().setValue(gCurrencies.get(position));
                    gsCurrencies.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    gsCurrencies.setSelection(0);
                }
            });

            gsCurrencies.setSelection(gCurrencies.indexOf(homeViewModel.getGivenCurrency().getValue()));
        }
    }
}
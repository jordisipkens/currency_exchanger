package com.jordisipkens.currencyexchanger.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.R;
import com.jordisipkens.currencyexchanger.network.objects.CurrencyRates;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private EditText datePicker;
    final Calendar myCalendar = Calendar.getInstance();
    private final List<String> currencies = new ArrayList<>();

    //  TODO Map the available currencies and get their symbols dynamic
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        homeViewModel.getRates().observe(getViewLifecycleOwner(), new Observer<CurrencyRates>() {
            @Override
            public void onChanged(CurrencyRates currencyRates) {
                currencies.clear();
                currencies.add(currencyRates.base);
                currencies.addAll(currencyRates.rates.keySet());

                Spinner sCurrencies = root.findViewById(R.id.original_currency);
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) sCurrencies.getAdapter();

                if (adapter != null) {
                    adapter.notifyDataSetInvalidated();
                } else {
                    setupCurrencyAdapter();
                }
            }
        });

        return root;
    }

    private void setupCurrencyAdapter() {
        View root = getView();
        Activity activity = getActivity();
        if (root != null && activity != null) {
            // Setup adapter for initial currency
            CurrencyAdapter iCurrencyAdapter = new CurrencyAdapter(activity, android.R.layout.simple_spinner_item, currencies.toArray(new String[0]));
            iCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner iCurrency = root.findViewById(R.id.original_currency); // initialCurrency
            iCurrency.setAdapter(iCurrencyAdapter);

            iCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("error", position + "");
                    Toast.makeText(parent.getContext(), "Selected: " + currencies.get(position), Toast.LENGTH_LONG).show();
                    iCurrency.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    iCurrency.setSelection(0);
                }
            });

            // Setup adapter for converting to given currency
            CurrencyAdapter gCurrencyAdapter = new CurrencyAdapter(activity, android.R.layout.simple_spinner_item, currencies.toArray(new String[0]));
            gCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner gCurrencies = root.findViewById(R.id.given_currency); // givenCurrency
            gCurrencies.setAdapter(gCurrencyAdapter);

            gCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("error", position + "");
                    Toast.makeText(parent.getContext(), "Selected: " + currencies.get(position), Toast.LENGTH_LONG).show();
                    gCurrencies.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    gCurrencies.setSelection(0);
                }
            });
        }
    }
}
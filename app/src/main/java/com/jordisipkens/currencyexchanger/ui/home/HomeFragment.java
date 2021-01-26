package com.jordisipkens.currencyexchanger.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.google.gson.Gson;
import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.R;
import com.jordisipkens.currencyexchanger.network.objects.CurrencyRates;
import com.jordisipkens.currencyexchanger.ui.recycler_view.adapter.CurrencyRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private static final String CURRENCIES_KEY = "currencies";


    private HomeViewModel homeViewModel;
    // Create two datasets for two different adapters
    private final List<String> bCurrencies = new ArrayList<>();
    private final List<String> gCurrencies = new ArrayList<>();
    private CurrencyRecyclerAdapter recyclerAdapter;
    private EditText datePicker;
    private final Calendar myCalendar = Calendar.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        checkForInitialInternet();

        setHomeViewObservers(root);
        setupViews(root);
        setupDatePicker(root);
        return root;
    }

    private void setHomeViewObservers(@NonNull View root) {
        homeViewModel.getRates().observe(getViewLifecycleOwner(), currencyRates -> {
            saveToSharedPreferences();

            bCurrencies.clear();
            gCurrencies.clear();

            // Somehow when the base is EUR, the EUR isn't included in the rates
            // With every other base, the base itself is included in the rates with value 1.
            // So include it when base = euro for offline use purposes
            if (currencyRates.base.equals("EUR")) {
                currencyRates.rates.put("EUR", 1.0);
            }

            bCurrencies.addAll(currencyRates.rates.keySet());
            gCurrencies.addAll(currencyRates.rates.keySet());

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
            View rootView = getView();
            if (rootView != null)
                rootView.findViewById(R.id.given_currency_layout).setVisibility(showAll ? View.GONE : View.VISIBLE);
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
        if (homeViewModel.getBaseAmount().getValue() != 0)
            amountEditText.setText(String.format(locale, "%.2f", homeViewModel.getBaseAmount().getValue()));

        exchangeButton.setOnClickListener(v -> {
            if (homeViewModel.getBaseAmount().getValue() == 0)
                amountEditText.setText("0"); // Set value visually

            setupRecyclerView(root);

            if (!MyApplication.getInstance().isNetworkConnected())
                Toast.makeText(getActivity(), "Host is not available or there is no internet connection, application is still usable but limited", Toast.LENGTH_LONG).show();

            hideKeyboard(getActivity());

        });

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

    private void setupDatePicker(@NonNull View root) {
        // Create a datePicker for the editText
        datePicker = (EditText) root.findViewById(R.id.date_picker);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date cached = myCalendar.getTime(); // only used when there is no internet connection;
                myCalendar.set(year, month, dayOfMonth);
                updateLabel();
                homeViewModel.getDate().setValue(myCalendar.getTime());

                try {
                    homeViewModel.loadCurrencies();
                } catch (NetworkError networkError) {
                    Toast.makeText(getActivity(), "Host is not available or there is no internet connection, application is still usable but limited.", Toast.LENGTH_LONG).show();
                    myCalendar.setTime(cached); // Reset date to previous
                    updateLabel();
                }
            }
        };

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        updateLabel();
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
                    try {
                        homeViewModel.selectBaseCurrency(bCurrencies.get(position));
                    } catch (NetworkError networkError) {
                        Toast.makeText(getActivity(), "Host is not available or there is no internet connection, application is still usable but limited", Toast.LENGTH_LONG).show();
                    }
                    bCurrency.setSelection(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    bCurrency.setSelection(0);
                }
            });

            bCurrencyAdapter.notifyDataSetChanged();// Sort arraylist before selecting value

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


            gCurrencyAdapter.notifyDataSetChanged(); // Sort arraylist before selecting value
            gsCurrencies.setSelection(gCurrencies.indexOf(homeViewModel.getGivenCurrency().getValue()));
        }
    }

    private boolean tryAndLoadDataFromStorage() {
        SharedPreferences prefs = MyApplication.getInstance().getSharedPreferences("MyExchanger", Context.MODE_PRIVATE);
        String currenciesString = prefs.getString(CURRENCIES_KEY, null);
        if (currenciesString != null) {
            CurrencyRates rates = new Gson().fromJson(currenciesString, CurrencyRates.class);
            homeViewModel.getRates().setValue(rates);
            homeViewModel.getDate().setValue(rates.getDate());
            myCalendar.setTime(rates.getDate());
            return true;
        } else
            return false;
    }

    private void saveToSharedPreferences() {
        SharedPreferences prefs = MyApplication.getInstance().getSharedPreferences("MyExchanger", Context.MODE_PRIVATE);
        prefs.edit().putString(CURRENCIES_KEY, new Gson().toJson(homeViewModel.getRates().getValue())).apply();
    }

    private void updateLabel() {
        String dateFormat = "dd-MM-yyyy"; // Choose which format you want it to display
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault()); // use locale default

        datePicker.setText(sdf.format(myCalendar.getTime())); // format chosen datePicker to a readable string
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void checkForInitialInternet() {
        try {
            homeViewModel.loadCurrencies();
        } catch (NetworkError networkError) {
            if (!tryAndLoadDataFromStorage())
                new AlertDialog.Builder(getActivity())
                        .setMessage("Can't connect to host, check your internet connection or try again later.")
                        .setCancelable(false)
                        .setNeutralButton("Try again", (dialog, which) -> {
                            dialog.dismiss();
                            checkForInitialInternet();
                        })
                        .create()
                        .show();
        }
    }
}
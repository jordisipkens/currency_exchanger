package com.jordisipkens.currencyexchanger.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jordisipkens.currencyexchanger.MyApplication;
import com.jordisipkens.currencyexchanger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private EditText datePicker;
    final Calendar myCalendar = Calendar.getInstance();

    //  TODO Map the available currencies and get their symbols dynamic
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        // Create a datePicker for the editText
        datePicker = (EditText) root.findViewById(R.id.date_picker);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(year, month, dayOfMonth);
                updateLabel();
            }
        };

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // TODO Convert this to a viewModel, so it saves your preferences when you switch or tab out.
        updateLabel(); // Make sure to update the datepicker to today;

        return root;
    }

    private void updateLabel() {
        String dateFormat = "dd-MM-yyyy"; // Choose which format you want it to display
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault()); // use locale default

        datePicker.setText(sdf.format(myCalendar.getTime())); // format chosen datePicker to a readable string
    }
}
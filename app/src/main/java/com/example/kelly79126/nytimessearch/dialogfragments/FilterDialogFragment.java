package com.example.kelly79126.nytimessearch.dialogfragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kelly79126.nytimessearch.R;

import java.sql.Date;

/**
 * Created by kelly79126 on 2017/2/21.
 */

public class FilterDialogFragment extends DialogFragment
                                implements Button.OnClickListener, DatePickerDialog.OnDateSetListener{

    private TextView tvBeginDate;
    private Spinner spSortOrder;
    private Button btnSave;

    public interface FilterDialogListener {
        void onFinishFilterDialog(String inputText);
    }


    public FilterDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterDialogFragment newInstance(String title) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBeginDate = (TextView) view.findViewById(R.id.tv_begin_date);
        spSortOrder = (Spinner) view.findViewById(R.id.sp_sort_order);
        btnSave = (Button) view.findViewById(R.id.btn_save);

        tvBeginDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_begin_date:
                showDatePickerDialog(v);
                break;
            case R.id.btn_save:
                FilterDialogListener listener = (FilterDialogListener) getActivity();
                listener.onFinishFilterDialog(spSortOrder.getSelectedItem().toString());
                dismiss();
                break;
        }
    }


//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        // request a window without the title
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }

    public void showDatePickerDialog(View v) {
        FragmentManager fm = getFragmentManager();
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setTargetFragment(this, 10);
        newFragment.show(fm, "datePicker");
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        showDateFormat(year, monthOfYear, dayOfMonth);
    }

    public void showDateFormat(int year, int month, int day) {
        Date chosenDate = new Date(year-1900, month, day);
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(getContext());
        tvBeginDate.setText(dateFormat.format(chosenDate));
    }
}
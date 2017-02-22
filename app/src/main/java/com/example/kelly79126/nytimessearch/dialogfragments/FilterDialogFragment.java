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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kelly79126.nytimessearch.R;

import java.sql.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kelly79126 on 2017/2/21.
 */

public class FilterDialogFragment extends DialogFragment
                                implements Button.OnClickListener, DatePickerDialog.OnDateSetListener{

    @BindView(R.id.tv_begin_date) TextView tvBeginDate;
    @BindView(R.id.sp_sort_order) Spinner spSortOrder;
    Button btnSave;
    @BindView(R.id.checkbox_fashion_style) CheckBox checkFashion;
    @BindView(R.id.checkbox_arts) CheckBox checkArts;
    @BindView(R.id.checkbox_sports) CheckBox checkSports;

    private String mStrBeginDate;
    private Unbinder unbinder;

    public interface FilterDialogListener {
        void onFinishFilterDialog(String beginDate, String sortOrder, String newsDesk);
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
        View view = inflater.inflate(R.layout.dialog_filter, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = (Button) view.findViewById(R.id.btn_save);
        tvBeginDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_begin_date:
                showDatePickerDialog(v);
                break;
            case R.id.btn_save:
                FilterDialogListener listener = (FilterDialogListener) getActivity();
                listener.onFinishFilterDialog(mStrBeginDate, spSortOrder.getSelectedItem().toString(), checkCheckBox());
                dismiss();
                break;
        }
    }

    public String checkCheckBox(){
        String newsdesk = "";

        if(checkArts.isChecked())
            newsdesk = newsdesk + "\" Arts\"";
        if(checkFashion.isChecked())
            newsdesk = newsdesk + "\" Fashion & Style\"";
        if(checkSports.isChecked())
            newsdesk = newsdesk + "\" Sports\"";

        if(!newsdesk.isEmpty()){
            newsdesk = "(news_desk:(" + newsdesk + "))";
        }

        return newsdesk;
    }

    public void showDatePickerDialog(View v) {
        FragmentManager fm = getFragmentManager();
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setTargetFragment(this, 10);
        newFragment.show(fm, "datePicker");
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date chosenDate = new Date(year-1900, monthOfYear, dayOfMonth);
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(getContext());
        tvBeginDate.setText(dateFormat.format(chosenDate));
        mStrBeginDate = "" + year + String.format("%02d", monthOfYear+1) + String.format("%02d", dayOfMonth);
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        // request a window without the title
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }
}
package com.example.kelly79126.nytimessearch.dialogfragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by kelly79126 on 2017/2/23.
 */

public class NoInternetDialogFragment extends DialogFragment {
    public NoInternetDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static NoInternetDialogFragment newInstance(String title) {
        NoInternetDialogFragment frag = new NoInternetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Please make sure your internet is available");
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return alertDialogBuilder.create();
    }

}

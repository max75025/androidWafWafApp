package com.wafwaf.wafwaf;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class deleteAccountDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDeleteDialogStyle);
        builder.setMessage("вы уверены что хотите удалить аккаунт?")
                .setPositiveButton("удалить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (MainActivity.accountName!=null){
                            mListener.onDialogDeleteAccountPositiveClick(deleteAccountDialog.this);
                        }else{

                        }

                    }
                })
                .setNegativeButton("отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface deleteDialogListener {
        public void onDialogDeleteAccountPositiveClick(DialogFragment dialog);
    }

    deleteDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (deleteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}

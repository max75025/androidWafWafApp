package com.wafwaf.wafwaf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewSiteDialogFragment extends DialogFragment {
    addSiteDialogListener listener;
    View view;
    EditText  site;
    EditText api;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


         view = inflater.inflate(R.layout.add_site_dialog, null);

          site =  view.findViewById(R.id.site);
         api = view.findViewById(R.id.api);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton(getString(R.string.add_site_dialog_positive_button), null)
                .setNegativeButton(getString(R.string.add_site_dialog_negative_button) ,null);

        return builder.create();
    }

    public interface addSiteDialogListener {
        void onFinishAddSiteDialog(String site, String api);
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }


    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    protected void onAttachToContext(Context context) {
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditNameDialogListener so we can send events to the host
            listener = (addSiteDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement EditNameDialogListener");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            //напрямую переопределяем слушателя для кнопки  для предотвращения закрытия диалога
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String siteStr = site.getText().toString();
                    String apiStr = api.getText().toString();


                    if (siteStr.equals("") || apiStr.equals("")){
                        //уведомления если не все поля заполнены
                        Toast massage = Toast.makeText(getActivity(),getString(R.string.toast_fill_all_fields), Toast.LENGTH_LONG);
                        massage.show();
                        //listener.onFinishAddSiteDialog(siteStr, apiStr);
                    }else if(apiStr.length()!=128){
                        //уведомления если не верный апи ключ
                        Toast massage = Toast.makeText(getActivity(),getString(R.string.toast_wrong_api_key), Toast.LENGTH_LONG);
                        massage.show();
                    }else if(!siteStr.equals("") && !apiStr.equals("")){
                        listener.onFinishAddSiteDialog(siteStr, apiStr);
                        dialog.dismiss();
                    }


                }
            });
            Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
}

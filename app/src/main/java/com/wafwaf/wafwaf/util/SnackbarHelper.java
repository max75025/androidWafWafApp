package com.wafwaf.wafwaf.util;

import com.google.android.material.snackbar.Snackbar;
import android.view.View;

public   class SnackbarHelper {

    public static void showSnackbar(View rootView, String text, int time, int textColor, int backgroundColor){
        Snackbar snackbar = Snackbar.make(rootView, text,time);
        snackbar.setActionTextColor(textColor);
        View view = snackbar.getView();
        view.setBackgroundResource(backgroundColor);
       // TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
        //tv.setTextColor(textColor);
        snackbar.show();
    }

}

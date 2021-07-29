package com.w3engineers.mesh.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;

import com.w3engineers.mesh.R;


public class DialogUtil {
    static ProgressDialog progressDialog;
    static AlertDialog alertDialog;

    public static AlertDialog showConfirmationDialog(Context context,
                                              String title,
                                              String message,
                                              String negativeText,
                                              String positiveText,
                                              final DialogButtonListener listener) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.DefaultAlertDialogStyle);
            alertDialogBuilder.setTitle(Html.fromHtml("<b>" + title + "</b>"));
            alertDialogBuilder.setMessage(Html.fromHtml("<font color='#757575'>" + message + "</font>"));


            if (negativeText != null) {
                alertDialogBuilder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener !=null) {
                            listener.onClickNegative();
                        }
                    }
                });
            }

            if (positiveText != null) {
                alertDialogBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (listener !=null){
                            listener.onClickPositive();
                        }


                    }
                });
            }

            alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (listener !=null) {
                        listener.onCancel();
                    }
                }
            });
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return alertDialog;
    }

    public interface DialogButtonListener {
        void onClickPositive();

        void onCancel();



        void onClickNegative();
    }

    public static void showLoadingProgress(Context context) {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = null;
            }
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Telemesh is connection with service...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog() {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void dismissLoadingProgress() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}

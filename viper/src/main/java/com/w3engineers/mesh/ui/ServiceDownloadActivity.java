package com.w3engineers.mesh.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.w3engineers.mesh.R;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.util.TSAppInstaller;
import com.w3engineers.mesh.util.Util;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;

/**
 * Created by Azizul Islam on 10/22/21.
 */
public class ServiceDownloadActivity extends AppCompatActivity implements ProgressListener {
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private ConstraintLayout buttonView;
    private ProgressBar progressBar;
    boolean isDownloading = false;
    private TextView downloadLabelTextView;
    private ProgressDialog progressDialog;
    private boolean isServiceAppApkExist;
    private Button serviceDownloadButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_download);
        buttonView = findViewById(R.id.btn_view);
        progressBar = findViewById(R.id.pb_download);
        downloadLabelTextView = findViewById(R.id.label_text);
        serviceDownloadButton = findViewById(R.id.btn_download);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isServiceAppApkExist = TSAppInstaller.isServiceAppApkExist(this);

        if (isServiceAppApkExist) {
            long serviceAppDownloadTime = TSAppInstaller.getServiceAppDownloadTime();
            long timeDifference = System.currentTimeMillis() - serviceAppDownloadTime;
            int second = (int) (timeDifference / 1000);
            Log.e("Service_app", "Download time difference :" + second);

            if (second >= 3600) {
                isServiceAppApkExist = false;
            } else {
                serviceDownloadButton.setText("Install Service App");
            }

        }

    }

    public void downloadServiceApp(View view) {
        if (isServiceAppApkExist) {
            TSAppInstaller.installServiceApp(this);
            finish();
        } else {
            checkPermissionAndTriggerDownload();
        }
    }

    public void onDownloadLater(View view) {
        finishAffinity();
        System.exit(0);
    }

    public void onClickGetServiceFromFriend(View view) {
        Toast.makeText(this, "Need to set a valid text message", Toast.LENGTH_LONG).show();
    }

    private void checkPermissionAndTriggerDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                downloadServiceAppAfterPermissionCheck();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        } else {
            downloadServiceAppAfterPermissionCheck();
        }
    }

    private void downloadServiceAppAfterPermissionCheck() {
        toggleProgressDialog(true);
        Util.isConnected(isConnected ->
                HandlerUtil.postForeground(() -> {
                    if (isConnected) {
                        TSAppInstaller.downloadApkFile(getApplicationContext(),
                                SharedPref.read(Constant.PreferenceKeys.APP_DOWNLOAD_LINK), this);
                    } else {
                        toggleProgressDialog(false);
                        Toast.makeText(this, "Internet connection required....", Toast.LENGTH_LONG).show();
                    }
                })

        );
    }


    private void toggleProgressDialog(boolean needToShow) {
        if (needToShow) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissionAndTriggerDownload();
        }
    }

    private void showHideView(boolean isNeedToShowProgressView) {
        if (isNeedToShowProgressView) {
            if (progressBar.getVisibility() != View.VISIBLE) {
                buttonView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                downloadLabelTextView.setText(getText(R.string.label_downloading));
            }
        } else {
            buttonView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            downloadLabelTextView.setText(getText(R.string.label_download_requires));
        }
    }


    @Override
    public void onDownloadProgress(int progress) {
        toggleProgressDialog(false);
        showHideView(true);
        isDownloading = true;
        progressBar.setProgress(progress);
        if (progress >= 100) {
            finish();
            isDownloading = false;
        }
    }

    @Override
    public void onErrorOccurred(String errorText) {
        runOnUiThread(() -> {
            Toast.makeText(ServiceDownloadActivity.this, errorText, Toast.LENGTH_LONG).show();
            toggleProgressDialog(false);
            showHideView(false);
            isDownloading = false;
        });

    }

    @Override
    public void onBackPressed() {

        if (isDownloading) {
            Toast.makeText(this, "Service app is downloading. Please wait...", Toast.LENGTH_LONG).show();
        }
        /*if (!isDownloading) {
            super.onBackPressed();
        }*/
    }
}

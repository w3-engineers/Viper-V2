package com.w3engineers.mesh.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    private TextView progressView;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_download);
        progressView = findViewById(R.id.tv_progress);
    }

    public void downloadServiceApp(View view) {
        checkPermissionAndTriggerDownload();
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
        Util.isConnected(isConnected ->
                HandlerUtil.postForeground(() -> {
                    if (isConnected) {
                        TSAppInstaller.downloadApkFile(getApplicationContext(),
                                SharedPref.read(Constant.PreferenceKeys.APP_DOWNLOAD_LINK), this);
                    }
                })

        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissionAndTriggerDownload();
        }
    }


    @Override
    public void onDownloadProgress(int progress) {
        progressView.setText("Download progress :" + progress);
    }
}

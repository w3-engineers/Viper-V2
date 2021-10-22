package com.w3engineers.mesh.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_download);
        progressView = findViewById(R.id.tv_progress);
    }

    public void downloadServiceApp(View view) {
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
    public void onDownloadProgress(int progress) {
        progressView.setText("Download progress :" + progress);
    }
}

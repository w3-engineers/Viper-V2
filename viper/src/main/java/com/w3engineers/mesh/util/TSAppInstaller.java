package com.w3engineers.mesh.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.w3engineers.mesh.R;
import com.w3engineers.mesh.ui.ProgressListener;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.mesh.util.lib.remote.RetrofitInterface;
import com.w3engineers.mesh.util.lib.remote.RetrofitService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


public class TSAppInstaller {
    private static DownloadZipFileTask downloadZipFileTask;
    private static final String TAG = "appDownloadTest";
    public static boolean isAppUpdating;
    private static ProgressListener progressListener;
    //private static AlertDialog dialog;
    //private static ProgressBar progressBar;

    public static void downloadApkFile(Context context, String baseUrl, ProgressListener listener) {

        progressListener = listener;
        //showDialog(MeshApp.getCurrentActivity());

        Log.d(TAG, "File url: " + baseUrl);

        RetrofitInterface downloadService = RetrofitService.createService(RetrofitInterface.class, baseUrl, null);
        Call<ResponseBody> call = downloadService.downloadFileByUrl("Service.apk");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Got response body");

                    downloadZipFileTask = new DownloadZipFileTask(context);
                    downloadZipFileTask.execute(response.body());

                } else {
                    Log.d(TAG, "Connection failed " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressListener.onErrorOccurred(t.getMessage());
                t.printStackTrace();
                // closeDialog(context, t.getMessage());
                Log.e(TAG, t.getMessage());
                //   isAppUpdating = false;
                //  InAppUpdate.getInstance(App.getContext()).setAppUpdateProcess(false);
            }
        });
    }


    private static class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {
        private Context context;

        public DownloadZipFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            saveToDisk(context, urls[0], "TeleService.apk");
            return null;
        }

        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            Log.d("Download_Progress", " progress : " + progress[0].second);

            if (progress[0].first == 100) {
                Toast.makeText(context, "File downloaded successfully", Toast.LENGTH_SHORT).show();
            }


            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
                progressListener.onDownloadProgress(currentProgress);
                /*//progressBar.setProgress(currentProgress);
                if (progressBar != null) {
                    progressBar.setProgress(currentProgress);
                }*/

                // txtProgressPercent.setText("Progress " + currentProgress + "%");
            }

            if (progress[0].first == -1) {
                //closeDialog(context, "Download failed");
                progressListener.onErrorOccurred("Download failed");
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                File destinationFile = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    destinationFile = new File(context.getExternalFilesDir(""), "TeleService.apk");
                } else {
                    destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "TeleService.apk");
                }
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String packageName = context.getPackageName() + ".provider";
                    Uri apkUri = FileProvider.getUriForFile(context, packageName, destinationFile);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    Log.d("InAppUpdateTest", "app uri: " + apkUri.getPath());
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Log.d("InAppUpdateTest", "app install process start");
                } else {
                    Uri apkUri = Uri.fromFile(destinationFile);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                //closeDialog(context, "Download completed");
                context.startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
                //closeDialog(context, e.getMessage());
                progressListener.onErrorOccurred(e.getMessage());
            }


            //    isAppUpdating = false;
            //    InAppUpdate.getInstance(App.getContext()).setAppUpdateProcess(false);
        }
    }

    private static void saveToDisk(Context context, ResponseBody body, String filename) {
        try {
            File destinationFile = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                destinationFile = new File(context.getExternalFilesDir(""), filename);
            } else {
                destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
                Log.d(TAG, "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadZipFileTask.doProgress(pairs);
                    Log.d("Download_Progress", "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                //  Log.d(TAG, destinationFile.getParent());

                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadZipFileTask.doProgress(pairs);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadZipFileTask.doProgress(pairs);
                //closeDialog(context, "Failed to save the file!");
                progressListener.onErrorOccurred("Failed to save the file!");
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            progressListener.onErrorOccurred("Failed to save the file!");
            //closeDialog(context, "Failed to save the file!");
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }
/*
    private static void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_service_app_install_progress, null);

        progressBar = view.findViewById(R.id.progressBar);

        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private static void closeDialog(Context context, String message) {
        HandlerUtil.postForeground(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
    }*/
}

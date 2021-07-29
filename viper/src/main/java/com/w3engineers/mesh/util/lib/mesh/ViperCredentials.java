package com.w3engineers.mesh.util.lib.mesh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViperCredentials {
    @Nullable
    private static ViperCredentials viperCredentials;

    private ViperCredentials() {
    }

    @NonNull
    public static ViperCredentials getInstance() {
        if (viperCredentials == null) {
            viperCredentials = new ViperCredentials();
        }
        return viperCredentials;
    }

    public native String getAuthUserName();

    public native String getAuthPassword();

    public native String getFileRepoLink();

    static {
        System.loadLibrary("viper-lib");
    }

}

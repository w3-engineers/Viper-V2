package com.w3engineers.mesh.application.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WalletPrepared extends Event implements Parcelable {
    public boolean success;
    public boolean isOldAccount;

    public WalletPrepared() {
    }

    protected WalletPrepared(Parcel in) {
        success = in.readByte() != 0;
        isOldAccount = in.readByte() != 0;
    }

    public static final Creator<WalletPrepared> CREATOR = new Creator<WalletPrepared>() {
        @Override
        public WalletPrepared createFromParcel(Parcel in) {
            return new WalletPrepared(in);
        }

        @Override
        public WalletPrepared[] newArray(int size) {
            return new WalletPrepared[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeByte((byte) (isOldAccount ? 1 : 0));
    }
}

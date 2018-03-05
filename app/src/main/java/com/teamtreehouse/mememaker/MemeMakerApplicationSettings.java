package com.teamtreehouse.mememaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teamtreehouse.mememaker.utils.StorageType;

public class MemeMakerApplicationSettings {
    public static final String PREFS_STORAGE = "storage";
    private SharedPreferences sharedPreferences;

    public MemeMakerApplicationSettings(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public String getStoragePreference() {
        return sharedPreferences.getString(PREFS_STORAGE, StorageType.INTERNAL);
    }

    public void setStoragePreferences(String storageType) {
        sharedPreferences.edit()
                .putString(PREFS_STORAGE, storageType)
                .apply();
    }
}

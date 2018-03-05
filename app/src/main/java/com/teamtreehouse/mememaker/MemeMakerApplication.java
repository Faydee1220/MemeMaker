package com.teamtreehouse.mememaker;

import android.preference.Preference;
import android.preference.PreferenceManager;
import com.teamtreehouse.mememaker.utils.FileUtilities;

public class MemeMakerApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FileUtilities.saveAssetImage(this, "dogmess.jpg");
        FileUtilities.saveAssetImage(this, "excitedcat.jpg");
        FileUtilities.saveAssetImage(this, "guiltypup.jpg");

        // 第三個參數設 false 代表預設值只會在未初始化時設置，避免覆寫用戶設定
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}

package com.cinque.cropdisease;

import android.os.Bundle;
import com.cinque.cropdisease.plugins.CropDiseasePlugin;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(CropDiseasePlugin.class);
        super.onCreate(savedInstanceState);
    }
}
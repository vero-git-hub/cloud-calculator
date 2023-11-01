package com.example.cloudcalc.badge.ignored;

import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.FileOperationManager;

public class IgnoreManager {

    private final UICallbacks uiCallbacks;
    private final IgnoreScreen ignoreScreen;
    private final FileOperationManager fileOperationManager;

    public IgnoreManager(UICallbacks uiCallbacks, FileOperationManager fileOperationManager) {
        this.uiCallbacks = uiCallbacks;
        this.ignoreScreen = new IgnoreScreen(this);
        this.fileOperationManager = fileOperationManager;
    }

    public UICallbacks getUiCallbacks() {
        return uiCallbacks;
    }

    public IgnoreScreen getIgnoreScreen() {
        return ignoreScreen;
    }

    public FileOperationManager getFileOperationManager() {
        return fileOperationManager;
    }
}

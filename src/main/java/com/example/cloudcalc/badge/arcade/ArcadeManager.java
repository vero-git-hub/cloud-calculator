package com.example.cloudcalc.badge.arcade;

import com.example.cloudcalc.UICallbacks;
import com.example.cloudcalc.badge.FileOperationManager;

public class ArcadeManager {

    private final UICallbacks uiCallbacks;
    private final ArcadeScreen arcadeScreen;
    private final FileOperationManager fileOperationManager;

    public ArcadeManager(UICallbacks uiCallbacks, FileOperationManager fileOperationManager) {
        this.uiCallbacks = uiCallbacks;
        this.arcadeScreen = new ArcadeScreen(this);
        this.fileOperationManager = fileOperationManager;
    }

    public UICallbacks getUiCallbacks() {
        return uiCallbacks;
    }

    public ArcadeScreen getArcadeScreen() {
        return arcadeScreen;
    }

    public FileOperationManager getFileOperationManager() {
        return fileOperationManager;
    }
}

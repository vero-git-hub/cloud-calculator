package com.example.cloudcalc.language;

import com.example.cloudcalc.builder.TableBuilder;

import java.util.ResourceBundle;

public interface Localizable {
    void updateLocalization(ResourceBundle bundle);

    default void updateElements(String newTitle, String newAddScreenTitle, ResourceBundle bundle) {
//        TableBuilder.updateTitle(newTitle);
//        TableBuilder.updateAddScreenTitle(newAddScreenTitle);
        TableBuilder.updateNotificationAlert(
                bundle.getString("nameAlertTitle"),
                bundle.getString("nameAlertHeader"),
                bundle.getString("nameAlertContent")
        );
        TableBuilder.updateDeleteAlert(
                bundle.getString("alertTitleDeleteBadge"),
                bundle.getString("alertHeaderDeleteBadge"),
                bundle.getString("alertContentDeleteBadge")
        );

    }
}

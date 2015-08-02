package de.xsrc.palaver.utils;

import javafx.application.Platform;

/**
 * Created by user on 01.09.14.
 */
public class Notifications {

    private static boolean enabled = true;

    public static synchronized void setEnabled(boolean b) {
        enabled = b;
    }

    public static void notify(String from, String body) {
        if (Notifications.enabled) {

            Platform.runLater(() -> {
                org.controlsfx.control.Notifications.create()
                        .title(from)
                        .text(body)
                        .showInformation();
            });
        }
    }
}

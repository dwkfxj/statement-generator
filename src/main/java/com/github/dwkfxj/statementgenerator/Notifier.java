package com.github.dwkfxj.statementgenerator;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notifier {


    public static void error(String content, Project project) {
        notify(content, NotificationType.ERROR, project);
    }

    public static void warn(String content, Project project) {
        notify(content, NotificationType.WARNING, project);
    }

    public static void info(String content, Project project) {
        notify(content, NotificationType.INFORMATION, project);
    }

    public static void notify(String content, NotificationType type, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("com.github.dwkfxj.statementgenerator.NotificationGroup")
                .createNotification(content, type)
                .notify(project);
    }
}

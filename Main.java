package com.mercadolibre.loyal_notifications;

import com.google.inject.Guice;
import com.mercadolibre.app.Application;
import com.mercadolibre.config.Config;
import com.mercadolibre.loyal_notifications.configuration.ApplicationModule;
import com.mercadolibre.loyal_notifications.controllers.MailController;
import com.mercadolibre.loyal_notifications.controllers.NotificationMessageController;
import com.mercadolibre.loyal_notifications.controllers.FloatNotificationController;
import com.mercadolibre.loyal_notifications.exceptions.CustomErrorHandler;

import static spark.Spark.*;

public class Main extends Application {

    private NotificationMessageController notificationMessageController;
    private FloatNotificationController floatNotificationController;
    private MailController mailController;

    public static void main(String[] args) {
        new Main().init();
    }

    public Main() {
        super();
    }

    @Override
    public void init() {
        Config.addInjector(APP, Guice.createInjector(
                new ApplicationModule()));

        this.initControllers();
        super.init();

        //override error handlers
        getInstance(CustomErrorHandler.class).register();
    }

    private void initControllers() {
        this.notificationMessageController = getInstance(NotificationMessageController.class);
        this.floatNotificationController = getInstance(FloatNotificationController.class);
        this.mailController = getInstance(MailController.class);
    }


    @Override
    protected void configure() { }

    @Override
    public void addRoutes() {
        post("/feeds/notification", this.notificationMessageController::consume);
        get("/notifications/:userId", this.floatNotificationController::get);
        delete("/notifications/:userId", this.floatNotificationController::markAsRead);
        post("/notifications/send/mail/:template", this.mailController::send);

        after("/*", (request, response) -> response.type("application/json"));
    }
}


package de.xsrc.palaver;


import de.xsrc.palaver.controller.ContactController;
import de.xsrc.palaver.controller.MainController;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.utils.Notifications;
import de.xsrc.palaver.utils.UiUtils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import de.xsrc.palaver.xmpp.listeners.AccountChangeListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.control.cell.ServiceListCellFactory;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.FlowActionChain;
import org.datafx.controller.flow.action.FlowLink;
import org.datafx.controller.flow.action.FlowMethodAction;

import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class
            .getName());

    static {
        Font.loadFont(FontAwesome.class.getResource("/font/fontawesome-webfont.ttf").toExternalForm(), 24);
        Font.loadFont(Main.class.getResource("/font/Roboto-Regular.ttf").toExternalForm(), 24);
        Font.loadFont(Main.class.getResource("/font/Roboto-Bold.ttf").toExternalForm(), 24);

    }

    private ApplicationContext applicationContext = ApplicationContext.getInstance();
    private ConnectionManager connections = new ConnectionManager();

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FlowException {
        AccountProvider accounts = new AccountProvider();


        applicationContext.register(accounts);
        final ObservableExecutor executor = new ObservableExecutor();
        applicationContext.register(executor);
        applicationContext.register(connections);


        Flow flow = new Flow(MainController.class);
        flow.withAction(ContactController.class, "startPalaverButton", new FlowActionChain(new FlowMethodAction("startPalaverAction"), new FlowLink<>(MainController.class)));
        flow.withAction(ContactController.class, "contactListView", new FlowActionChain(new FlowMethodAction("startPalaverAction"), new FlowLink<>(MainController.class)));
        flow.withAction(ContactController.class, "addContactButton", new FlowActionChain(new FlowMethodAction("addContactAction"), new FlowLink<>(MainController.class)));
        Scene scene = UiUtils.prepareFlow(flow, null);

        primaryStage.setScene(scene);

        primaryStage.show();
//		showDebug();
        primaryStage.focusedProperty().addListener((ond, old, n) -> Notifications.setEnabled(!n));
        Platform.runLater(() -> {
            accounts.retrieve();
            accounts.getData().addListener(new AccountChangeListener(executor, connections));
        });

    }

    private void showDebug() {
        ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);

        ListView<Service<?>> serviceListView = new ListView<>();
        serviceListView.setCellFactory(new ServiceListCellFactory());

        serviceListView.itemsProperty().bind(executor.currentServicesProperty());


        Scene scene = new Scene(serviceListView);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(200);
        stage.show();

    }
}

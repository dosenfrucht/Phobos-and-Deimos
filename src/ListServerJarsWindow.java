import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class ListServerJarsWindow {

    static Stage window;
    static  File serverJar;

    public static File display() {
        window = new Stage();
        window.setTitle("Select server-jar");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(false);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);

        TableView table = new TableView();
        table.setPrefSize(300, 400);
        TableColumn name = new TableColumn();
        TableColumn type = new TableColumn();
        table.getItems().addAll(name, type);

        HBox btnpane = new HBox(50);

        FileChooser jarChooser = new FileChooser();

        HBox leftbtns = new HBox(10);
        leftbtns.setAlignment(Pos.CENTER_LEFT);
        Button refresh = new Button("Refresh");
        refresh.setPrefSize(90, 30);
        Button browse = new Button("...");
        browse.setPrefSize(50, 30);
        browse.setOnAction(e -> {
            File jar = jarChooser.showOpenDialog(window);
            if (jar != null) {
                if (jar.getName().endsWith(".jar")) {
                    serverJar = jar;
                    window.close();
                } else {
                    AlertWindow.display("Select server-jar", "The server-jar has to be a JAR", Alert.AlertType.ERROR);
                }
            }
        });
        leftbtns.getChildren().addAll(refresh, browse);

        HBox rightbtns = new HBox(10);
        rightbtns.setAlignment(Pos.CENTER_RIGHT);
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> window.close());
        cancel.setPrefSize(80, 30);
        Button ok = new Button("OK");
        ok.setPrefSize(50, 30);
        ok.setOnAction(e -> {
            if (serverJar != null) {
                window.close();
            } else {
                AlertWindow.display("Selecr server-jar", "Please select a server-jar from the list or browse for one on your PC manually", Alert.AlertType.ERROR);
            }

        });
        rightbtns.getChildren().addAll(cancel, ok);

        btnpane.getChildren().addAll(leftbtns, rightbtns);

        layout.getChildren().addAll(table, btnpane);

        window.showAndWait();
        return serverJar;
    }

}

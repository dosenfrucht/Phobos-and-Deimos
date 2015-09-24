import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.instance.ServerInstance;

import java.io.*;

public class CreateInstanceWindow {

    static Stage window;

    public static void display() {

        window = new Stage();
        window.setTitle("Create new instance");
        window.minHeightProperty().set(250);
        window.minWidthProperty().set(300);
        window.setResizable(false);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        //layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);

        InstanceContainer tmp = new InstanceContainer();
        ServerInstance si = tmp.getInstance();

        Image unknown = null;
        try {
            unknown = new Image(new FileInputStream(new File("./assets/unknown_server.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView serverimg = new ImageView(unknown);
        serverimg.setFitHeight(64);
        serverimg.setFitWidth(64);
        serverimg.setStyle(" -fx-border-color: black; -fx-border-width: 2;");
        StackPane imgpane = new StackPane(serverimg);
        imgpane.setAlignment(Pos.CENTER);

        VBox name = new VBox();
        Label namelb = new Label("Server name");
        TextField nametf = new TextField();
        nametf.setPrefSize(280, 30);
        name.getChildren().addAll(namelb, nametf);


        FileChooser fileChooser = new FileChooser();

        VBox serverjar = new VBox();
        Label serverjarlb = new Label("Server-jar");
        HBox serverjarnselect = new HBox(10);
        TextField serverjartf = new TextField();
        serverjartf.setEditable(false);
        serverjartf.setPromptText("Select jar");
        serverjartf.setPrefSize(220, 30);
        Button serverjarbtn = new Button("...");
        serverjarbtn.setOnAction(e -> {
            File jar = fileChooser.showOpenDialog(window);
            if (jar != null && jar.getName().contains(".jar")) {
                serverjartf.setText(jar.getName());
                si.setServerFile(jar.getName());
            }
        });
        serverjarbtn.setPrefSize(50, 30);
        serverjarnselect.getChildren().addAll(serverjartf, serverjarbtn);
        serverjar.getChildren().addAll(serverjarlb, serverjarnselect);

        VBox version = new VBox();
        Label versionlb = new Label("Version");
        TextField versiontf = new TextField();
        versiontf.setPrefSize(100, 30);
        version.getChildren().addAll(versionlb, versiontf);

        CheckBox eula = new CheckBox("Do you agree with the EULA");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button cancelbtn = new Button("Cancel");
        cancelbtn.setPrefSize(100, 30);
        cancelbtn.setOnAction(e -> window.close());
        Button createbtn = new Button("Create");
        createbtn.setOnAction(e -> {
            if (!nametf.getText().equals("") && !serverjartf.getText().equals("") && !versiontf.getText().equals("")) {
                si.setName(nametf.getText());
                si.setServerInstanceID(nametf.getText());
                si.setServerVersion(versiontf.getText());
                si.save();
                InstancePool.set(si.getServerInstanceID(), tmp);
                tmp.init();
                tmp.addServerInstanceToList();
                window.close();
            }
        });
        createbtn.setPrefSize(100, 30);
        buttons.getChildren().addAll(cancelbtn, createbtn);

        layout.getChildren().addAll(imgpane, name, serverjar, version, eula, buttons);

        window.show();
    }

    public static void close() {
        if(window == null)
            return;
        window.close();
    }

}


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.demus_intergalactical.serverman.Globals;
import net.demus_intergalactical.serverman.instance.ServerInstance;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class CreateInstanceWindow {

    static Stage window;
    static int serverIconSize = 64;
    static File serverJar;
    static Image serverIcon;
    static File serverIconFile;

    public static void display() {

        window = new Stage();
        window.setTitle("Create new instance");
        window.setResizable(false);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        //layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);

        InstanceContainer tmp = new InstanceContainer();
        ServerInstance si = tmp.getInstance();

        try {
            serverIcon = new Image(new FileInputStream(new File("./assets/unknown_server.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView serverimg = new ImageView(serverIcon);
        serverimg.setFitHeight(64);
        serverimg.setFitWidth(64);
        FileChooser iconChooser = new FileChooser();
        serverimg.setOnMouseClicked(e -> {
            File icon = iconChooser.showOpenDialog(window);
            if (icon != null) {
                if(icon.getName().endsWith(".png")) {
                    try {
                        serverIcon = new Image(new FileInputStream(icon.getPath()));
                        serverIconFile = new File(icon.getPath());
                        if (serverIcon.getHeight() == serverIconSize && serverIcon.getWidth() == serverIconSize) {
                            serverimg.setImage(serverIcon);
                        } else {
                            AlertWindow.display("Icon selection", "The server icon has to be 64x64", Alert.AlertType.ERROR);
                        }
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    AlertWindow.display("Icon selection", "The server icon has to be a PNG", Alert.AlertType.ERROR);
                }
            }
        });
        StackPane imgpane = new StackPane(serverimg);
        imgpane.setAlignment(Pos.CENTER);

        VBox name = new VBox();
        Label namelb = new Label("Server name");
        TextField nametf = new TextField();
        nametf.setOnKeyTyped(e -> {
            if (!e.getCharacter().matches("[\\w\\.\\!\\?\\-\\+\\,\\&\\'\\#\\(\\)\\[\\]\\s]")) {
                e.consume();
            }
        });
        nametf.setPrefSize(250, 30);
        name.getChildren().addAll(namelb, nametf);


        FileChooser fileChooser = new FileChooser();

        VBox serverjar = new VBox();
        Label serverjarlb = new Label("Server-jar");
        HBox serverjarnselect = new HBox(10);
        TextField serverjartf = new TextField();
        serverjartf.setEditable(false);
        serverjartf.setPromptText("Select jar");
        serverjartf.setPrefSize(170, 30);
        Button serverjarbtn = new Button("...");
        serverjarbtn.setOnAction(e -> {
            serverJar = ListServerJarsWindow.display();
            try {
                serverjartf.setText(serverJar.getName());
            } catch (NullPointerException e2) {} // i dont care about you muhahaha

        });
        serverjarbtn.setPrefSize(50, 30);
        serverjarnselect.getChildren().addAll(serverjartf, serverjarbtn);
        serverjar.getChildren().addAll(serverjarlb, serverjarnselect);

        VBox version = new VBox();
        Label versionlb = new Label("Version");
        TextField versiontf = new TextField();
        versiontf.setPrefSize(100, 30);
        version.getChildren().addAll(versionlb, versiontf);

        Hyperlink eulalink = new Hyperlink("https://account.mojang.com/documents/minecraft_eula");
        eulalink.setText("EULA");
        CheckBox eula = new CheckBox("Do you agree with the " + eulalink.getText());
        //(https://account.mojang.com/documents/minecraft_eula)

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button cancelbtn = new Button("Cancel");
        cancelbtn.setPrefSize(100, 30);
        cancelbtn.setOnAction(e -> window.close());
        Button createbtn = new Button("Create");
        createbtn.setOnAction(e -> {
            if (!nametf.getText().equals("") && !serverjartf.getText().equals("") && !versiontf.getText().equals("")) {
                if (eula.isSelected()) {
                    try {
                        FileUtils.writeStringToFile(new File(Globals.getServerManConfig().get("instances_home") + File.separator + nametf.getText() + File.separator + "eula.txt"), "eula=true");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    AlertWindow.display("EULA", "You can't run a server, if you don't agree with the EULA", Alert.AlertType.INFORMATION);
                    return;
                }
                si.setName(nametf.getText());
                si.setServerInstanceID(nametf.getText());
                si.setServerFile(serverJar.getName());
                si.setServerVersion(versiontf.getText());
                try {
                    si.setIcon(serverIconFile);
                } catch (Exception e1) {
                }

                try {
                    FileUtils.copyFileToDirectory(serverJar, new File(Globals.getServerManConfig().get("instances_home") + File.separator + si.getServerInstanceID()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                si.save();
                InstancePool.set(si.getServerInstanceID(), tmp);
                tmp.init();
                tmp.addServerInstanceToList();
                tmp.setActive(false);
                window.close();
            } else {
                AlertWindow.display("Create new Instance", "Please fill all textfields with information", Alert.AlertType.ERROR);
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


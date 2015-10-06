import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ListWindow extends Dialog {

	static Stage window;

	public static void display(String msg, Window parentWindow) {
		window = new Stage();
		window.setTitle("Error");
		window.initModality(Modality.APPLICATION_MODAL);
		window.setResizable(false);
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		window.setScene(scene);

		Label error = new Label(msg);
		Button ok = new Button("OK");
		ok.setPrefSize(50, 40);
		ok.setOnAction(e -> window.close());
		layout.getChildren().addAll(error, ok);

		window.showAndWait();
	}
}

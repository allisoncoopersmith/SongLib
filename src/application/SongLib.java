/*
 * @author Allison Coopersmith
* @author Kaushal Parikh
*/

package application;

import java.io.IOException;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SongLib extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mainApp.fxml"));
		loader.setController(new Controller());
		Parent root = loader.load();
		Scene scene = new Scene(root);
		Controller controller = loader.getController();
		controller.setMainStage(primaryStage);
		primaryStage.setTitle("Song Library");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
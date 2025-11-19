package vista;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Loginusuarioregistrado.fxml"));

        Scene scene;
        
		try {
			scene = new Scene(loader.load());
		       stage.setTitle("SLR Renting");
		        stage.setScene(scene);
		        stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
    }
}

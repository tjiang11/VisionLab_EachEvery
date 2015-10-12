package main;

import view.GameGUI;
import javafx.application.Application;
import javafx.stage.Stage;
/**
 * @author Tony Jiang
 * 10-12-2015
 *
 */

public class EachEveryMain extends Application {
    
    /**
    * Main class.
    * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new GameGUI(primaryStage);
    }
}

package leveleditor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    
    public static LevelEditorView editor = new LevelEditorView();
    
    @Override
    public void start(Stage primaryStage) {
        
        //StackPane root = new StackPane();
        
        Scene scene = new Scene(editor.initScene());
        
        primaryStage.setTitle("LevelEdit");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            System.out.println(java.lang.Thread.activeCount());
            Platform.exit();
            System.exit(0);});


        primaryStage.setScene(scene);
        
        primaryStage.show();        
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}

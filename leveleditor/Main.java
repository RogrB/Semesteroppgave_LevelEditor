package leveleditor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <h1>The Main class</h1>
 * Starting point for the JavaFX application
 */
public class Main extends Application {
    
    /**
    * Creates an instance of the {@code LevelEditorView} class
    * to grab the root from its {@code initScene} method
    */
    public static LevelEditorView editor = new LevelEditorView();
    
    /**
    * Starting method for the JavaFX application
    */    
    @Override
    public void start(Stage primaryStage) {
        
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

    /**
    * Entrypoint for the application
    * @param args args
    */    
    public static void main(String[] args) {
        launch(args);
    }
    
}

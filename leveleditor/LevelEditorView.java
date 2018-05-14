package leveleditor;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * <h1>LevelEditorView class</h1>
 * This class handles FrontEnd functionality of the LevelEditor
 * 
 * @author Roger Birkenes Solli
 */
public class LevelEditorView {
    
    /**
     * Singleton object
     */      
    private static LevelEditorView inst = new LevelEditorView();
    
    /**
     * Method to access singleton class.
     * @return Returns a reference to the singleton object.
     */              
    public static LevelEditorView getInstance(){return inst; }      
    
    /**
     * Root pane
     */      
    public Pane root = new Pane();
    
    /**
     * BackGround image source path
     */      
    private static final String BG_IMG = "assets/image/background.jpg";
    
    /**
     * {@code Sprite} object
     */      
    Sprite sprite;
    
    /**
     * Number of columns - Amount of enemy waves to be generated
     */      
    private int columnCounter = 10;
    
    /**
     * Canvas - Renders the grid of {@code EnemyItem} objects
     */      
    final Canvas canvas = new Canvas(1190, 425);
    
    /**
     * GraphicsContext for the Canvas
     */      
    final GraphicsContext gc = canvas.getGraphicsContext2D();
    
    /**
     * {@code LevelEditorLogic} object
     * @see LevelEditorLogic
     */      
    LevelEditorLogic logic = new LevelEditorLogic(columnCounter);  
    
    /**
     * FlowPane for draggable EnmyItem ImgViews
     */      
    private FlowPane enemyPane;    
    
    /**
     * ObservableList containing the various Movement Patterns for enemies in the game
     */      
    ObservableList<String> movements = FXCollections.observableArrayList("",
            "LEFT", "LEFT_PULSATING", "SIN", "SIN_REVERSED",
            "COS", "COS_REVERSED", "TRI", "TRI_REVERSED",
            "MADNESS_01", "MADNESS_02", "MADNESS_03",
            "BOSS_LINE", "BOSS_EIGHT", "BOSS_OVAL");
    
    /**
     * ChoiceBox to select the Enemy Movement Patterns
     */      
    private ChoiceBox<String> enemyMovement = new ChoiceBox<>(movements);
    
    /**
     * Method to initiate the Scene
     * @return gets the Root pane to pass to {@code Main}
     */      
    public Parent initScene(){
        root.setPrefSize(1200, 800);
        root.setBackground(getBackGroundImage(BG_IMG));        
        
        enemyPane = getEnemiesPane();
        populateEnemies();
        
        final Pane canvasPane = new Pane();
        canvasPane.setTranslateX(10);
        canvasPane.setTranslateY(290);
        canvasPane.getChildren().add(canvas);
        
        gc.setFill(Color.WHITE);
        gc.fillRect(1, 1, 1190, 425);
        
        initCanvas();        
        Slider enemyWavesSlider = getEnemyWavesSlider();
        setFooter();
        setText();

        root.getChildren().addAll(enemyPane, enemyWavesSlider, canvasPane);
        logic.resetArray(gc);
        
        return root;
    }
    
    /**
     * Sets descriptive text in the scene
     */      
    private void setText() {
        Text selectEnemiesText = new Text("Drag and Drop enemies to grid");
        selectEnemiesText.setX(450);
        selectEnemiesText.setY(230);
        selectEnemiesText.setFill(Color.WHITE);
        selectEnemiesText.setFont(selectEnemiesText.getFont().font(20));        
        
        Text selectWavesText = new Text("Set number of columns: (WARNING: Clears grid)");
        selectWavesText.setX(20);
        selectWavesText.setY(240);
        selectWavesText.setFill(Color.WHITE);
        selectWavesText.setFont(selectEnemiesText.getFont().font(15));  

        root.getChildren().addAll(selectEnemiesText, selectWavesText);
    }
    
    /**
     * Sets a footer and attaches buttons and the ChoiceBox with EnemyMovement patterns
     */      
    private void setFooter() {
        HBox footer = new HBox();
        footer.setTranslateX(10);
        footer.setTranslateY(740);
        
        MenuButton saveButton = new MenuButton("Save Level");
        MenuButton resetButton = new MenuButton("Reset");
        MenuButton clearButton = new MenuButton("Clear Cell");
        Button helpButton = new Button("Help");
        Text enemyMovementText = new Text("MovementType:");
        enemyMovementText.setFill(Color.WHITE);
        
        helpButton.setTranslateX(750);
        helpButton.setTranslateY(210);
        helpButton.setOnMouseClicked(event -> getHelp());
        footer.getChildren().addAll(saveButton, resetButton, clearButton, enemyMovementText, enemyMovement);
        footer.setSpacing(10);
        saveButton.setOnMouseClicked(event -> printData());
        clearButton.setOnMouseClicked(event -> logic.clearCell(gc));
        resetButton.setOnMouseClicked(event -> logic.resetArray(gc));  
        enemyMovement.setOnAction(event -> {
            setMovementPattern();
                });       
                
        root.getChildren().addAll(helpButton, footer);
    }
    
    /**
     * Creates a slider to adjust number of columns (enemy waves)
     */      
    private Slider getEnemyWavesSlider() {
    Slider enemyWavesSlider = new Slider(10, 30, columnCounter);
        enemyWavesSlider.setShowTickMarks(true);
        enemyWavesSlider.setBlockIncrement(1);
        enemyWavesSlider.setTranslateX(20);
        enemyWavesSlider.setTranslateY(250);
        enemyWavesSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            enemyWavesSlider.setValue(newValue.intValue());
            columnCounter = newValue.intValue();
            logic.setEnemyColumns(gc, columnCounter);
        });  
        return enemyWavesSlider;
    }
    
    /**
     * Initiates the Canvas where the grid is drawn
     * and sets up the events for clicking and drag-and-drop functionality
     */      
    private void initCanvas() {
        canvas.setOnMouseClicked(event -> {
            logic.clicked(event, gc);
            if(logic.getEnemies()[logic.getSelectedX()][logic.getSelectedY()].getActive()) {
                enemyMovement.setValue(logic.getEnemies()[logic.getSelectedX()][logic.getSelectedY()].getMovementPattern());
            }
            else {
                enemyMovement.setValue("");
            }
                });
        
        canvas.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean complete = false;
            if (db.hasString()) {
                String nodeId = db.getString();
                logic.setEnemy(logic.getCellX(event.getX()), logic.getCellY(event.getY()), logic.getSprite(nodeId));
                complete = true;
                logic.drawGrid(gc);

            }
            event.setDropCompleted(complete);
            event.consume();
        });   
        
        canvas.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != canvas &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });      
    }
    
    /**
     * @return gets the FlowPane that contains the draggable ImgViews
     */      
    public FlowPane getEnemiesPane() {
        enemyPane = new FlowPane(10, 10);
        enemyPane.setTranslateX(10);
        enemyPane.setTranslateY(10);
        enemyPane.setPrefWrapLength(1190);
        enemyPane.setStyle("-fx-border-color: white");        
        
        enemyPane.setPadding(new Insets(10.0));
        
        return enemyPane;
    }
        
    /**
     * @return gets the BackgroundImage used in the scene
     * @param BG_IMG inputs the source path
     */      
    public Background getBackGroundImage(String BG_IMG){
        BackgroundImage bg = new BackgroundImage(
                new Image(BG_IMG),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        false,
                        false,
                        true,
                        false
                )
        );
        return new Background(bg);
    }    
    
    /**
     * @param columns sets the number of columns - derived from the slider
     */      
    public void setColumns(int columns) {
        this.columnCounter = columns;
        logic.setEnemyColumns(gc, columns);
    }
    
    /**
     * @return gets the current number of columns
     */      
    public int getColumns() {
        return this.columnCounter;
    }
    
    /**
     * @return gets the GraphicsContext to draw on
     */      
    public GraphicsContext getGC() {
        return this.gc;
    }
    
    /**
     * Method to set the selected MovementPattern from the ChoiceBox
     * to the currently selected {@code EnemyItem} on the grid
     */      
    public void setMovementPattern() {
        String pattern = enemyMovement.getValue();
        if (logic.getSelectedX() != -1 && logic.getSelectedY() != -1) {
            logic.setMovementPattern(logic.getSelectedX(), logic.getSelectedY(), pattern);
        }
    }
    
    /**
     * Adds all the available enemies in the game to a List
     */      
    private List<EnemyItem> getEnemyList() {
        ArrayList<EnemyItem> enemyList = new ArrayList<>();
        enemyList.add(new EnemyItem(false, Sprite.BLUE1));
        enemyList.add(new EnemyItem(false, Sprite.BLUE2));
        enemyList.add(new EnemyItem(false, Sprite.GREEN1));
        enemyList.add(new EnemyItem(false, Sprite.ORANGE1));
        enemyList.add(new EnemyItem(false, Sprite.RED1));
        enemyList.add(new EnemyItem(false, Sprite.RED2));
        enemyList.add(new EnemyItem(false, Sprite.RED3));
        enemyList.add(new EnemyItem(false, Sprite.REDBIG));
        enemyList.add(new EnemyItem(false, Sprite.UFOBLUE));
        enemyList.add(new EnemyItem(false, Sprite.UFOGREEN));
        enemyList.add(new EnemyItem(false, Sprite.UFORED));
        enemyList.add(new EnemyItem(false, Sprite.UFOYELLOW));
        enemyList.add(new EnemyItem(false, Sprite.BOSS01));
        enemyList.add(new EnemyItem(false, Sprite.BOSS02));
        enemyList.add(new EnemyItem(false, Sprite.METEOR));
        return enemyList;    
    }    
    
    /**
     * Adds the draggable EnemyItems to the FlowPane
     */      
    private void populateEnemies() {
        List<EnemyItem> enems = getEnemyList();
        enems.stream().map((e) -> {
            enemyPane.getChildren().add(e.getImgView());
            return e;
        }).forEach((e) -> { });
    }  
    
    /**
     * @return gets the ChoiceBox for EnemyMovements
     */     
    public ChoiceBox<String> getChoiceBox() {
        return enemyMovement;
    }
    
    /**
     * Creates an Alert PopUp to notify the user
     * that the LevelData has been copied to the clipboard
     */     
    public void printData() {
        logic.setDataToClipBoard();
        Alert output = new Alert(Alert.AlertType.INFORMATION);
        output.setTitle("LevelData Output");
        output.setHeaderText(null);
        output.setContentText("LevelData has been copied to your Clipboard");

        output.showAndWait();        
    }
    
    /**
     * Creates a Help PopUp
     */     
    public void getHelp() {
        Alert help = new Alert(Alert.AlertType.INFORMATION);
        help.setTitle("LevelEditor Help");
        help.setHeaderText(null);
        help.setContentText("1. Select gridsize from slider\n"
                + "2. Drag and drop enemies from menu to grid\n"
                + "3. Select MovementPattern for individual enemies on grid\n"
                + "4. Click \"Save\" to copy LevelData to clipboard\n\n"
                + "Clear Cell clears enemy from the selected cell\n\n"
                + "(Note: You can only have one bosstype for each level)");

        help.showAndWait();           
    }
 
}

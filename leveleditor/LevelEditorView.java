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


public class LevelEditorView {
    
    private static LevelEditorView inst = new LevelEditorView();
    public static LevelEditorView getInstance(){return inst; }      
    
    public Pane root;
    private static final String BG_IMG = "assets/image/background.jpg";
    Sprite sprite;
    private int columnCounter = 10;
    final Canvas canvas = new Canvas(1190, 425);
    final GraphicsContext gc = canvas.getGraphicsContext2D();
    final Pane canvasPane = new Pane();
    LevelEditorLogic logic = new LevelEditorLogic(columnCounter);  
    private Slider enemyWavesSlider;
    private FlowPane enemyPane;    
    
    ObservableList<String> movements = FXCollections.observableArrayList("",
            "LEFT", "LEFT_PULSATING", "SIN", "SIN_REVERSED",
            "COS", "COS_REVERSED", "TRI", "TRI_REVERSED",
            "MADNESS_01", "MADNESS_02", "MADNESS_03",
            "BOSS_LINE", "BOSS_EIGHT", "BOSS_OVAL");
    
    private ChoiceBox<String> enemyMovement = new ChoiceBox<>(movements);
    
    public Parent initScene(){
        root = new Pane();
        Text selectEnemiesText = new Text("Drag and Drop enemies to grid");
        selectEnemiesText.setX(450);
        selectEnemiesText.setY(230);
        selectEnemiesText.setFill(Color.WHITE);
        selectEnemiesText.setFont(selectEnemiesText.getFont().font(20));
        root.setPrefSize(1200, 800);
        root.setBackground(getBackGroundImage(BG_IMG));
        
        enemyPane = getEnemiesPane();
        populateEnemies();
        
        canvasPane.setTranslateX(10);
        canvasPane.setTranslateY(290);
        canvasPane.getChildren().add(canvas);
        gc.setFill(Color.WHITE);
        gc.fillRect(1, 1, 1190, 425);
        canvas.setOnMouseClicked(event -> {
            logic.clicked(event, gc);
            if(logic.getEnemies()[logic.getSelectedX()][logic.getSelectedY()].getActive()) {
                enemyMovement.setValue(logic.getEnemies()[logic.getSelectedX()][logic.getSelectedY()].getMovementPattern());
                //System.out.println(enemies[selectedX][selectedY].getMovementPattern());
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
                // System.out.println("dropped " + nodeId + " at " + logic.getCellX(event.getX()) + " , " + logic.getCellY(event.getY()));
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
        
        Text selectWavesText = new Text("Set number of columns: (WARNING: Clears grid)");
        selectWavesText.setX(20);
        selectWavesText.setY(240);
        selectWavesText.setFill(Color.WHITE);
        selectWavesText.setFont(selectEnemiesText.getFont().font(15));  
        
        enemyWavesSlider = new Slider(10, 30, columnCounter);
        enemyWavesSlider.setShowTickMarks(true);
        enemyWavesSlider.setBlockIncrement(1);
        enemyWavesSlider.setTranslateX(20);
        enemyWavesSlider.setTranslateY(250);
        enemyWavesSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            enemyWavesSlider.setValue(newValue.intValue());
            columnCounter = newValue.intValue();
            logic.setEnemyColumns(gc, columnCounter);
        });        
        
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
        //enemyMovementText.setTranslateX(650);
        //enemyMovement.setTranslateX(700);
        footer.getChildren().addAll(saveButton, resetButton, clearButton, enemyMovementText, enemyMovement);
        footer.setSpacing(10);
        saveButton.setOnMouseClicked(event -> printData());
        clearButton.setOnMouseClicked(event -> logic.clearCell(gc));
        resetButton.setOnMouseClicked(event -> logic.resetArray(gc));  
        enemyMovement.setOnAction(event -> {
            setMovementPattern();
                });       
        
        root.getChildren().addAll(selectEnemiesText, enemyPane, enemyWavesSlider, selectWavesText, canvasPane, helpButton, footer);
        System.out.println(gc);
        
        logic.resetArray(gc);
        
        return root;

    }    
    
    public FlowPane getEnemiesPane() {
        enemyPane = new FlowPane(10, 10);
        enemyPane.setTranslateX(10);
        enemyPane.setTranslateY(10);
        enemyPane.setPrefWrapLength(1190);
        enemyPane.setStyle("-fx-border-color: white");        
        
        enemyPane.setPadding(new Insets(10.0));
        
        return enemyPane;
    }
        
    
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
    
    public void setColumns(int columns) {
        this.columnCounter = columns;
        logic.setEnemyColumns(gc, columns);
    }
    
    public int getColumns() {
        return this.columnCounter;
    }
    
    public GraphicsContext getGC() {
        return this.gc;
    }
    
    public void setMovementPattern() {
        String pattern = enemyMovement.getValue();
        if (logic.getSelectedX() != -1 && logic.getSelectedY() != -1) {
            //System.out.println("Selected movementpattern " + pattern  + " for enemy " + logic.getSelectedX() + " , " + logic.getSelectedY());
            logic.setMovementPattern(logic.getSelectedX(), logic.getSelectedY(), pattern);
        }
        else {
            //System.out.println("Selected movementpattern " + pattern);
        }
    }
    
    private void populateEnemies() {
        List<EnemyItem> enems = getEnemyList();
        enems.stream().map((e) -> {
            enemyPane.getChildren().add(e.getImgView());
            return e;
        }).forEach((e) -> { });
    }  
    
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
    
    public ChoiceBox<String> getChoiceBox() {
        return enemyMovement;
    }
    
    public void printData() {
        logic.setDataToClipBoard();
        Alert output = new Alert(Alert.AlertType.INFORMATION);
        output.setTitle("LevelData Output");
        output.setHeaderText(null);
        output.setContentText("LevelData has been copied to your Clipboard");

        output.showAndWait();        
    }
    
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

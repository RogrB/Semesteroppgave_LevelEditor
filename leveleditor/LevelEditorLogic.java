package leveleditor;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

/**
 * <h1>Handles the BackEnd functionality of the LevelEditor</h1>
 * <i>Note:</i> The passing of the GraphicsContext could have been handled more elegantly,
 * but I had an issue with accessing and was unable to sort it out due to time constraints.
 * 
 * @author Roger Birkenes Solli
 */
public class LevelEditorLogic {
    
    /**
     * Array of {@code EnemyItem} objects to populate the grid
     * @see EnemyItem
     */          
    private EnemyItem enemies[][];
    
    /**
     * Scalable size of the individual cells on the grid
     */          
    private int cellSize = 60;
    
    /**
     * Number of columns - Represents the amount of enemy waves in the level
     */          
    private int columns = 10;
    
    /**
     * Old X value for grid rectangle - to know where to clear when re-drawing the grid
     */          
    private double oldRectX = -1;
    
    /**
     * Old Y value for grid rectangle - to know where to clear when re-drawing the grid
     */       
    private double oldRectY = -1;
    
    /**
     * Currently selected grid cell X value
     */       
    private int selectedX = -1; 
    
    /**
     * Currently selected grid cell Y value
     */      
    private int selectedY = -1;
    
    /**
     * Previous selected grid cell X value
     * Needed to re-draw grid cell when selecting a new one
     */      
    private int oldSelectedX = -1;
    
    /**
     * Previous selected grid cell Y value
     * Needed to re-draw grid cell when selecting a new one
     */      
    private int oldSelectedY = -1;
    
    
    /**
     * <b>Constructor</b>
     * @param columns sets the initial column count
     * and creates a new array of {@code EnemyItem} objects
     * based on the column count
     */          
    public LevelEditorLogic(int columns) {
        enemies = new EnemyItem[columns][7];
        this.columns = columns;
    }
    
    /**
     * @return gets the current state of the {@code EnemyItem} -
     * to decide if the Item is to be rendered.
     * @param x X value of the {@code EnemyItem} array
     * @param y Y value of the {@code EnemyItem} array
     */       
    public boolean checkEnemyState(int x, int y) {
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == x && y == j) {
                    return enemies[i][j].getActive();   
                }
            }
        }
        return false;
    }
    
    /**
     * Method that renders the grid and its items to the canvas.
     * Draws the {@code EnemyItem} sprite to the cell if there is any active Items.
     * Draws blank cells where there are no active {@code EnemyItem}
     * @param gc GraphicsContext to draw on.
     */       
    public void drawGrid(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.clearRect(0, 0, 1190, 425);
        adjustCellSize();
        gc.fillRect(0, 0, cellSize*columns+(columns), cellSize*7+(columns));
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < 7; j++) {
                if (checkEnemyState(i, j)) {
                    // System.out.println("Drawing " + enemies[i][j].getSprite().getSrc() + " for enemy " + i + " , " + j);
                    gc.drawImage(new Image(enemies[i][j].getSprite().getSrc()), i*cellSize+(i), j*cellSize+(j), cellSize, cellSize);
                }
                else {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(i*cellSize+(i), j*cellSize+(j), cellSize, cellSize);
                }
            }
        }
    }     
    
    /**
     * Method to set the current number of grid columns
     * @param gc Passes the GraphicsContext
     * @param columns sets the current number of grid columns
     */       
    public void setEnemyColumns(GraphicsContext gc, int columns) {
        this.columns = columns;
        enemies = new EnemyItem[columns][7];
        resetArray(gc);
    }
    
    /**
     * Clears the {@code EnemyItem} array
     * and re-draws the grid
     * @param gc Passes the GraphicsContext to draw
     */       
    public void resetArray(GraphicsContext gc) {
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < 7; j++) {
                enemies[i][j] = new EnemyItem(false);
            }
        }
        drawGrid(gc);
    }
    
    /**
     * @return compares an input with two numbers to see if the value is in between
     * @param input the input value to be compared - Used to adjust the CellSize of the Grid
     * @param low the lower number
     * @param high the higher number
     */       
    public boolean isBetween(int input, int low, int high) {
        return low <= input && input <= high;
    }
    
    /**
     * Adjust the CellSize of the grid to be drawn on the canvas.
     * Higher number of columns makes the CellSize smaller so the grid
     * can still fit on the screen.
     */       
    public void adjustCellSize() {
        if (isBetween(columns, 19,21)) {
            cellSize = 54;
        }
        else if (isBetween(columns, 21, 23)) {
            cellSize = 50;
        }
        else if (isBetween(columns, 23, 25)) {
            cellSize = 46;
        }
        else if (isBetween(columns, 25, 27)) {
            cellSize = 42;
        }
        else if (isBetween(columns, 27, 29)) {
            cellSize = 40;
        }
        else if (isBetween(columns, 29, 31)) {
            cellSize = 38;
        }
        else {
            cellSize = 60;
        }   
    }
    
    /**
     * Method that handles click event when the user clicks on the grid and
     * selects the cell that was clicked
     * @param e the Mouse Event
     * @param gc Passes the GraphicsContext
     */       
    public void clicked(MouseEvent e, GraphicsContext gc) {  
        double x = e.getX();
        double y = e.getY();
        double rectX;
        double rectY;

        for (int i = 0; i < 1190; i++) {
            for (int j = 0; j < 425; j++) {
                rectX = i*cellSize+(i);
                rectY = j*cellSize+(j);
                
                if (x > rectX && x < rectX+cellSize) {
                    if (y > rectY && y < rectY+cellSize) {
                        if(i < columns && j < 7) {
                            // System.out.println("clicked cell " + i + " , " + j);
                            selectedX = i;
                            selectedY = j;
                            reDrawCell(gc);
                            selectCell(gc, rectX, rectY);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * @return gets the X value of a Cell on the grid - used for dropping {@code EnemyItem} on the grid
     * @param mouseX passes the X value of the mouse when the method is called
     */       
    public int getCellX(double mouseX) {
        int x = 0;
        double rectX;
        for (int i = 0; i < 1190; i++) {
            rectX = i*cellSize+(i);
            if (mouseX > rectX && mouseX < rectX+cellSize) {
                if (i < columns) {
                    x = i;
                }
            }
        }
        return x;
    }
    
    /**
     * @return gets the Y value of a Cell on the grid - used for dropping {@code EnemyItem} on the grid
     * @param mouseY passes the Y value of the mouse when the method is called
     */       
    public int getCellY(double mouseY) {
        int y = 0;
        double rectY;
        for (int i = 0; i < 1190; i++) {
            rectY = i*cellSize+(i);
            if (mouseY > rectY && mouseY < rectY+cellSize) {
                if (i < columns) {
                    y = i;
                }
            }
        }
        return y;
    }    
    
    /**
     * Method that highlights a cell that has been clicked on the grid
     * @param gc Passes GraphicsContext to draw on.
     * @param x The cells X value
     * @param y The cells Y value
     */
    public void selectCell(GraphicsContext gc, double x, double y) {
        oldRectX = x;
        oldRectY = y;
        oldSelectedX = selectedX;
        oldSelectedY = selectedY;
        gc.clearRect(x+1, y+1, cellSize-2, cellSize-2);
        gc.setFill(Color.RED);
        gc.fillRect(x+1, y+1, cellSize-2, cellSize-2);
        if (enemies[selectedX][selectedY].getActive()) {
            gc.drawImage(new Image(enemies[selectedX][selectedY].getSprite().src),
                    selectedX*cellSize+(selectedX),selectedY*cellSize+(selectedY), cellSize, cellSize);
        }
    }
    
    /**
     * Re-Draws the previously selected cell
     * @param gc Passes GraphicsContext to be drawn on.
     */    
    public void reDrawCell(GraphicsContext gc) {
        if (oldRectX != -1 && oldRectY != -1) {
            gc.clearRect(oldRectX+1, oldRectY+1, cellSize-2, cellSize-2);
            gc.setFill(Color.BLACK);
            gc.fillRect(oldRectX+1, oldRectY+1, cellSize-2, cellSize-2);
        }
        if (oldSelectedX != -1 && oldSelectedY != -1) {
            if (enemies[oldSelectedX][oldSelectedY].getActive()) {
                gc.drawImage(new Image(enemies[oldSelectedX][oldSelectedY].getSprite().getSrc()),
                        oldSelectedX*cellSize+(oldSelectedX), oldSelectedY*cellSize+(oldSelectedY), cellSize, cellSize);
            }
        }        
    }
    
    /**
     * Method that sets an {@code EnemyItem} to the array
     * and applies a default movement pattern.
     * @param x Arrays X value
     * @param y Arrays Y value
     * @param sprite the Sprite for the {@code EnemyItem}
     */       
    public void setEnemy(int x, int y, Sprite sprite) {
        enemies[x][y] = new EnemyItem(true, sprite);
        enemies[x][y].setMovementPattern("LEFT");
    }
    
    /**
     * @return gets the Sprite from an {@code EnemyItem}
     * @param source inputs the Source string
     */       
    public Sprite getSprite(String source) {
        Sprite rSprite = Sprite.BLUE1;
        switch(source) {
            case "BLUE1":
                rSprite = Sprite.BLUE1;
                break;
            case "BLUE2":
                rSprite = Sprite.BLUE2;
                break;
            case "BOSS01":
                rSprite = Sprite.BOSS01;
                break;       
            case "BOSS02":
                rSprite = Sprite.BOSS02;
                break;   
            case "GREEN1":
                rSprite = Sprite.GREEN1;
                break;   
            case "ORANGE1":
                rSprite = Sprite.ORANGE1;
                break;   
            case "RED1":
                rSprite = Sprite.RED1;
                break;      
            case "RED2":
                rSprite = Sprite.RED2;
                break;  
            case "RED3":
                rSprite = Sprite.RED3;
                break;  
            case "METEOR":
                rSprite = Sprite.METEOR;
                break;  
            case "REDBIG":
                rSprite = Sprite.REDBIG;
                break;  
            case "UFOBLUE":
                rSprite = Sprite.UFOBLUE;
                break;  
            case "UFOGREEN":
                rSprite = Sprite.UFOGREEN;
                break;  
            case "UFORED":
                rSprite = Sprite.UFORED;
                break;  
            case "UFOYELLOW":
                rSprite = Sprite.UFOYELLOW;
                break;                  
        }    
        return rSprite;
    }
    
    /**
     * @return gets the X value of the currently selected grid cell
     */       
    public int getSelectedX() {
        return this.selectedX;
    }
    
    /**
     * @return gets the Y value of the currently selected grid cell
     */       
    public int getSelectedY() {
        return this.selectedY;
    }
    
    /**
     * @param movement sets the movement pattern for an {@code EnemyItem}
     * @param x Arrays X value
     * @param y Arrays Y value
     */       
    public void setMovementPattern(int x, int y, String movement) {
        enemies[x][y].setMovementPattern(movement);
    }   
    
    /**
     * @return gets the array of {@code EnemyItem}
     */       
    public EnemyItem[][] getEnemies() {
        return this.enemies;
    }
    
    /**
     * Method that formats the Array into valid {@code LevelData} used by the {@code LevelLoader} class in the main project
     * @return creates a string of code that can be input in the {@code LevelData} class to make a playable level.
     */       
    public String formatArray() {
        String levelData = "private static final String[][][] LEVELNAME = new String[][][] {\n";
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < columns; j++) {
                if(j == 0) {
                    levelData += "{";
                }
                if(enemies[j][i].getActive()) {
                    if (enemies[j][i].getType() == 2) {
                        levelData += "{\"" + enemies[j][i].getType() + "\", \"" + enemies[j][i].getMovementPattern() + "\"}";
                    }
                    else {
                        levelData += "{\"" + enemies[j][i].getType() + "\", \"" + enemies[j][i].getSprite().toString() +
                                "\", \"" + enemies[j][i].getMovementPattern() + "\"}";
                    }
                    if (j != columns-1) {
                        levelData += ", ";
                    }
                }
                else {
                    levelData += "{\"0\"}";
                    if (j != columns -1) {
                        levelData += ", ";
                    }
                }
                
                if(j == columns-1) {
                    if (i == 6) {
                        levelData += "}\n};";
                    }
                    else {
                        levelData += "},\n";
                    }
                }
            }
        }
        return levelData;
    }
    
    /**
     * Method that sets the LevelData to the ClipBoard
     */       
    public void setDataToClipBoard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(formatArray());
        clipboard.setContent(content);
    }
    
    /**
     * Clears the currently selected cell on the grid
     * @param gc Passes GraphicsContext to draw on.
     */       
    public void clearCell(GraphicsContext gc) {
        if (selectedX != -1 && selectedY != -1) {
            if (enemies[selectedX][selectedY].getActive()) {
                enemies[selectedX][selectedY] = new EnemyItem(false);
                drawGrid(gc);
            }
        }
        else {
            System.out.println("Select cell to clear");
        }
    }
    
}

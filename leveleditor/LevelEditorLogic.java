package leveleditor;

import javafx.scene.input.InputEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

public class LevelEditorLogic {
    
    private EnemyItem enemies[][];
    private int cellSize = 60;
    private int columns = 10;
    private double oldRectX = -1;
    private double oldRectY = -1;
    private int selectedX = -1; 
    private int selectedY = -1;
    private int oldSelectedX = -1;
    private int oldSelectedY = -1;
    
    public LevelEditorLogic(int columns) {
        enemies = new EnemyItem[columns][7];
        this.columns = columns;
    }
    
    public boolean checkEnemyState(int x, int y) {
        // Sjekker om enemy i angitt posisjon i array er aktiv og skal rendres
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == x && y == j) {
                    return enemies[i][j].getActive();   
                }
            }
        }
        return false;
    }
    
    public void drawGrid(GraphicsContext gc) {
        // Tegner grid til canvas
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
    
    public void setEnemyColumns(GraphicsContext gc, int columns) {
        this.columns = columns;
        enemies = new EnemyItem[columns][7];
        resetArray(gc);
    }
    
    public void resetArray(GraphicsContext gc) {
        // Clearer enemyarray
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < 7; j++) {
                enemies[i][j] = new EnemyItem(false);
            }
        }
        drawGrid(gc);
    }
    
    public boolean isBetween(int input, int low, int high) {
        return low <= input && input <= high;
    }
    
    public void adjustCellSize() {
        // Justerer cellsize ettersom kolonneantallet blir høyere
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
    
    public void clicked(MouseEvent e, GraphicsContext gc) {  
        // Onclick Event - for å kunne trykke på individuelle celler
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
                            selectCell(gc, rectX, rectY);
                        }
                    }
                }
            }
        }
    }
    
    public int getCellX(double mouseX) {
        // Finner X celle fra drop
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
    
    public int getCellY(double mouseY) {
        // Finner Y celle fra drop
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
    
    public void selectCell(GraphicsContext gc, double x, double y) {
        // "markerer" en celle
        if (oldRectX != -1 && oldRectY != -1) {
            gc.clearRect(oldRectX+1, oldRectY+1, cellSize-2, cellSize-2);
            gc.setFill(Color.BLACK);
            gc.fillRect(oldRectX+1, oldRectY+1, cellSize-2, cellSize-2);
        }
        if (oldSelectedX != -1 && oldSelectedY != -1) {
            if (enemies[oldSelectedX][oldSelectedY].getActive()) {
                gc.drawImage(new Image(enemies[oldSelectedX][oldSelectedY].getSprite().getSrc()), oldSelectedX*cellSize+(oldSelectedX), oldSelectedY*cellSize+(oldSelectedY), cellSize, cellSize);
            }
        }
        oldRectX = x;
        oldRectY = y;
        oldSelectedX = selectedX;
        oldSelectedY = selectedY;
        gc.clearRect(x+1, y+1, cellSize-2, cellSize-2);
        gc.setFill(Color.RED);
        gc.fillRect(x+1, y+1, cellSize-2, cellSize-2);
        if (enemies[selectedX][selectedY].getActive()) {
            gc.drawImage(new Image(enemies[selectedX][selectedY].getSprite().src), selectedX*cellSize+(selectedX), selectedY*cellSize+(selectedY), cellSize, cellSize);
        }
    }
    
    /*
    public void adjustMovementLabel() {
        if(enemies[selectedX][selectedY].getActive()) {
            LevelEditorView.getInstance().getChoiceBox().setValue(enemies[selectedX][selectedY].getMovementPattern());
            System.out.println(enemies[selectedX][selectedY].getMovementPattern());
        }
        else {
            LevelEditorView.getInstance().getChoiceBox().setValue("");
        }
    }*/
    
    public void setEnemy(int x, int y, Sprite sprite) {
        enemies[x][y] = new EnemyItem(true, sprite);
        enemies[x][y].setMovementPattern("LEFT");
    }
    
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
    
    public int getSelectedX() {
        return this.selectedX;
    }
    
    public int getSelectedY() {
        return this.selectedY;
    }
    
    public void setMovementPattern(int x, int y, String movement) {
        enemies[x][y].setMovementPattern(movement);
    }
    
    public void testSetEnemies(GraphicsContext gc) {
        // Clearer enemyarray og legger til 2 testenemies
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < 7; j++) {
                enemies[i][j] = new EnemyItem(false);
            }
        }
        enemies[1][1] = new EnemyItem(true, Sprite.BLUE1);
        enemies[6][3] = new EnemyItem(true, Sprite.RED1);
        enemies[7][6] = new EnemyItem(true, Sprite.UFORED);
        drawGrid(gc);
    }    
    
    public EnemyItem[][] getEnemies() {
        return this.enemies;
    }
    
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
                        levelData += "{\"" + enemies[j][i].getType() + "\", \"" + enemies[j][i].getSprite().toString() + "\", \"" + enemies[j][i].getMovementPattern() + "\"}";
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
    
    
    
    private static final String[][][] LEVEL2 = new String[][][]{
            {{"0"}, {"0"}, {"0"}},
            {{"2", "MADNESS_01"}, {"0"}},
            {{"0"}, {"0"}, {"0"}},
            {{"2", "MADNESS_01"}, {"0"}, {"0"}},
            {{"0"}, {"0"}, {"0"}},
            {{"0"}, {"3", "BOSS01", "BOSS_EIGHT"}, {"0"}},
            {{"0"}, {"0"}, {"0"}}
    };     
    
    public void setDataToClipBoard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(formatArray());
        clipboard.setContent(content);
    }
    
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

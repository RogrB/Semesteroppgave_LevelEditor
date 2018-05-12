package leveleditor;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class EnemyItem {
    
    private int x;
    private int y;
    private boolean active;
    private Sprite sprite;
    private Image image;
    private ImageView enemyImage;
    private String movementPattern;
    
    public EnemyItem(boolean active, Sprite sprite) {
        this.active = active;
        this.sprite = sprite;
        
        image = new Image(sprite.getSrc());
        enemyImage = new ImageView();
        enemyImage.setImage(image);
        enemyImage.setId(this.getClass().getSimpleName() + System.currentTimeMillis());

        enemyImage.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = enemyImage.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(sprite.toString());
            db.setContent(content);
            event.consume();
        });        
    }
    
    public EnemyItem(boolean active) {
        this.active = active;
    }
    
    public boolean getActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /*
    public int getX() {
        return this.x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(int y) {
        this.y = y;
    }*/
    
    public Sprite getSprite() {
        return this.sprite;
    }
    
    public ImageView getImgView() {
        return this.enemyImage;
    }
    
    public String getMovementPattern() {
        return this.movementPattern;
    }
    
    public void setMovementPattern(String pattern) {
        this.movementPattern = pattern;
    }
    
    public int getType() {
        int type = 0;
        if (sprite == sprite.METEOR) {
            type = 2;
        }
        else if (sprite == sprite.BOSS01 || sprite == sprite.BOSS02) {
            type = 3;
        }
        else {
            type = 1;
        }
        return type;
    }
    
}

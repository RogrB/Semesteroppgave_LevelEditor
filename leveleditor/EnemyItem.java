package leveleditor;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * <h1>EnemyItem class</h1>
 * This class handles the individual enemy items
 * that populates the grid.
 * 
 * @author Roger Birkenes Solli
 */
public class EnemyItem {
    
    /**
     * Active state - if the EnemyItem is "alive" or "dead"
     */        
    private boolean active;
    
    /**
     * Sprite
     */        
    private Sprite sprite;
    
    /**
     * Image
     */        
    private Image image;
    
    /**
     * ImageView - needed to make the EnemyItem draggable
     */        
    private ImageView enemyImage;
    
    /**
     * MovementPattern 
     */        
    private String movementPattern;
    
    /**
     * <b>Constructor</b> Sets the sprite and whether or not
     * the EnemyItem is "alive".
     * Then creates an ImageView and sets Mouse Drag event for the object
     * to make it draggable.
     * @param active sets the EnemyItem active or dead
     * @param sprite sets the Sprite of the EnemyItem
     */       
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
    
    /**
     * <b>Constructor</b> That takes in an active input.
     * Primarily used to set EnemyItems inactive
     * @param active sets active or inactive.
     */       
    public EnemyItem(boolean active) {
        this.active = active;
    }
    
    /**
     * @return gets current active state
     */       
    public boolean getActive() {
        return this.active;
    }
    
    /**
     * @param active sets active state
     */        
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * @return gets the Sprite for {@code this}
     */        
    public Sprite getSprite() {
        return this.sprite;
    }
    
    /**
     * @return gets the ImageView for {@code this}
     */        
    public ImageView getImgView() {
        return this.enemyImage;
    }
    
    /**
     * @return gets the MovementPattern of {@code this}
     */        
    public String getMovementPattern() {
        return this.movementPattern;
    }
    
    /**
     * @param pattern sets MovementPattern
     */        
    public void setMovementPattern(String pattern) {
        this.movementPattern = pattern;
    }
    
    /**
     * @return gets {@code this} type of enemy
     */        
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

package leveleditor;

import javafx.geometry.Pos;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * <h1>MenuButton class</h1>
 * Copied from our main project.
 * Refer to the MenuButton class in the main project
 * for proper documentation
 */
public class MenuButton extends StackPane{
    private Text text;
    private Rectangle bg;

    public MenuButton(String name){
        text = new Text(name);
        text.setFont(text.getFont().font(20));
        text.setFill(Color.WHITE);

        bg = new Rectangle(300, 30);
        bg.setFill(Color.BLACK);
        bg.setStroke(Color.WHITE);
        bg.setEffect(new GaussianBlur(3.5));

        setAlignment(Pos.CENTER);
        getChildren().addAll(bg, text);
        this.setOnMouseEntered(event -> gainedFocus());
        this.setOnMouseExited(event -> lostFocus());
    }

    public String getText(){
        return text.getText();
    }

    public void gainedFocus(){
        bg.setFill(Color.WHITE);
        text.setFill(Color.BLACK);
    }

    public void lostFocus(){
        bg.setFill(Color.BLACK);
        text.setFill(Color.WHITE);
    }
}

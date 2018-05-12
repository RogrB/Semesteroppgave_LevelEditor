package leveleditor;

import javafx.scene.image.Image;

public enum Sprite {

    // Enemies
    
    BLUE1("assets/image/enemies/enemyBlue1.png"),
    BLUE2("assets/image/enemies/enemyBlue2.png"),
    GREEN1("assets/image/enemies/enemyGreen1.png"),
    ORANGE1("assets/image/enemies/enemyOrange1.png"),
    RED1("assets/image/enemies/enemyRed1.png"),
    RED2("assets/image/enemies/enemyRed2.png"),
    RED3("assets/image/enemies/enemyRed3.png"),
    REDBIG("assets/image/enemies/enemyRedBig.png"),
    
    UFOBLUE("assets/image/enemies/ufoBlue.png"),
    UFOGREEN("assets/image/enemies/ufoGreen.png"),
    UFORED("assets/image/enemies/ufoRed.png"),
    UFOYELLOW("assets/image/enemies/ufoYellow.png"),
    
    METEOR("assets/image/enemies/meteor/meteor001.png"),
    
    BOSS01("assets/image/enemies/boss01.png"),
    BOSS02("assets/image/enemies/boss02.png");
  

    public String src;

    Sprite(String src){
        this.src = src;
    }
    
    public String getSrc() {
        return this.src;
    }

    public int getHeight(){
        return (int) new Image(src).getHeight();
    }

    public int getWidth(){
        return (int) new Image(src).getWidth();
    }
}

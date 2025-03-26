package classes;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

//todo:
//  - a kép útvonal még lehet rossz
//  - a többit kb a Placeableből másoltam
//  - a radiust majd ki kell találni
//  - valszeg kell majd egy ilyen irány field hogy éppen merre megyünk mert a képed majd forgatni kell


//mire kell az absrtact?
public class Jeep extends Pane{
    public int x, y, size;
    protected ImageView picture;
    public int radius;
    public int happyBonus;
    public int speciesSeen;

    public Jeep(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.radius = 0; //todo
        this.happyBonus = 0;
        this.speciesSeen = 0;

        Image img = new Image(getClass().getResource("/images/jeep.png").toExternalForm());
        this.picture = new ImageView(img);

        //this.picture.setLayoutX(0);
        //this.picture.setLayoutX(0);
        this.picture.setFitWidth(size);
        this.picture.setFitHeight(size);

        this.getChildren().add(picture);
        //placing the pane itself
        //setLayoutX(x);
        //setLayoutY(y);

    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        this.picture.setX(this.x);
        this.picture.setY(this.y);
    }


}
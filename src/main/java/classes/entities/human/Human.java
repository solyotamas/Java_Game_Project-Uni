package classes.entities.human;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


//mire kell az absrtact?
public abstract class Human extends Pane{
    protected int x, y, size;
    protected ImageView picture;

    public Human(int x, int y, int size, String imgURL) {
        this.x = x;
        this.y = y;
        this.size = size;

        Image img = new Image(getClass().getResource(imgURL).toExternalForm());
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

}
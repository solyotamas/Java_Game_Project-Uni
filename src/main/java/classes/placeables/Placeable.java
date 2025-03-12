package classes.placeables;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Placeable {
    protected int x, y, size;
    protected ImageView imageView;

    public Placeable(int x, int y, int size, String imgURL) {
        this.x = x;
        this.y = y;
        this.size = size;

        Image image = new Image(getClass().getResource(imgURL).toExternalForm());
        this.imageView = new ImageView(image);
        this.imageView.setX(x);
        this.imageView.setY(y);
        this.imageView.setFitWidth(size);
        this.imageView.setFitHeight(size);
    }

    public void draw(Pane gamePane) {
        gamePane.getChildren().add(imageView);
    }
}
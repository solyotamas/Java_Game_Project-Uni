package classes;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Tile {
    protected int x, y, width, height;
    protected String imgURL;
    protected ImageView imageView;

    public Tile(int x, int y, int width, int height, String imgURL) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.imgURL = imgURL;

        Image image = new Image(getClass().getResource(imgURL).toExternalForm());
        this.imageView = new ImageView(image);
        this.imageView.setX(x);
        this.imageView.setY(y);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
    }

    public void draw(Pane gamePane) {
        gamePane.getChildren().add(imageView);
    }

    public void setX(int tileX) {
        this.x = tileX;
        imageView.setX(tileX);
    }

    public void setY(int tileY) {
        this.y = tileY;
        imageView.setY(tileY);
    }
}
package classes.terrains;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Terrain {
    protected int x, y, size;
    protected ImageView imageView;
    protected int crossingDifficulty;

    public Terrain(int x, int y, int size, String imgURL, int crossingDifficulty) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.crossingDifficulty = crossingDifficulty;

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
package classes.placeables;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Placeable extends Pane{
    protected static final int TILE_SIZE = 30;

    protected int x, y;
    protected final int widthInTiles;
    protected final int heightInTiles;
    protected ImageView picture;

    public Placeable(int x, int y, int widthInTiles, int heightInTiles, String imgURL) {
        this.x = x;
        this.y = y;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;

        Image img = new Image(getClass().getResource(imgURL).toExternalForm());
        this.picture = new ImageView(img);

        this.picture.setFitWidth(widthInTiles * TILE_SIZE);
        this.picture.setFitHeight(heightInTiles * TILE_SIZE);

        this.getChildren().add(picture);

    }

    public int getWidthInTiles() {
        return widthInTiles;
    }

    public int getHeightInTiles() {
        return heightInTiles;
    }

}
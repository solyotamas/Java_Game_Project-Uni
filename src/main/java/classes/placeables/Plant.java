package classes.placeables;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Plant extends Pane {
    private static final int TILE_SIZE = 30;

    private int widthInTiles;
    private int heightInTiles;
    private int x;
    private int y;
    private ImageView picture;


    public Plant(int x, int y, int heightInTiles, int widthInTiles, String imgURL) {
        //tulajdonsagok
        this.x = x;
        this.y = y;
        this.heightInTiles = heightInTiles;
        this.widthInTiles = widthInTiles;

        //kép
        Image img = new Image(getClass().getResource(imgURL).toExternalForm());
        this.picture = new ImageView(img);
        this.picture.setFitWidth(widthInTiles * TILE_SIZE);
        this.picture.setFitHeight(heightInTiles * TILE_SIZE);

        //kép a Pane-be
        this.getChildren().add(picture);
    }
}
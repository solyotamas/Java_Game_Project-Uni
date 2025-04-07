package classes.landforms;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Landform extends Pane{
    protected static final int TILE_SIZE = 30;

    protected double x, y;
    protected double depth;
    protected final int widthInTiles;
    protected final int heightInTiles;
    protected ImageView picture;

    public Landform(double x, double y, int widthInTiles, int heightInTiles, Image img, double depth) {
        this.x = x;
        this.y = y;
        this.depth = depth;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;

        this.picture = new ImageView(img);
        this.picture.setFitWidth(widthInTiles * TILE_SIZE);
        this.picture.setFitHeight(heightInTiles * TILE_SIZE);

        setLayoutX(x);
        setLayoutY(y);

        this.getChildren().add(picture);

    }

    public int getWidthInTiles() {
        return widthInTiles;
    }
    public int getHeightInTiles() {
        return heightInTiles;
    }

    public double getDepth(){
        return this.depth;
    }
    public void setDepth(double depth){
        this.depth = depth;
    }

    public ImageView getPicture(){
        return this.picture;
    }

    public void setPicture(Image img){
        picture.setImage(img);
    }



}
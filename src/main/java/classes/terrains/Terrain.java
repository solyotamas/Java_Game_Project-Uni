package classes.terrains;

import classes.landforms.Landform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Terrain extends Pane {

    private static final int SIZE = 30;
    protected int row;
    protected int col;

    protected ImageView background;
    protected int crossingDifficulty;
    protected boolean walkable;

    private Landform landform;

    public Terrain(int row, int col, Image img, int crossingDifficulty, boolean walkable) {
        //properties
        this.row = row;
        this.col = col;
        this.crossingDifficulty = crossingDifficulty;
        this.setPrefSize(SIZE, SIZE);
        this.walkable = walkable;

        //background
        this.background = new ImageView(img);
        this.background.setFitWidth(SIZE);
        this.background.setFitHeight(SIZE);

        //placing the pane itself
        setLayoutX(row * SIZE);
        setLayoutY(col * SIZE);

        //adding to pane directly
        getChildren().add(background);
    }

    public void placeLandform(Landform landform) {
        this.landform = landform;
    }

    public boolean hasLandform() {
        return landform != null;
    }

    public Landform getLandform() {
        return this.landform;
    }

    public void setLandform(Landform landform) {
        this.landform = landform;
    }

    public int getSize() {
        return SIZE;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public int getCrossingDifficulty() {
        return crossingDifficulty;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + col + ", " + row + ")";
    }

}
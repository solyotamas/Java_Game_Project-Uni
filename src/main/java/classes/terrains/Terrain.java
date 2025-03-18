package classes.terrains;

import classes.placeables.Plant;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Terrain extends Pane{

    private static final int SIZE = 30;
    protected int row;
    protected int col;

    protected ImageView background;
    protected int crossingDifficulty;

    private Plant plant;

    public Terrain(int row, int col, Image img, int crossingDifficulty) {
        //tulajdonsagok
        this.row = row;
        this.col = col;
        this.crossingDifficulty = crossingDifficulty;
        this.setPrefSize(SIZE, SIZE);

        //Hatter
        this.background = new ImageView(img);
        this.background.setFitWidth(SIZE);
        this.background.setFitHeight(SIZE);

        //placing the pane itself
        setLayoutX(row * SIZE);
        setLayoutY(col * SIZE);

        //adding to pane directly
        getChildren().add(background);
    }


    public void placePlant(Plant plant) {
        this.plant = plant;
    }

    public boolean hasPlant() {
        return plant != null;
    }




}
package testing;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane{
    private int row;
    private int col;
    private double movementCost;

    private Rectangle node;
    private Image texture;

    private Terrain terrain;
    private static final Image base = new Image(Tile.class.getResourceAsStream("/images/road.png"));

    private static final Image clickedTexture = new Image(Tile.class.getResourceAsStream("/images/tree.png"));

    public Tile(int row, int col, double movementCost, int tilesize){
        this.row = row;
        this.col = col;
        this.movementCost = movementCost;
        //this.texture = texture;

        node = new Rectangle(tilesize, tilesize);
        node.setFill(new ImagePattern(base));

        node.setStrokeWidth(0.5);
        node.setStroke(Color.BLACK);

        getChildren().add(node);
    }


    // ✅ Handles texture change on click
    private void handleClick() {
        System.out.println("Tile clicked at:" + row + " " + col);
        node.setFill(new ImagePattern(clickedTexture));
    }
}

package classes.placeables;

import java.util.ArrayList;

public class Herbivore extends Animal {
    public Herbivore(int x, int y, int widthInTiles, int heightInTiles, String imgURL, ArrayList<Placeable> consumes) {
        super(x, y, widthInTiles, heightInTiles, imgURL, consumes);
    }
}
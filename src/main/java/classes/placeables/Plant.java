package classes.placeables;

public abstract class Plant extends Placeable {
    public Plant(int x, int y, int widthInTiles, int heightInTiles, String imgURL) {
        super(x, y, widthInTiles, heightInTiles, imgURL);
    }
}
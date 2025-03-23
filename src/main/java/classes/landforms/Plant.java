package classes.landforms;

public abstract class Plant extends Landform {
    public Plant(double x, double y, int widthInTiles, int heightInTiles, String imgURL, double depth) {
        super(x, y, widthInTiles, heightInTiles, imgURL, depth);
    }
}
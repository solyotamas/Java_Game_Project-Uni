package classes.landforms;

public class Lake extends Landform {
    private static final double depth = 0.0;

    public Lake(double x, double y) {
        super(x, y, 4, 2, "/images/lake.png", depth);
    }
}
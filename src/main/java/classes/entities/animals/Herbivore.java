package classes.entities.animals;

public abstract class Herbivore extends Animal {
    public Herbivore(double x, double y, int frameWidth, int frameHeight, String childImgUrl, String imgURL, double speed, int price, int life_expectancy, boolean isChild) {
        super(x, y, frameWidth, frameHeight, childImgUrl, imgURL, speed, price, life_expectancy, isChild);
    }

}
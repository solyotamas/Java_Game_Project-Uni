package classes.entities.animals;

public abstract class Herbivore extends Animal {
    public Herbivore(double x, double y, int frameWidth, int frameHeight, String imgURL, double speed, int price, int life_expectancy) {
        super(x, y, frameWidth, frameHeight, imgURL, speed, price, life_expectancy);
    }

}
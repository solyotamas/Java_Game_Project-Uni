package classes.entities.animals;

public abstract class Carnivore extends Animal {

    public Carnivore(double x, double y, int frameWidth, int frameHeight, String imgURL, double speed, int price){
        super(x,y,frameWidth, frameHeight, imgURL, speed, price);
    }
}
package classes.entities.animals;

import classes.landforms.plants.Plant;

import java.util.ArrayList;
import java.util.Random;

//public abstract class Herbivore extends Animal<Plant> {

public abstract class Herbivore extends Animal {
    public Herbivore(double x, double y, int frameWidth, int frameHeight, String imgURL, double speed, int price) {
        super(x, y, frameWidth, frameHeight, imgURL, speed, price);
    }

}
package classes.entities.animals.herbivores;

import classes.entities.animals.Herbivore;

public class Elephant extends Herbivore {

    private static final int price = 3000;
    private static final int lifeExpectancy = 60;

  private static final int frameWidth = 90;
  private static final int frameHeight = 88;
  private static final double speed = 0.4;
  private static final String imgURL = "/images/animated/elephant.png";
    private static final String childImgURL = "/images/animated/elephant_baby.png";

  public Elephant(double x, double y, boolean isChild) {
      super(x, y, frameWidth, frameHeight, childImgURL, imgURL, speed, price, lifeExpectancy, isChild);
  }
}

package classes.entities.animals;

import classes.entities.Direction;
import classes.landforms.plants.Plant;

import java.util.ArrayList;
import java.util.Random;

//public abstract class Carnivore extends Animal<Herbivore> {
public abstract class Carnivore extends Animal {
    private Herbivore prey;

    public Carnivore(double x, double y, int frameWidth, int frameHeight, String imgURL, double speed, int price){
        super(x,y,frameWidth, frameHeight, imgURL, speed, price);
    }


    public void huntTarget() {
        if (prey == null) {
            this.state = AnimalState.IDLE;
            return;
        }

        double targetX = prey.getX();
        double targetY = prey.getY();

        double dx = targetX - this.x;
        double dy = targetY - this.y;

        double distance = Math.hypot(dx, dy);
        if (distance < 2) {
            this.state = AnimalState.EATING;
            prey.transitionTo(AnimalState.PAUSED);
            return;
        }

        double stepX = (dx / distance) * speed;
        double stepY = (dy / distance) * speed;

        Direction dir;
        if (Math.abs(dx) > Math.abs(dy)) {
            dir = dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            dir = dy > 0 ? Direction.DOWN : Direction.UP;
        }

        move(dir, stepX, stepY);
    }
    public void choosePrey(ArrayList<Herbivore> herbivores) {
        if (herbivores.isEmpty()) return;
        this.prey = herbivores.get(new Random().nextInt(herbivores.size()));
    }
    public Herbivore getPrey(){
        return prey;
    }


    /*
    @Override
    public void rest(ArrayList<Herbivore> herbivores) {
        restingTimePassed += 0.05; // updateAnimalPositions() is 50ms

        if (restingTimePassed >= restDuration) {
//            System.out.println("wants to move but empty list");
            if (!herbivores.isEmpty()) {
                resting = false;
                restingTimePassed = 0.0;
                pickNewTarget(herbivores);
//                System.out.println("called pickNewTarget coz list notEmpty");
            }
        }
    }*/

    /*
    @Override
    public void pickNewTarget(ArrayList<Herbivore> herbivores) {
        Random random = new Random();
        prey = herbivores.get(random.nextInt(herbivores.size()));

        this.targetX = prey.getX(); // + (double )(randomPrey.getTileSize() / 2);
        this.targetY = prey.getY(); // + (double )(randomPrey.getTileSize() / 2);
        System.out.println("target picked: " + targetX +  " : " + targetY);

    }*/

    /*
    public void updateTarget() {
        if (this.prey != null) {
            this.targetX = prey.getX(); // + (double )(randomPrey.getTileSize() / 2);
            this.targetY = prey.getY(); // + (double )(randomPrey.getTileSize() / 2);
        }
    }*/


}
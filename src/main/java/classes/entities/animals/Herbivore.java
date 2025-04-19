package classes.entities.animals;

//public abstract class Herbivore extends Animal<Plant> {

public abstract class Herbivore extends Animal {
    public Herbivore(double x, double y, int frameWidth, int frameHeight, String imgURL, double speed, int price, int life_expectancy) {
        super(x, y, frameWidth, frameHeight, imgURL, speed, price, life_expectancy);
    }

    /*
    @Override
    public void rest(ArrayList<Plant> plants) {
        restingTimePassed += 0.05; // updateAnimalPositions() is 50ms

        if (restingTimePassed >= restDuration) {
            resting = false;
            restingTimePassed = 0.0;
            pickNewTarget(plants);
        }
    }*/

    /*
    @Override
    public void pickNewTarget(ArrayList<Plant> plants) {
        Random random = new Random();
        Plant randomPlant = plants.get(random.nextInt(plants.size()));

        this.targetX = randomPlant.getX() + (double) (randomPlant.getTileSize() / 2);  //* randomPlant.getTileSize(); //* randomPlant.getWidthInTiles(); //randomPlant.getLayoutX();
        this.targetY = randomPlant.getY() + (double) (randomPlant.getTileSize() / 2);  //* randomPlant.getTileSize(); //* randomPlant.getHeightInTiles(); //randomPlant.getLayoutY();
        // System.out.println("target picked: " + targetX +  " : " +targetY);


    }*/

}
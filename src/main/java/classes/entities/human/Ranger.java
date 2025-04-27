package classes.entities.human;

import classes.entities.Direction;
import classes.entities.animals.AnimalState;
import classes.entities.animals.Carnivore;
import java.util.Random;

public class Ranger extends Human {

    private static final int frameWidth = 36;//104;
    private static final int frameHeight = 46;//106;
    private static double speed = 1.5;
    private static final String imgURL = "/images/animated/ranger.png";
    private double lastPaidHour;

    private Carnivore prey;

    public Ranger(double x, double y){
        super(x, y, frameWidth, frameHeight, imgURL, speed);
    }

    @Override
    public void pickNewTarget() {
        Random random = new Random();
        double minY = this.getImageView().getFitHeight();
        double maxY = 31 * 30;
        double minX = 5 * 30 + this.getImageView().getFitWidth() / 2;
        double maxX = 59 * 30 - this.getImageView().getFitWidth() / 2;


        this.targetX = minX + random.nextDouble() * (maxX - minX);
        this.targetY = minY + random.nextDouble() * (maxY - minY);

    }

    // ==== HIRING
    public void setLastPaidHour(double lastPaidHour) {
        this.lastPaidHour = lastPaidHour;
    }

    public boolean isDueForPayment(double currentGameHour) {
        return currentGameHour - lastPaidHour >= 720; // 30 days * 24 hours
    }
    // =====

    // ==== HUNTING
    public void choosePrey(Carnivore carnivore) {
        if (carnivore == null) return;
        this.prey = carnivore;
    }

    public void huntTarget() {
        if (prey == null) {
            this.state = HumanState.IDLE;
            return;
        }

        double targetX = prey.getX();
        double targetY = prey.getY();

        double dx = targetX - this.x;
        double dy = targetY - this.y;

        double distance = Math.hypot(dx, dy);
        if (distance < 2) {
            this.state = HumanState.CAPTURED;
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
    public Carnivore getPrey() {
        return prey;
    }
    // =====

}
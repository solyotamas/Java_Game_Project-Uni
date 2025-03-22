package classes.entities.human;

import classes.entities.Human;

public class Ranger extends Human {

    public boolean paid;

    public Ranger(int x, int y, int size, String imgURL) {
        super(x, y, size, imgURL);
        this.paid = true;
    }

    public void unepmloye() {

    }
}

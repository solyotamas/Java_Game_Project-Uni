package classes.entities.human;
//todo
//  most ezt Objectnek írtam mert a Rangernek lehet Animal és Poacher is
//  Poachernek csak Animal lehet
//bár az is lehet hogy amikor visszalő a Rangerre akkor már ő az idk

import classes.entities.Human;

public class Hunter extends Human {
    private Object target;
    public int radius;

    public Hunter(int x, int y, int size, String imgURL) {
        super(x, y, size, imgURL);
        this.target = null;
    }
}

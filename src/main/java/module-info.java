module projekt.szafari {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens classes to javafx.fxml;
    exports classes.screens;
    opens classes.screens to javafx.fxml;
    exports classes.controllers;
    opens classes.controllers to javafx.fxml;
    exports classes.terrains;
    opens classes.terrains to javafx.fxml;
    exports classes;

    exports classes.entities.human;
    opens classes.entities.human to javafx.fxml;

}
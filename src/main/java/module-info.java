module projekt.szafari {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens classes to javafx.fxml;
    exports classes.screens;
    opens classes.screens to javafx.fxml;
    exports classes.controllers;
    opens classes.controllers to javafx.fxml;
    exports classes.terrains;
    opens classes.terrains to javafx.fxml;
    exports classes;
}
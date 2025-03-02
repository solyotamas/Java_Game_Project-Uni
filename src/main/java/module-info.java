module projekt.szafari {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens classes to javafx.fxml;
    exports classes;
}
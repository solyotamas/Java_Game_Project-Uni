module testing {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens testing to javafx.fxml;
    exports testing;
}
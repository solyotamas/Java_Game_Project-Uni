package classes.game;

import javafx.scene.control.Button;



public class Market extends Button {

    private Button ui;

    public Market(Button ui){
        this.ui = ui;

        setOnAction(e -> onClick());
    }

    private void onClick() {
        System.out.println("Market button clicked!");

        // Example action: Change the UI button text
        if (ui != null) {
            ui.setText("Market Clicked!");
        }

        // Additional logic here (like opening a new window or overlay)
    }

}

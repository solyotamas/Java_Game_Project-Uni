package classes.terrains;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class River extends Terrain {

    private static Image riverImage;
    /*
    private Image spritesheet = new Image(getClass().getResource("/images/animated/water.png").toExternalForm());
    private Image animationImages[] = new Image[3];
    private ImageView imageview;
    private int currentImage = 0;
    private int flowDelay = 0;*/

    public River(int row, int col) {
        super(row, col, riverImage, 2);

        /*
        loadAnimationImages();
        imageview = new ImageView(animationImages[0]);

        imageview.setFitHeight(30);
        imageview.setFitWidth(30);
        getChildren().add(imageview);*/
    }

    static {
        riverImage = new Image(River.class.getResource("/images/river.png").toExternalForm());
    }

    /*
    public void loadAnimationImages(){
        for (int i = 0; i < 3; i++) {
            animationImages[i] = new WritableImage(spritesheet.getPixelReader(), i+2 * 16, 0, 16, 16);
        }
    }

    public void flow(){
        flowDelay++;
        if(flowDelay >= 3){
            currentImage = (currentImage + 1) % animationImages.length;
            flowDelay = 0;
        }
        imageview.setImage(animationImages[currentImage]);
    }*/
}
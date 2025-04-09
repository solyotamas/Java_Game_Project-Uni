package classes.entities.additions;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GhostImage extends ImageView {
    private final double depth = Double.MAX_VALUE;

    public GhostImage(ImageView sourceImageView) {
        super(sourceImageView.getImage());

        // Optional: match size and styling
        setFitWidth(sourceImageView.getFitWidth());
        setFitHeight(sourceImageView.getFitHeight());
        setPreserveRatio(sourceImageView.isPreserveRatio());
        setOpacity(0.5);
        setMouseTransparent(true);
    }

    public double getDepth(){
        return this.depth;
    }


}

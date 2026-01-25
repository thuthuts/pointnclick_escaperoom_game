package project.animation;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.scene.Node;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 * Node Animation in x/y direction that will also autoreverse.
 */
public class PathAnimation {

    /**
     * Animate Node into x/y direction.
     * @param node that is being animated.
     * @param xOffset value of x direction offset.
     * @param yOffset value of y direction offset.
     * @return PathTransition Obj for stopping the animation.
     */
    public static PathTransition addPathTransition(Node node, int xOffset, int yOffset) {
        PathTransition transition = new PathTransition();
        transition.setNode(node);
        transition.setDuration(Duration.millis(700));

        Path path = new Path();
        path.getElements().add(new MoveToAbs(node));
        path.getElements().add(new LineToAbs(node, node.getLayoutX() + xOffset, node.getLayoutY() + yOffset));

        transition.setPath(path);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();
        return transition;
    }

    public static class MoveToAbs extends MoveTo {

            public MoveToAbs(Node node) {
                super(node.getLayoutBounds().getWidth() / 2, node.getLayoutBounds().getHeight() / 2);
            }

            public MoveToAbs(Node node, double x, double y) {
                super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
            }

        }

    public static class LineToAbs extends LineTo {

            public LineToAbs(Node node, double x, double y) {
                super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
            }

        }
}


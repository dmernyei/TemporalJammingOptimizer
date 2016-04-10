package temporaljammingoptimizer.utils;

import temporaljammingoptimizer.logic.geometry.points.Point;

/**
 * Created by Daniel Mernyei
 */
public class MathUtilities {
    public static double computeDistance(Point p1, Point p2){
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }
}

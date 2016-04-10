package temporaljammingoptimizer.logic.geometry;

import temporaljammingoptimizer.logic.geometry.points.Point;

/**
 * Created by Daniel Mernyei
 */
public class Size {

    private int width;
    private int height;

    public Size(int width, int height){
        this.width = width;
        this.height = height;
    }

    public boolean isPointInsideArea(Point p){
        int x = p.getX();
        int y = p.getY();

        return 0 <= x && width >= x && 0 <= y && height >= y;
    }
}

package temporaljammingoptimizer.logic.geometry;

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

    public boolean isPositionInsideArea(Vector2 position){
        int x = position.getX();
        int y = position.getY();

        return 0 <= x && width >= x && 0 <= y && height >= y;
    }
}

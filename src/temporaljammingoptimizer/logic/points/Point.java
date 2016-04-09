package temporaljammingoptimizer.logic.points;

/**
 * Created by Daniel Mernyei
 */
public class Point {

    private int x;
    private int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o){
        if (null == o || !(o instanceof Point))
            return false;

        Point p = (Point)o;
        return p.getX() == x && p.getY() == y;
    }
}

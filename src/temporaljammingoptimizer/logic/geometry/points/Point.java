package temporaljammingoptimizer.logic.geometry.points;

/**
 * Created by Daniel Mernyei
 */
public class Point {

    protected static int indexGenerator = 0;

    protected int index;

    private int x;
    private int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getIndex(){
        return index;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static void resetIndexGenerator(){
        Point.indexGenerator = 0;
    }

    @Override
    public boolean equals(Object o){
        if (null == o || !(o instanceof Point))
            return false;

        Point p = (Point)o;
        return p.getX() == x && p.getY() == y;
    }

    @Override
    public int hashCode(){
        return Integer.parseInt(x + "" + y);
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }
}

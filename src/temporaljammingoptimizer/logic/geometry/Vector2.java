package temporaljammingoptimizer.logic.geometry;

/**
 * Created by Daniel Mernyei
 */
public class Vector2 implements Cloneable {
    private int x;
    private int y;

    public Vector2(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2 subtract(Vector2 other){
        return new Vector2(x - other.getX(), y - other.getY());
    }

    public Vector2 add(Vector2 other){
        return new Vector2(x + other.getX(), y + other.getY());
    }

    public Vector2 multiplyScalar(float scalar){
        return new Vector2(Math.round(x * scalar), Math.round(y * scalar));
    }

    private float getLength(){
        return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    private float getLengthSquared(){
        return (float)(Math.pow(x, 2) + Math.pow(y, 2));
    }

    @Override
    public boolean equals(Object o){
        if (null == o || !(o instanceof Vector2))
            return false;

        Vector2 v = (Vector2)o;
        return v.getX() == x && v.getY() == y;
    }

    @Override
    public int hashCode(){
        return Integer.parseInt(x + "" + y);
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }

    public static float distance(Vector2 v1, Vector2 v2){
        return v1.subtract(v2).getLength();
    }

    public static float distanceSquared(Vector2 v1, Vector2 v2){
        return v1.subtract(v2).getLengthSquared();
    }

    public static int dot(Vector2 v1, Vector2 v2){
        return v1.getX() * v2.getX() + v1.getY() * v2.getY();
    }
}

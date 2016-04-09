package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.logic.points.Point;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class Polygon {

    private ArrayList<Point> vertices;

    public Polygon(){
        vertices = new ArrayList<>();
    }

    public Polygon(ArrayList<Point> vertices){
        this.vertices = vertices;
    }

    public int getVertexCount(){
        return vertices.size();
    }

    public Point getVertexAt(int index){
        return vertices.get(index);
    }

    public void addVertex(Point p){
        vertices.add(p);
    }

    public void clear(){
        vertices.clear();
    }

    public boolean isConvexAndHasArea(){
        int side, lastSideOfLine = 0;
        int length = vertices.size();
        Point segmentVertex1, segmentVertex2, testVertex;
        boolean verticesLieInOneLine = true;

        if (length < 3)
            return false;

        for (int i = 0; i < length; ++i)
        {
            segmentVertex1 = vertices.get(i);
            segmentVertex2 = vertices.get((i + 1) % length);
            testVertex = vertices.get((i + 2) % length);

            side = applyLineEquation(segmentVertex1, segmentVertex2, testVertex);

            if (0 != side){
                if (!verticesLieInOneLine && Math.signum(side) != Math.signum(lastSideOfLine))
                    return false;

                verticesLieInOneLine = false;
                lastSideOfLine = side;
            }
        }
        return !verticesLieInOneLine;
    }

    public boolean isPointInsidePolygon(Point p){
        Point segmentVertex1, segmentVertex2;
        int length = vertices.size();

        int side, lastSideOfLine = 0;

        for (int i = 0; i < length; ++i)
        {
            segmentVertex1 = vertices.get(i);
            segmentVertex2 = vertices.get((i + 1) % length);

            side = applyLineEquation(segmentVertex1, segmentVertex2, p);

            if (0 == side || 0 < i && Math.signum(side) != Math.signum(lastSideOfLine))
                return false;

            lastSideOfLine = side;
        }
        return true;
    }

    private int applyLineEquation(Point segmentVertex1, Point segmentVertex2, Point testPoint){
        // Converting the current side to a line with infinite length. (linear equation standard form: A*x + B*y + C = 0)
        int a = segmentVertex2.getY() - segmentVertex1.getY();
        int b = segmentVertex1.getX() - segmentVertex2.getX();
        int c = (segmentVertex2.getX() * segmentVertex1.getY()) - (segmentVertex1.getX() * segmentVertex2.getY());

        // Determining which side the investigated position is on relative to the line equation
        return (a * testPoint.getX()) + (b * testPoint.getY()) + c;
    }
}

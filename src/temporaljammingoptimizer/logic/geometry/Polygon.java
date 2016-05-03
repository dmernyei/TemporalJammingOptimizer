package temporaljammingoptimizer.logic.geometry;

import temporaljammingoptimizer.utilities.MathUtilities;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class Polygon {

    private ArrayList<Vector2> vertices;

    public Polygon(){
        vertices = new ArrayList<>();
    }

    public Polygon(ArrayList<Vector2> vertices){
        this.vertices = vertices;
    }

    public int getVertexCount(){
        return vertices.size();
    }

    public Vector2 getVertexAt(int index){
        return vertices.get(index);
    }

    public ArrayList<Vector2> getVertices(){
        return vertices;
    }

    public void addVertex(Vector2 vertex){
        vertices.add(vertex);
    }

    public void clear(){
        vertices.clear();
    }

    public boolean isConvexAndHasArea(){
        int side, lastSideOfLine = 0;
        int length = vertices.size();
        Vector2 segmentVertex1, segmentVertex2, testVertex;
        boolean verticesLieInOneLine = true;

        if (length < 3)
            return false;

        for (int i = 0; i < length; ++i)
        {
            segmentVertex1 = vertices.get(i);
            segmentVertex2 = vertices.get((i + 1) % length);
            testVertex = vertices.get((i + 2) % length);

            side = MathUtilities.applyLineEquation(segmentVertex1, segmentVertex2, testVertex);

            if (0 != side){
                if (!verticesLieInOneLine && Math.signum(side) != Math.signum(lastSideOfLine))
                    return false;

                verticesLieInOneLine = false;
                lastSideOfLine = side;
            }
        }
        return !verticesLieInOneLine;
    }

    public boolean isPositionInsidePolygon(Vector2 position){
        Vector2 segmentVertex1, segmentVertex2;
        int length = vertices.size();

        int side, lastSideOfLine = 0;

        for (int i = 0; i < length; ++i)
        {
            segmentVertex1 = vertices.get(i);
            segmentVertex2 = vertices.get((i + 1) % length);

            side = MathUtilities.applyLineEquation(segmentVertex1, segmentVertex2, position);

            if (0 == side || 0 < i && Math.signum(side) != Math.signum(lastSideOfLine))
                return false;

            lastSideOfLine = side;
        }
        return true;
    }
}

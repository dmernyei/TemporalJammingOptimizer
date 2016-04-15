package temporaljammingoptimizer.utils;

import temporaljammingoptimizer.logic.geometry.Vector2;

/**
 * Created by Daniel Mernyei
 */
public class MathUtilities {
    public static int applyLineEquation(Vector2 segmentVertex1, Vector2 segmentVertex2, Vector2 testPosition){
        // Converting the current side to a line with infinite length. (linear equation standard form: A*x + B*y + C = 0)
        int a = segmentVertex2.getY() - segmentVertex1.getY();
        int b = segmentVertex1.getX() - segmentVertex2.getX();
        int c = (segmentVertex2.getX() * segmentVertex1.getY()) - (segmentVertex1.getX() * segmentVertex2.getY());

        // Determining which side the investigated position is on relative to the line equation
        return (a * testPosition.getX()) + (b * testPosition.getY()) + c;
    }

    public static Vector2 closestPointInSegmentToOuterPoint(Vector2 segmentVertex1, Vector2 segmentVertex2, Vector2 point) {
        float segmentLengthSquared = Vector2.distanceSquared(segmentVertex1, segmentVertex2);
        if (0 == segmentLengthSquared)
            return segmentVertex1;

        Vector2 segmentVector = segmentVertex2.subtract(segmentVertex1);
        float t = Math.max(0, Math.min(1, Vector2.dot(point.subtract(segmentVertex1), segmentVector) / segmentLengthSquared));

        return segmentVertex1.add(segmentVector.multiplyScalar(t));
    }
}

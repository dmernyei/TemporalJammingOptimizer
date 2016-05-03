package temporaljammingoptimizer.logic;

/**
 * Created by Daniel Mernyei
 */
public class JammerDistanceInfo implements Comparable<JammerDistanceInfo> {
    private int index;
    private float distance;

    public JammerDistanceInfo(int index, float distance){
        this.index = index;
        this.distance = distance;
    }

    public int getIndex() {
        return index;
    }

    private double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(JammerDistanceInfo o) {
        return (int)Math.signum(distance - o.getDistance());
    }
}

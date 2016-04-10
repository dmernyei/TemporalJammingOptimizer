package temporaljammingoptimizer.utils;

/**
 * Created by Daniel Mernyei
 */
public class JammerDistanceInfo implements Comparable<JammerDistanceInfo> {
    private int index;
    private double distance;

    public JammerDistanceInfo(int index, double distance){
        this.index = index;
        this.distance = distance;
    }

    public int getIndex() {
        return index;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(JammerDistanceInfo o) {
        return (int)Math.signum(distance - o.getDistance());
    }
}

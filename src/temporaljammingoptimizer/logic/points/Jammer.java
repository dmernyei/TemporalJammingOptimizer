package temporaljammingoptimizer.logic.points;

/**
 * Created by Daniel Mernyei
 */
public class Jammer extends Point {

    private float activityProbability = -1;

    public Jammer(int x, int y) {
        super(x, y);
    }

    public float getActivityProbability() {
        return activityProbability;
    }

    public void setActivityProbability(float activityProbability) {
        this.activityProbability = activityProbability;
    }
}

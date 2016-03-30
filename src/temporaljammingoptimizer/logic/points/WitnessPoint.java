package temporaljammingoptimizer.logic.points;

/**
 * Created by Daniel Mernyei
 */
public class WitnessPoint extends Point {

    private boolean isInsideStorage;

    public WitnessPoint(int x, int y, boolean isInsideStorage) {
        super(x, y);
        this.isInsideStorage = isInsideStorage;
    }

    public boolean isInsideStorage() {
        return isInsideStorage;
    }
}

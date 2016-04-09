package temporaljammingoptimizer.logic.points;

/**
 * Created by Daniel Mernyei
 */
public class WitnessPoint extends Point {

    private boolean isInsideControlledRegion;

    public WitnessPoint(int x, int y, boolean isInsideControlledRegion) {
        super(x, y);
        this.isInsideControlledRegion = isInsideControlledRegion;
    }

    public boolean isInsideControlledRegion() {
        return isInsideControlledRegion;
    }
}

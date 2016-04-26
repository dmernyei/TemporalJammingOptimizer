package temporaljammingoptimizer.logic.algorithms.twonearestjammeralgorithm;

/**
 * Created by Daniel Mernyei
 */
public class JammerCostInfo {
    private float cost;
    private int middleJAPIndex;

    public JammerCostInfo(){
        this.cost = Float.MAX_VALUE;
        this.middleJAPIndex = -1;       // todo: is this needed?
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getMiddleJAPIndex() {
        return middleJAPIndex;
    }

    public void setMiddleJAPIndex(int middleJAPIndex) {
        this.middleJAPIndex = middleJAPIndex;
    }
}

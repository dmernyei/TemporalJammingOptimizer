package temporaljammingoptimizer.logic.algorithms.twonearestjammeralgorithm;

/**
 * Created by Daniel Mernyei
 */
class JammerCostInfo {
    private float cost;
    private int middleJAPIndex;

    JammerCostInfo(){
        this.cost = Float.MAX_VALUE;
        this.middleJAPIndex = -1;
    }

    float getCost() {
        return cost;
    }

    void setCost(float cost) {
        this.cost = cost;
    }

    int getMiddleJAPIndex() {
        return middleJAPIndex;
    }

    void setMiddleJAPIndex(int middleJAPIndex) {
        this.middleJAPIndex = middleJAPIndex;
    }
}

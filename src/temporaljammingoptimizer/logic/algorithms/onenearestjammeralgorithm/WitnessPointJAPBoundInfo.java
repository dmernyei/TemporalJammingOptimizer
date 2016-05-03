package temporaljammingoptimizer.logic.algorithms.onenearestjammeralgorithm;

/**
 * Created by Daniel Mernyei
 */
public class WitnessPointJAPBoundInfo {
    private int witnessPointId;
    private float JAPBound;

    WitnessPointJAPBoundInfo(int witnessPointId, float JAPBound){
        this.witnessPointId = witnessPointId;
        this.JAPBound = JAPBound;
    }

    public int getWitnessPointId() {
        return witnessPointId;
    }

    public float getJAPBound() {
        return JAPBound;
    }
}

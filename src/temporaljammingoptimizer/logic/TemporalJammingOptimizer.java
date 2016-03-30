package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.logic.points.Jammer;
import temporaljammingoptimizer.logic.points.WitnessPoint;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class TemporalJammingOptimizer {

    private final Size mapSize;

    private float lowerTBEPThreshold;
    private float upperTBEPThreshold;
    private float propagationFactor1;
    private float propagationFactor2;
    private float jammingFactor1;
    private float jammingFactor2;
    private boolean stepByStep;

    private ArrayList<Jammer> jammers;
    private ArrayList<WitnessPoint> witnessPoints;

    public TemporalJammingOptimizer(Size mapSize){
        this.mapSize = mapSize;
        jammers = new ArrayList<>();
        witnessPoints = new ArrayList<>();
    }

}

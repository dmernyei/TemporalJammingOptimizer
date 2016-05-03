package temporaljammingoptimizer.logic.algorithms.onenearestjammeralgorithm;

import temporaljammingoptimizer.logic.algorithms.NearestJammerAlgorithm;
import temporaljammingoptimizer.logic.entities.Jammer;
import temporaljammingoptimizer.logic.entities.WitnessPoint;
import temporaljammingoptimizer.logic.exceptions.UnAssignableJammerException;
import temporaljammingoptimizer.logic.geometry.Vector2;
import temporaljammingoptimizer.utilities.MessageProvider;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class OneNearestJammerAlgorithm extends NearestJammerAlgorithm {
    private final float epsilon;

    private int nextJammerIndex;

    private ArrayList<WitnessPointJAPBoundInfo> storagePointJAPBoundInfos;
    private ArrayList<WitnessPointJAPBoundInfo> eavesdropperPointJAPBoundInfos;
    private float minStoragePointJAPBound = -1.0f;
    private float maxEavesdropperPointJAPBound = -1.0f;

    public OneNearestJammerAlgorithm(){
        storagePointJAPBoundInfos = new ArrayList<>();
        eavesdropperPointJAPBoundInfos = new ArrayList<>();
        epsilon = 0.01f;
    }

    public ArrayList<WitnessPointJAPBoundInfo> getStoragePointJAPBoundInfos(){
        return storagePointJAPBoundInfos;
    }

    public ArrayList<WitnessPointJAPBoundInfo> getEavesdropperPointJAPBoundInfos(){
        return eavesdropperPointJAPBoundInfos;
    }

    public float getMinStoragePointJAPBound(){
        return minStoragePointJAPBound;
    }

    public float getMaxEavesdropperPointJAPBound(){
        return maxEavesdropperPointJAPBound;
    }

    public boolean isFinished() {
        return nextJammerIndex >= jammers.length;
    }

    public void reset(){
        nextJammerIndex = 0;
        minStoragePointJAPBound = -1.0f;
        maxEavesdropperPointJAPBound = -1.0f;
        storagePointJAPBoundInfos.clear();
        eavesdropperPointJAPBoundInfos.clear();
    }

    public void computeJAPForNextJammer() throws UnAssignableJammerException {
        storagePointJAPBoundInfos.clear();
        eavesdropperPointJAPBoundInfos.clear();

        Jammer currentJammer = jammers[nextJammerIndex];
        ++nextJammerIndex;

        for (int i = 0; i < currentJammer.getNearbyWitnessPointCount(); ++i){
            computeJAPBound(currentJammer.getNearbyWitnessPointAt(i), currentJammer);
        }

        float currentJAPBound;
        minStoragePointJAPBound = storagePointJAPBoundInfos.get(0).getJAPBound();
        for (int i = 1; i < storagePointJAPBoundInfos.size(); ++i){
            currentJAPBound = storagePointJAPBoundInfos.get(i).getJAPBound();
            if (currentJAPBound < minStoragePointJAPBound)
                minStoragePointJAPBound = currentJAPBound;
        }

        maxEavesdropperPointJAPBound = eavesdropperPointJAPBoundInfos.get(0).getJAPBound();
        for (int i = 1; i < eavesdropperPointJAPBoundInfos.size(); ++i){
            currentJAPBound = eavesdropperPointJAPBoundInfos.get(i).getJAPBound();
            if (currentJAPBound > maxEavesdropperPointJAPBound)
                maxEavesdropperPointJAPBound = currentJAPBound;
        }

        if (maxEavesdropperPointJAPBound > minStoragePointJAPBound)
            throw new UnAssignableJammerException(MessageProvider.getMessage("defaultUnAssignableJammerExceptionMessageForOneNearestJammerModel"), currentJammer.getId());
        else
            currentJammer.setJAP(maxEavesdropperPointJAPBound);
    }

    private void computeJAPBound(WitnessPoint witnessPoint, Jammer jammer) throws UnAssignableJammerException {
        boolean isStoragePoint = witnessPoint.isStoragePoint();
        float TBEPBound = isStoragePoint ? configuration.getUpperSTBEPBound() : configuration.getLowerETBEPBound();
        float JAP = -1;
        float step = 1.0f;
        float difference = 1;
        float TBEP;

        // Checking corner cases
        float zeroJAPSignum = Math.signum(TBEPBound - computeTBEP(witnessPoint, jammer, 0));
        float oneJAPSignum = Math.signum(TBEPBound - computeTBEP(witnessPoint, jammer, 1));
        if (zeroJAPSignum == oneJAPSignum){
            if (isStoragePoint){
                JAP = 1;
            }
            else{
                if (0 == zeroJAPSignum || -1 == zeroJAPSignum)
                    JAP = 0;
                else
                    throw new UnAssignableJammerException(MessageProvider.getMessage("tooLowTBEPForEavesdropper"), witnessPoint.getId());
            }
        }

        // Approximating JAP
        if (-1 == JAP){
            JAP = 0;
            do{
                step /= 2;
                JAP += Math.signum(difference) * step;
                TBEP = computeTBEP(witnessPoint, jammer, JAP);
                difference = TBEPBound - TBEP;
            }while (epsilon < Math.abs(difference));
        }

        // Assigning JAP
        WitnessPointJAPBoundInfo witnessPointJAPBoundInfo = new WitnessPointJAPBoundInfo(witnessPoint.getId(), JAP);
        if (isStoragePoint)
            storagePointJAPBoundInfos.add(witnessPointJAPBoundInfo);
        else
            eavesdropperPointJAPBoundInfos.add(witnessPointJAPBoundInfo);
    }

    private float computeTBEP(WitnessPoint witnessPoint, Jammer jammer, float activityProbability){
        float jammerDistanceFactor = computeJammerDistanceFactor(Vector2.distance(witnessPoint.getPosition(), jammer.getPosition()));

        if (witnessPoint.isStoragePoint()){
            return activityProbability * jammerDistanceFactor;
        }
        else{
            float storageDistanceFactor = computeStorageDistanceFactor(witnessPoint.getSmallestDistanceFromStorage());
            return (1 - activityProbability) * storageDistanceFactor
                    + activityProbability * Math.max(storageDistanceFactor, jammerDistanceFactor);
        }
    }
}

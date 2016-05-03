package temporaljammingoptimizer.logic.algorithms.twonearestjammeralgorithm;

import temporaljammingoptimizer.logic.algorithms.NearestJammerAlgorithm;
import temporaljammingoptimizer.logic.entities.Jammer;
import temporaljammingoptimizer.logic.entities.WitnessPoint;
import temporaljammingoptimizer.logic.exceptions.UnAssignableJammerException;
import temporaljammingoptimizer.logic.geometry.Vector2;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class TwoNearestJammerAlgorithm extends NearestJammerAlgorithm {
    private float[] JAPDomain;
    private JammerCostInfo[][][][] result;

    public TwoNearestJammerAlgorithm(){
        initializeActivityProbabilityDomain();
    }

    private void initializeActivityProbabilityDomain(){
        ArrayList<Integer> values = new ArrayList<>();
        int value = 0;
        int JAPDomainStep = 1;
        float divisor = 100.0f;

        do{
            values.add(value);
            value += JAPDomainStep;
        }while (value <= 100);

        JAPDomain = new float[values.size()];
        for (int i = 0; i < values.size(); ++i){
            JAPDomain[i] = values.get(i) / divisor;
        }
    }

    public void reset(){
        result = null;
    }

    public void computeActivityProbabilities() throws UnAssignableJammerException {
        initializeResult();

        computeBaseCaseCosts();
        computeCompositeCaseCosts();
        int[] powerOfTwoIndices = generatePowerOfTwoIndices(jammers.length);
        if (1 < powerOfTwoIndices.length){
            computeRemainderCompositeCosts(powerOfTwoIndices);
        }
        assignJAPToJammersFromResult();
    }

    private void initializeResult(){
        result = new JammerCostInfo[jammers.length][jammers.length][JAPDomain.length][JAPDomain.length];

        // upper triangular matrix (without last line)
        for (int i = 0; i < result.length - 1; ++i){
            for (int j = i + 1; j < result[0].length; ++j){
                for (int k = 0; k < result[0][0].length; ++k){
                    for (int l = 0; l < result[0][0][0].length; ++l){
                        result[i][j][k][l] = new JammerCostInfo();
                    }
                }
            }
        }

        // first column
        for (int i = 0; i < result.length; ++i){
            for (int k = 0; k < result[0][0].length; ++k){
                for (int l = 0; l < result[0][0][0].length; ++l){
                    result[i][0][k][l] = new JammerCostInfo();
                }
            }
        }
    }

    private void computeBaseCaseCosts(){
        int jammerCount = jammers.length;
        int JAPDomainSize = JAPDomain.length;
        int nextJammerIndex;

        for (int i = 0; i < jammerCount; ++i){
            nextJammerIndex = (i + 1) % jammerCount;
            for (int p1 = 0; p1 < JAPDomainSize; ++p1){
                for (int p2 = 0; p2 < JAPDomainSize; ++p2){
                    if (allCommonWitnessPointTBEPFitInsideBounds(i, nextJammerIndex, JAPDomain[p1], JAPDomain[p2])){
                        result[i][nextJammerIndex][p1][p2].setCost(JAPDomain[p1] + JAPDomain[p2]);
                    }
                }
            }
        }
    }

    private boolean allCommonWitnessPointTBEPFitInsideBounds(int jammerIndex1, int jammerIndex2, float activityProbability1, float activityProbability2){
        Jammer jammer1 = jammers[jammerIndex1];
        Jammer jammer2 = jammers[jammerIndex2];
        ArrayList<WitnessPoint> nearbyWitnessPoints = Jammer.intersectionOfNearbyWitnessPoints(jammer1, jammer2);
        float TBEP;
        boolean allTBEPFitsInsideBounds = true;

        for (WitnessPoint witnessPoint : nearbyWitnessPoints){
            TBEP = computeTBEP(witnessPoint, jammer1, jammer2, activityProbability1, activityProbability2);

            if (witnessPoint.isStoragePoint() && TBEP > configuration.getUpperSTBEPBound()
                    || !witnessPoint.isStoragePoint() && TBEP < configuration.getLowerETBEPBound()){
                allTBEPFitsInsideBounds = false;
                break;
            }
        }

        return allTBEPFitsInsideBounds;
    }

    private float computeTBEP(WitnessPoint witnessPoint, Jammer jammer1, Jammer jammer2, float activityProbability1, float activityProbability2){
        float inActivityProbability1 = 1 - activityProbability1;
        float inActivityProbability2 = 1 - activityProbability2;
        float jammer1DistanceFactor = computeJammerDistanceFactor(Vector2.distance(witnessPoint.getPosition(), jammer1.getPosition()));
        float jammer2DistanceFactor = computeJammerDistanceFactor(Vector2.distance(witnessPoint.getPosition(), jammer2.getPosition()));
        float storageDistanceFactor = computeStorageDistanceFactor(witnessPoint.getSmallestDistanceFromStorage());

        if (witnessPoint.isStoragePoint()){
            return activityProbability1 * inActivityProbability2 * jammer1DistanceFactor
                    + inActivityProbability1 * activityProbability2 * jammer2DistanceFactor;
        }
        else{
            return inActivityProbability1 * inActivityProbability2 * storageDistanceFactor
                    + activityProbability1 * inActivityProbability2 * Math.max(storageDistanceFactor, jammer1DistanceFactor)
                    + inActivityProbability1 * activityProbability2 * Math.max(storageDistanceFactor, jammer2DistanceFactor)
                    + activityProbability1 * activityProbability2 * Math.max(storageDistanceFactor, Math.max(jammer1DistanceFactor, jammer2DistanceFactor));
        }
    }

    private void computeCompositeCaseCosts(){
        int jammerCount = jammers.length;
        int middleJammerIndex;
        int jammer2Index;

        for (int step = 2; step <= jammerCount; step *= 2){
            for (int i = 0; i + step <= jammerCount; i += step){
                middleJammerIndex = i + (step / 2);
                jammer2Index = (i + step) % jammerCount;
                computeCompositeCaseCost(i, middleJammerIndex, jammer2Index);
            }
        }
    }

    private void computeRemainderCompositeCosts(int[] powerOfTwoIndices){
        int jammerCount = jammers.length;
        for (int i = 0; i < powerOfTwoIndices.length - 1; ++i){
            computeCompositeCaseCost(0, powerOfTwoIndices[i], powerOfTwoIndices[i + 1] % jammerCount);
        }
    }

    private void computeCompositeCaseCost(int jammer1Index, int middleJammerIndex, int jammer2Index){
        int JAPDomainSize = JAPDomain.length;
        JammerCostInfo compositeJammerCostInfo;
        float compositeCost;

        for (int p1 = 0; p1 < JAPDomainSize; ++p1){
            for (int p2 = 0; p2 < JAPDomainSize; ++p2){
                for (int mp = 0; mp < JAPDomainSize; ++mp){
                    compositeCost = result[jammer1Index][middleJammerIndex][p1][mp].getCost() + result[middleJammerIndex][jammer2Index][mp][p2].getCost() - JAPDomain[mp];

                    compositeJammerCostInfo = result[jammer1Index][jammer2Index][p1][p2];
                    if (compositeCost < compositeJammerCostInfo.getCost()){
                        compositeJammerCostInfo.setCost(compositeCost);
                        compositeJammerCostInfo.setMiddleJAPIndex(mp);
                    }
                }
            }
        }
    }

    private int[] generatePowerOfTwoIndices(int number){
        ArrayList<Integer> powerOfTwoIndices = new ArrayList<>();

        while (0 < number){
            powerOfTwoIndices.add(number);
            number -= (number & -number);
        }

        powerOfTwoIndices.sort((o1, o2) -> o1 - o2);

        int[] indicesArray = new int[powerOfTwoIndices.size()];
        for (int i = 0; i < indicesArray.length; ++i){
            indicesArray[i] = powerOfTwoIndices.get(i);
        }

        return indicesArray;
    }

    private void assignJAPToJammersFromResult() throws UnAssignableJammerException {
        int JAPDomainSize = JAPDomain.length;
        JammerCostInfo minJammerCostInfo = result[0][0][0][0];
        int index = 0;
        JammerCostInfo currentJammerCostInfo;

        for (int i = 1; i < JAPDomainSize; ++i){
            currentJammerCostInfo = result[0][0][i][i];
            if (currentJammerCostInfo.getCost() < minJammerCostInfo.getCost()){
                minJammerCostInfo = currentJammerCostInfo;
                index = i;
            }
        }

        if (minJammerCostInfo.getCost() == Float.MAX_VALUE)
            throw new UnAssignableJammerException();

        jammers[0].setJAP(JAPDomain[index]);
        assignJAPToJammerSubsetFromResult(0, jammers.length, index, index);
    }

    private void assignJAPToJammerSubsetFromResult(int jammer1Index, int jammer2Index, int JAP1Index, int JAP2Index){
        int powerOfTwoSubtractionRemainder = jammer2Index - (jammer2Index & -jammer2Index);
        int middleJammerIndex = (0 != jammer1Index || 0 == powerOfTwoSubtractionRemainder) ? (jammer1Index + jammer2Index) / 2 : powerOfTwoSubtractionRemainder;
        int middleJAPIndex = result[jammer1Index][jammer2Index % jammers.length][JAP1Index][JAP2Index].getMiddleJAPIndex();

        jammers[middleJammerIndex].setJAP(JAPDomain[middleJAPIndex]);

        if (1 < middleJammerIndex - jammer1Index)
            assignJAPToJammerSubsetFromResult(jammer1Index, middleJammerIndex, JAP1Index, middleJAPIndex);

        if (1 < jammer2Index - middleJammerIndex)
            assignJAPToJammerSubsetFromResult(middleJammerIndex, jammer2Index, middleJAPIndex, JAP2Index);
    }
}

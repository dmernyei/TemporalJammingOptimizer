package temporaljammingoptimizer.logic.entities;

import temporaljammingoptimizer.logic.exceptions.IncorrectMapException;
import temporaljammingoptimizer.logic.JammerDistanceInfo;
import temporaljammingoptimizer.logic.geometry.Polygon;
import temporaljammingoptimizer.logic.geometry.Vector2;
import temporaljammingoptimizer.utilities.MathUtilities;
import temporaljammingoptimizer.utilities.MessageProvider;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Daniel Mernyei
 */
public class WitnessPoint extends Entity {

    private float smallestDistanceFromStorage = -1;
    private Jammer oneNearestJammer;

    public WitnessPoint(Vector2 position){
        super(position);
    }

    public boolean isStoragePoint() {
        return -1 == smallestDistanceFromStorage;
    }

    public float getSmallestDistanceFromStorage() {
        return smallestDistanceFromStorage;
    }

    public boolean isOneNearestJammer(Jammer jammer){
        return oneNearestJammer.equals(jammer);
    }

    public void setClosestStoragePoint(Polygon storage){
        int vertexCount = storage.getVertexCount();
        Vector2 currentClosestStoragePoint = MathUtilities.closestPointInSegmentToOuterPoint(storage.getVertexAt(0), storage.getVertexAt(1), position);
        smallestDistanceFromStorage = Vector2.distance(currentClosestStoragePoint, position);
        float currentDistance;

        for (int i = 1; i < vertexCount; ++i){
            currentClosestStoragePoint = MathUtilities.closestPointInSegmentToOuterPoint(storage.getVertexAt(i), storage.getVertexAt((i + 1) % vertexCount), position);
            currentDistance = Vector2.distance(currentClosestStoragePoint, position);

            if (currentDistance < smallestDistanceFromStorage){
                smallestDistanceFromStorage = currentDistance;
            }
        }
    }

    public void registerToNearestJammers(ArrayList<Jammer> jammers, boolean twoNearest) throws IncorrectMapException {
        int jammerCount = jammers.size();
        JammerDistanceInfo[] jammerDistanceInfos = new JammerDistanceInfo[jammerCount];

        for (int i = 0; i < jammerCount; ++i){
            jammerDistanceInfos[i] = new JammerDistanceInfo(i, Vector2.distance(jammers.get(i).getPosition(), position));
        }
        Arrays.sort(jammerDistanceInfos);

        int nearbyJammerIndex1 = jammerDistanceInfos[0].getIndex();
        oneNearestJammer = jammers.get(nearbyJammerIndex1);
        oneNearestJammer.addNearbyWitnessPoint(this);

        if (twoNearest && jammerCount > 1){
            int nearbyJammerIndex2 = jammerDistanceInfos[1].getIndex();
            int indexDifference = Math.abs(nearbyJammerIndex1 - nearbyJammerIndex2);

            if (1 != indexDifference && (jammerCount - 1) != indexDifference)
                throw new IncorrectMapException(MessageProvider.getMessage("twoNearestJammersAreNotAdjacent"));

            jammers.get(nearbyJammerIndex2).addNearbyWitnessPoint(this);
        }
    }

    @Override
    public String toString(){
        return super.toString() + " storage point: " + isStoragePoint();
    }
}

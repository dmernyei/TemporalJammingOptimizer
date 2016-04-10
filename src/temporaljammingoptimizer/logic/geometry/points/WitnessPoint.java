package temporaljammingoptimizer.logic.geometry.points;

import temporaljammingoptimizer.logic.exceptions.IncorrectMapException;
import temporaljammingoptimizer.utils.JammerDistanceInfo;
import temporaljammingoptimizer.utils.MathUtilities;
import temporaljammingoptimizer.utils.MessageProvider;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Daniel Mernyei
 */
public class WitnessPoint extends Point {

    private boolean isInsideControlledRegion;

    public WitnessPoint(int x, int y) {
        super(x, y);
        index = ++Point.indexGenerator;
    }

    public boolean isInsideControlledRegion() {
        return isInsideControlledRegion;
    }

    public void setInsideControlledRegion(boolean insideControlledRegion) {
        isInsideControlledRegion = insideControlledRegion;
    }

    public void registerToNearestJammers(ArrayList<Jammer> jammers, boolean twoNearest) throws IncorrectMapException {
        int jammerCount = jammers.size();
        JammerDistanceInfo[] jammerDistanceInfos = new JammerDistanceInfo[jammerCount];

        for (int i = 0; i < jammerCount; ++i){
            jammerDistanceInfos[i] = new JammerDistanceInfo(i, MathUtilities.computeDistance(jammers.get(i), this));
        }
        Arrays.sort(jammerDistanceInfos);

        int nearbyJammerIndex1 = jammerDistanceInfos[0].getIndex();
        jammers.get(nearbyJammerIndex1).addNearbyWitnessPoint(this);

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
        return super.toString() + " storage point: " + isInsideControlledRegion;
    }
}

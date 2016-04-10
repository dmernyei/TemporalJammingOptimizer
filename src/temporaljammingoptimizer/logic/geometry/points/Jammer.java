package temporaljammingoptimizer.logic.geometry.points;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class Jammer extends Point {

    private float activityProbability = -1;
    private ArrayList<WitnessPoint> nearbyStoragePoints;
    private ArrayList<WitnessPoint> nearbyEavesdropperPoints;

    public Jammer(int x, int y) {
        super(x, y);
        index = ++Point.indexGenerator;
        nearbyStoragePoints = new ArrayList<>();
        nearbyEavesdropperPoints = new ArrayList<>();
    }

    public float getActivityProbability() {
        return activityProbability;
    }

    public void setActivityProbability(float activityProbability) {
        this.activityProbability = activityProbability;
    }

    public int getNearbyStoragePointCount(){
        return nearbyStoragePoints.size();
    }

    public WitnessPoint getNearbyStoragePointAt(int index){
        return nearbyStoragePoints.get(index);
    }

    public int getNearbyEavesdropperPointCount(){
        return nearbyEavesdropperPoints.size();
    }

    public WitnessPoint getNearbyEavesdropperPointAt(int index){
        return nearbyEavesdropperPoints.get(index);
    }

    public void addNearbyWitnessPoint(WitnessPoint witnessPoint){
        if (witnessPoint.isInsideControlledRegion())
            nearbyStoragePoints.add(witnessPoint);
        else
            nearbyEavesdropperPoints.add(witnessPoint);
    }

    public boolean hasStorageAndEavesdropperPointNearby(){
        return 0 < nearbyEavesdropperPoints.size() && 0 < nearbyStoragePoints.size();
    }

    @Override
    public String toString(){
        String coordinates = super.toString();
        String nearbySP = "Nearby storage points: ";
        String nearbyEP = "Nearby eavesdropper points: ";

        for (Point p : nearbyStoragePoints){
            nearbySP += "[" + p.getX() + " ," + p.getY() + "] ";
        }

        for (Point p : nearbyEavesdropperPoints){
            nearbyEP += "[" + p.getX() + " ," + p.getY() + "] ";
        }

        return coordinates + "\n" + nearbySP + "\n" + nearbyEP;
    }
}

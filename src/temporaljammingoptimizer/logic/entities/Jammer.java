package temporaljammingoptimizer.logic.entities;

import temporaljammingoptimizer.logic.geometry.Vector2;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class Jammer extends Entity {

    private float activityProbability = -1;
    private ArrayList<WitnessPoint> nearbyStoragePoints;
    private ArrayList<WitnessPoint> nearbyEavesdropperPoints;

    public Jammer(Vector2 position){
        super(position);
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
        if (witnessPoint.isStoragePoint())
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

        for (WitnessPoint witnessPoint : nearbyStoragePoints){
            nearbySP += witnessPoint.getPosition().toString() + " ";
        }

        for (WitnessPoint witnessPoint : nearbyEavesdropperPoints){
            nearbyEP += witnessPoint.getPosition().toString() + " ";
        }

        return coordinates + "\n" + nearbySP + "\n" + nearbyEP;
    }
}

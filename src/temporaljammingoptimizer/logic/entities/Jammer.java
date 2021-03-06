package temporaljammingoptimizer.logic.entities;

import temporaljammingoptimizer.logic.geometry.Vector2;

import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class Jammer extends Entity {

    private float JAP = -1;
    private ArrayList<WitnessPoint> nearbyWitnessPoints;

    public Jammer(Vector2 position){
        super(position);
        nearbyWitnessPoints = new ArrayList<>();
    }

    public float getJAP() {
        return JAP;
    }

    public void setJAP(float JAP) {
        this.JAP = JAP;
    }

    public void resetJAP(){
        JAP = -1;
    }

    public boolean isJAPSet(){
        return -1 != JAP;
    }

    public int getNearbyWitnessPointCount(){
        return nearbyWitnessPoints.size();
    }

    public WitnessPoint getNearbyWitnessPointAt(int index){
        return nearbyWitnessPoints.get(index);
    }

    void addNearbyWitnessPoint(WitnessPoint witnessPoint){
        nearbyWitnessPoints.add(witnessPoint);
    }

    public boolean hasStorageAndEavesdropperPointNearby(){
        boolean hasStoragePointNearby = false;
        boolean hasEavesdropperPointNearby = false;

        for (WitnessPoint witnessPoint : nearbyWitnessPoints){
            if (witnessPoint.isStoragePoint())
                hasStoragePointNearby = true;
            else
                hasEavesdropperPointNearby = true;

            if (hasStoragePointNearby && hasEavesdropperPointNearby)
                return true;
        }
        return false;
    }

    private boolean isWitnessPointNearby(WitnessPoint witnessPoint){
        return nearbyWitnessPoints.contains(witnessPoint);
    }

    public static ArrayList<WitnessPoint> intersectionOfNearbyWitnessPoints(Jammer jammer1, Jammer jammer2){
        ArrayList<WitnessPoint> intersection = new ArrayList<>();
        WitnessPoint witnessPoint;

        for (int i = 0; i < jammer1.getNearbyWitnessPointCount(); ++i){
            witnessPoint = jammer1.getNearbyWitnessPointAt(i);
            if (jammer2.isWitnessPointNearby(witnessPoint))
                intersection.add(witnessPoint);
        }

        return intersection;
    }

    @Override
    public String toString(){
        String coordinates = super.toString();

        ArrayList<WitnessPoint> storagePoints = new ArrayList<>();
        ArrayList<WitnessPoint> eavesdropperPoints = new ArrayList<>();

        for (WitnessPoint witnessPoint : nearbyWitnessPoints){
            if (witnessPoint.isStoragePoint())
                storagePoints.add(witnessPoint);
            else
                eavesdropperPoints.add(witnessPoint);
        }

        String nearbySP = "Nearby storage points: ";
        String nearbyEP = "Nearby eavesdropper points: ";

        for (WitnessPoint witnessPoint : storagePoints){
            nearbySP += witnessPoint.getPosition().toString() + " ";
        }

        for (WitnessPoint witnessPoint : eavesdropperPoints){
            nearbyEP += witnessPoint.getPosition().toString() + " ";
        }

        return coordinates + "\n" + nearbySP + "\n" + nearbyEP;
    }
}

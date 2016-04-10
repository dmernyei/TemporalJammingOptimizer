package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.logic.exceptions.IncorrectMapException;
import temporaljammingoptimizer.logic.geometry.Polygon;
import temporaljammingoptimizer.logic.geometry.Size;
import temporaljammingoptimizer.logic.geometry.points.Jammer;
import temporaljammingoptimizer.logic.geometry.points.Point;
import temporaljammingoptimizer.logic.geometry.points.WitnessPoint;
import temporaljammingoptimizer.utils.MessageProvider;
import temporaljammingoptimizer.utils.StringUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Daniel Mernyei
 */
public class Optimizer {

    private final Size mapSize;

    private AlgorithmType algorithmType;

    private float lowerTBEPThreshold;
    private float upperTBEPThreshold;
    private float propagationFactor1;
    private float propagationFactor2;
    private float jammingFactor1;
    private float jammingFactor2;
    private boolean stepByStep;

    private Polygon<Point> controlledRegion;
    private Polygon<Point> storage;
    private ArrayList<Jammer> jammers;
    private ArrayList<WitnessPoint> witnessPoints;

    public Optimizer(Size mapSize) {
        this.mapSize = mapSize;

        controlledRegion = new Polygon<>();
        storage = new Polygon<>();
        jammers = new ArrayList<>();
        witnessPoints = new ArrayList<>();
    }

    public AlgorithmType getAlgorithmType(){
        return algorithmType;
    }

    public float getLowerTBEPThreshold() {
        return lowerTBEPThreshold;
    }

    public void setLowerTBEPThreshold(float lowerTBEPThreshold) {
        this.lowerTBEPThreshold = lowerTBEPThreshold;
    }

    public float getUpperTBEPThreshold() {
        return upperTBEPThreshold;
    }

    public void setUpperTBEPThreshold(float upperTBEPThreshold) {
        this.upperTBEPThreshold = upperTBEPThreshold;
    }

    public float getPropagationFactor1() {
        return propagationFactor1;
    }

    public void setPropagationFactor1(float propagationFactor1) {
        this.propagationFactor1 = propagationFactor1;
    }

    public float getPropagationFactor2() {
        return propagationFactor2;
    }

    public void setPropagationFactor2(float propagationFactor2) {
        this.propagationFactor2 = propagationFactor2;
    }

    public float getJammingFactor1() {
        return jammingFactor1;
    }

    public void setJammingFactor1(float jammingFactor1) {
        this.jammingFactor1 = jammingFactor1;
    }

    public float getJammingFactor2() {
        return jammingFactor2;
    }

    public void setJammingFactor2(float jammingFactor2) {
        this.jammingFactor2 = jammingFactor2;
    }

    public boolean isStepByStep() {
        return stepByStep;
    }

    public void setStepByStep(boolean stepByStep) {
        this.stepByStep = stepByStep;
    }

    public Polygon<Point> getControlledRegion() {
        return controlledRegion;
    }

    public Polygon<Point> getStorage() {
        return storage;
    }

    public int getJammerCount(){
        return jammers.size();
    }

    public Jammer getJammerAt(int index){
        return jammers.get(index);
    }

    public int getWitnessPointCount(){
        return witnessPoints.size();
    }

    public WitnessPoint getWitnessPointAt(int index){
        return witnessPoints.get(index);
    }

    public void loadMap(String fileName) throws FileNotFoundException, IncorrectMapException {
        clearMapData();

        Scanner sc = new Scanner(new File(fileName));
        String line;
        int lineCount = 0;
        int readBlockCount = 0;
        boolean previousLineWasEmpty = false;

        // Loading points
        while (sc.hasNextLine() && 3 >= readBlockCount) {
            line = sc.nextLine();
            ++lineCount;

            if (line.startsWith("#"))
                continue;

            if (line.isEmpty()) {
                if (!previousLineWasEmpty){
                    ++readBlockCount;
                    previousLineWasEmpty = true;
                }
                continue;
            }
            else{
                previousLineWasEmpty = false;
            }

            processLine(line, readBlockCount, lineCount);
        }
        sc.close();

        if (3 > readBlockCount){
            throw new IncorrectMapException(MessageProvider.getMessage("incorrectMapStructure"));
        }

        // Filtering out duplicate points
        filterDuplicatePoints(controlledRegion.getVertices());
        filterDuplicatePoints(storage.getVertices());
        filterDuplicatePoints(jammers);
        filterDuplicatePoints(witnessPoints);

        // Checking polygons
        checkPolygons();

        // Checking jammers, witness points, and their relations
        checkJammers();
        determineAlgorithmModelType();
        determineWitnessPointTypes();
        assignWitnessPointsToJammers();
        checkJammerWitnessPointRelations();
    }

    private void clearMapData(){
        controlledRegion.clear();
        storage.clear();
        jammers.clear();
        witnessPoints.clear();
        Point.resetIndexGenerator();
    }

    private void processLine(String line, int readBlockCount, int lineCount) throws IncorrectMapException {
        String[] stringNumbers;
        Point point;

        // Checking the point syntactically and semantically
        stringNumbers = line.split(",");
        if (2 != stringNumbers.length){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("incorrectDimensionCount"), lineCount));
        }

        try {
            point = new Point(Integer.parseInt(stringNumbers[0].trim()), Integer.parseInt(stringNumbers[1].trim()));
        }
        catch (NumberFormatException ex){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("incorrectDimensionType"), lineCount));
        }

        if (!mapSize.isPointInsideArea(point)){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("pointOutOfMap"), lineCount));
        }

        // Assigning the point to the proper map object
        switch (readBlockCount) {
            case 0:
                controlledRegion.addVertex(point);
                break;
            case 1:
                storage.addVertex(point);
                break;
            case 2:
                jammers.add(new Jammer(point.getX(), point.getY()));
                break;
            case 3:
                witnessPoints.add(new WitnessPoint(point.getX(), point.getY()));
                break;
        }
    }

    private void checkPolygons() throws IncorrectMapException {
        // Are polygons correctly shaped?
        if (!controlledRegion.isConvexAndHasArea())
            throw new IncorrectMapException(MessageProvider.getMessage("controlledRegionIsConcaveOrHasNoArea"));

        if (!storage.isConvexAndHasArea())
            throw new IncorrectMapException(MessageProvider.getMessage("storageIsConcaveOrHasNoArea"));

        // Is the storage inside the controlled region?
        int vertexCount = storage.getVertexCount();
        for (int i = 0; i < vertexCount; ++i){
            if (!controlledRegion.isPointInsidePolygon(storage.getVertexAt(i)))
                throw new IncorrectMapException(MessageProvider.getMessage("storageNotInsideControlledRegion"));
        }
    }

    private void filterDuplicatePoints(ArrayList<? extends Point> pointArray){
        HashSet<Point> pointSet = new HashSet<>();
        Point point;

        for (int i = 0; i < pointArray.size(); ++i){
            point = pointArray.get(i);
            if (pointSet.contains(point)){
                pointArray.remove(i);
                --i;
            }
            else{
                pointSet.add(point);
            }
        }
    }

    private void checkJammers() throws IncorrectMapException {
        // Checking jammer positions
        for (Jammer jammer : jammers) {
            if (!controlledRegion.isPointInsidePolygon(jammer))
                throw new IncorrectMapException(MessageProvider.getMessage("jammerNotInsideControlledRegion"));

            if (storage.isPointInsidePolygon(jammer))
                throw new IncorrectMapException(MessageProvider.getMessage("jammerInsideStorage"));
        }
    }

    private void determineAlgorithmModelType(){
        Polygon<Jammer> jammerPolygon = new Polygon<>(jammers);
        algorithmType = jammerPolygon.isConvexAndHasArea() ? AlgorithmType.TWO_NEAREST_JAMMER : AlgorithmType.NEAREST_JAMMER;
    }

    private void determineWitnessPointTypes(){
        for (WitnessPoint witnessPoint : witnessPoints)
            witnessPoint.setInsideControlledRegion(controlledRegion.isPointInsidePolygon(witnessPoint));
    }

    private void assignWitnessPointsToJammers() throws IncorrectMapException {
        boolean twoNearest = AlgorithmType.TWO_NEAREST_JAMMER == algorithmType;
        for (WitnessPoint witnessPoint : witnessPoints){
            witnessPoint.registerToNearestJammers(jammers, twoNearest);
        }
    }

    private void checkJammerWitnessPointRelations() throws IncorrectMapException {
        for (Jammer jammer : jammers){
            if (!jammer.hasStorageAndEavesdropperPointNearby())
                throw new IncorrectMapException(MessageProvider.getMessage("jammerTooFarFromStorageOrEavesdropperPoints"));
        }
    }
}

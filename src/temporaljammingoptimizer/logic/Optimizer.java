package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.logic.exceptions.IncorrectMapException;
import temporaljammingoptimizer.logic.geometry.Polygon;
import temporaljammingoptimizer.logic.geometry.Size;
import temporaljammingoptimizer.logic.entities.Entity;
import temporaljammingoptimizer.logic.entities.Jammer;
import temporaljammingoptimizer.logic.entities.WitnessPoint;
import temporaljammingoptimizer.logic.geometry.Vector2;
import temporaljammingoptimizer.utils.MessageProvider;
import temporaljammingoptimizer.utils.StringUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Daniel Mernyei
 */
public class Optimizer {

    private final Size mapSize;

    private AlgorithmType algorithmType;
    private boolean isOptimizationRunning;
    private boolean isResultComputed;

    private float lowerTBEPThreshold;
    private float upperTBEPThreshold;
    private float propagationFactor1;
    private float propagationFactor2;
    private float jammingFactor1;
    private float jammingFactor2;
    private boolean stepByStep;

    private Polygon controlledRegion;
    private Polygon storage;
    private ArrayList<Jammer> jammers;
    private ArrayList<WitnessPoint> witnessPoints;

    public Optimizer(Size mapSize) {
        this.mapSize = mapSize;

        controlledRegion = new Polygon();
        storage = new Polygon();
        jammers = new ArrayList<>();
        witnessPoints = new ArrayList<>();
        algorithmType = AlgorithmType.NOT_APPLICABLE;
    }

    public AlgorithmType getAlgorithmType(){
        return algorithmType;
    }

    public boolean isOptimizationRunning() {
        return isOptimizationRunning;
    }

    public boolean isResultComputed() {
        return isResultComputed;
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

    public Polygon getControlledRegion() {
        return controlledRegion;
    }

    public Polygon getStorage() {
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

    public void optimize(){

    }

    public void step(){

    }

    public void loadMap(String fileName) throws FileNotFoundException, IncorrectMapException {
        clearData(true);

        Scanner sc = new Scanner(new File(fileName));
        String line;
        int lineCount = 0;
        int readBlockCount = 0;
        boolean previousLineWasEmpty = false;

        // Loading positions
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

        // Filtering out duplicate entities
        filterDuplicateElements(controlledRegion.getVertices());
        filterDuplicateElements(storage.getVertices());
        filterDuplicateElements(jammers);
        filterDuplicateElements(witnessPoints);

        // Checking polygons
        checkPolygons();

        // Checking jammers, witness entities, and their relations
        checkJammers();
        determineAlgorithmModelType();
        setWitnessPointTypes();
        assignWitnessPointsToJammers();
        checkJammerWitnessPointRelations();
    }

    private void clearData(boolean withMapData){
        if (withMapData) {
            controlledRegion.clear();
            storage.clear();
            jammers.clear();
            witnessPoints.clear();
            Entity.resetIdGenerator();
        }

        // todo: clear algorithm data

        isOptimizationRunning = false;
        isResultComputed = false;
    }

    private void processLine(String line, int readBlockCount, int lineCount) throws IncorrectMapException {
        String[] stringNumbers;
        Vector2 position;

        // Checking the position syntactically and semantically
        stringNumbers = line.split(",");
        if (2 != stringNumbers.length){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("incorrectDimensionCount"), lineCount));
        }

        try {
            position = new Vector2(Integer.parseInt(stringNumbers[0].trim()), Integer.parseInt(stringNumbers[1].trim()));
        }
        catch (NumberFormatException ex){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("incorrectDimensionType"), lineCount));
        }

        if (!mapSize.isPositionInsideArea(position)){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("positionOutOfMap"), lineCount));
        }

        // Assigning the position to the proper map object
        switch (readBlockCount) {
            case 0:
                controlledRegion.addVertex(position);
                break;
            case 1:
                storage.addVertex(position);
                break;
            case 2:
                jammers.add(new Jammer(position));
                break;
            case 3:
                witnessPoints.add(new WitnessPoint(position));
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
            if (!controlledRegion.isPositionInsidePolygon(storage.getVertexAt(i)))
                throw new IncorrectMapException(MessageProvider.getMessage("storageNotInsideControlledRegion"));
        }
    }

    private <T> void filterDuplicateElements(ArrayList<T> elements){
        HashSet<T> set = new HashSet<>();
        T element;

        for (int i = 0; i < elements.size(); ++i){
            element = elements.get(i);
            if (set.contains(element)){
                elements.remove(i);
                --i;
            }
            else{
                set.add(element);
            }
        }
    }

    private void checkJammers() throws IncorrectMapException {
        // Checking jammer positions
        for (Jammer jammer : jammers) {
            if (!controlledRegion.isPositionInsidePolygon(jammer.getPosition()))
                throw new IncorrectMapException(MessageProvider.getMessage("jammerNotInsideControlledRegion"));

            if (storage.isPositionInsidePolygon(jammer.getPosition()))
                throw new IncorrectMapException(MessageProvider.getMessage("jammerInsideStorage"));
        }
    }

    private void determineAlgorithmModelType(){

        ArrayList<Vector2> jammerPositions = jammers.stream()
                .map(Entity::getPosition)
                .collect(Collectors.toCollection(ArrayList<Vector2>::new));

        Polygon jammerPolygon = new Polygon(jammerPositions);
        algorithmType = jammerPolygon.isConvexAndHasArea() ? AlgorithmType.TWO_NEAREST_JAMMER : AlgorithmType.NEAREST_JAMMER;
    }

    private void setWitnessPointTypes(){
        witnessPoints.stream().filter(witnessPoint -> !controlledRegion.isPositionInsidePolygon(witnessPoint.getPosition())).forEach(witnessPoint -> {
            witnessPoint.setClosestStoragePoint(storage);
        });
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

    public void saveConfiguration(String filePath) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath + ".tcfg"));
        oos.writeObject(lowerTBEPThreshold);
        oos.writeObject(upperTBEPThreshold);
        oos.writeObject(propagationFactor1);
        oos.writeObject(propagationFactor2);
        oos.writeObject(jammingFactor1);
        oos.writeObject(jammingFactor2);
        oos.writeBoolean(stepByStep);
        oos.close();
    }

    public void loadConfiguration(File configFile) throws IOException, ClassNotFoundException {
        clearData(false);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFile));
        lowerTBEPThreshold = ois.readInt();
        upperTBEPThreshold = ois.readInt();
        propagationFactor1 = ois.readInt();
        propagationFactor2 = ois.readInt();
        jammingFactor1 = ois.readInt();
        jammingFactor2 = ois.readInt();
        stepByStep = ois.readBoolean();
        ois.close();
    }

    public void saveResult(String filePath) throws FileNotFoundException {
        if (!isResultComputed)
            return;

        PrintWriter pw = new PrintWriter(new File(filePath));

        pw.println(MessageProvider.getMessage("configurationInformation"));
        pw.println(MessageProvider.getMessage("lowerTBEPThreshold") + "\t" + lowerTBEPThreshold);
        pw.println(MessageProvider.getMessage("upperTBEPThreshold") + "\t" + upperTBEPThreshold);
        pw.println(MessageProvider.getMessage("propagationFactor1") + "\t" + propagationFactor1);
        pw.println(MessageProvider.getMessage("propagationFactor2") + "\t" + propagationFactor2);
        pw.println(MessageProvider.getMessage("jammingFactor1") + "\t" + jammingFactor1);
        pw.println(MessageProvider.getMessage("jammingFactor2") + "\t" + jammingFactor2);
        pw.println(MessageProvider.getMessage("showStepByStepWithColon") + "\t" + stepByStep);
        pw.println();
        pw.println(MessageProvider.getMessage("algorithminformationWithColon"));
        // todo: write algorithm information

        pw.close();
        // todo: is flush needed?
    }
}

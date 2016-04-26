package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.logic.algorithms.onenearestjammeralgorithm.OneNearestJammerAlgorithm;
import temporaljammingoptimizer.logic.algorithms.onenearestjammeralgorithm.WitnessPointJAPBoundInfo;
import temporaljammingoptimizer.logic.algorithms.twonearestjammeralgorithm.TwoNearestJammerAlgorithm;
import temporaljammingoptimizer.logic.exceptions.IncorrectMapException;
import temporaljammingoptimizer.logic.exceptions.UnAssignableJammerException;
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

    private Configuration configuration;
    private AlgorithmType algorithmType;
    private boolean isOptimizationRunning;
    private boolean isResultComputed;

    private OneNearestJammerAlgorithm oneNearestJammerAlgorithm;
    private TwoNearestJammerAlgorithm twoNearestJammerAlgorithm;

    private Polygon controlledRegion;
    private Polygon storage;
    private ArrayList<Jammer> jammers;
    private ArrayList<WitnessPoint> witnessPoints;
    private float totalJAPSum = -1;

    public Optimizer(Size mapSize) {
        this.mapSize = mapSize;

        configuration = new Configuration(0.07f, 0.16f, 0.1f, 2, 3, 2, false);
        controlledRegion = new Polygon();
        storage = new Polygon();
        jammers = new ArrayList<>();
        witnessPoints = new ArrayList<>();
        algorithmType = AlgorithmType.NOT_APPLICABLE;

        oneNearestJammerAlgorithm = new OneNearestJammerAlgorithm();
        twoNearestJammerAlgorithm = new TwoNearestJammerAlgorithm();
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

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration){
        this.configuration = configuration;
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

    public ArrayList<WitnessPointJAPBoundInfo> getStoragePointJAPBoundInfos(){
        return oneNearestJammerAlgorithm.getStoragePointJAPBoundInfos();
    }

    public ArrayList<WitnessPointJAPBoundInfo> getEavesdropperPointJAPBoundInfos(){
        return oneNearestJammerAlgorithm.getEavesdropperPointJAPBoundInfos();
    }

    public float getMinStoragePointJAPBound(){
        return oneNearestJammerAlgorithm.getMinStoragePointJAPBound();
    }

    public float getMaxEavesdropperPointJAPBound(){
        return oneNearestJammerAlgorithm.getMaxEavesdropperPointJAPBound();
    }
    
    public float getTotalJAPSum() {
        return totalJAPSum;
    }

    public void optimize() throws UnAssignableJammerException {
        if (isOptimizationRunning)
            return;

        isOptimizationRunning = true;
        isResultComputed = false;

        if (AlgorithmType.NEAREST_JAMMER == algorithmType){
            oneNearestJammerAlgorithm.reset();
            oneNearestJammerAlgorithm.setConfiguration(configuration);

            if (!configuration.isStepByStep()){
                while (oneNearestJammerAlgorithm.isFinished()){
                    step();
                }
            }
        }
        else{
            twoNearestJammerAlgorithm.reset();
            twoNearestJammerAlgorithm.setConfiguration(configuration);
            twoNearestJammerAlgorithm.computeActivityProbabilities();

            isOptimizationRunning = false;
            isResultComputed = true;
            computeTotalJAPSum();
        }
    }

    public void step() throws UnAssignableJammerException {
        if (!isOptimizationRunning || isResultComputed || !configuration.isStepByStep())
            return;

        oneNearestJammerAlgorithm.computeJAPForNextJammer();

        if (oneNearestJammerAlgorithm.isFinished()){
            isOptimizationRunning = false;
            isResultComputed = true;
            computeTotalJAPSum();
        }
    }

    private void computeTotalJAPSum(){
        if (isResultComputed){
            totalJAPSum = 0;
            for (Jammer jammer : jammers)
                totalJAPSum += jammer.getActivityProbability();
        }
        else{
            totalJAPSum = -1;
        }
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

        // Assigning jammers to algorithms
        Jammer[] jammerArray = new Jammer[jammers.size()];
        for (int i = 0; i < jammerArray.length; ++i)
            jammerArray[i] = jammers.get(i);

        if (AlgorithmType.NEAREST_JAMMER == algorithmType)
            oneNearestJammerAlgorithm.setJammers(jammerArray);
        else
            twoNearestJammerAlgorithm.setJammers(jammerArray);
    }

    private void clearData(boolean withMapData){
        if (withMapData) {
            controlledRegion.clear();
            storage.clear();
            jammers.clear();
            witnessPoints.clear();
            Entity.resetIdGenerator();
        }
        else{
            jammers.forEach(Jammer::resetActivityProbability);
        }

        oneNearestJammerAlgorithm.reset();
        twoNearestJammerAlgorithm.reset();

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
        oos.writeObject(configuration);
        oos.close();
    }

    public void loadConfiguration(File configFile) throws IOException, ClassNotFoundException {
        clearData(false);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(configFile));
        configuration = (Configuration) ois.readObject();
        ois.close();
    }

    public void saveResult(String filePath) throws FileNotFoundException {
        if (!isResultComputed)
            return;

        PrintWriter pw = new PrintWriter(new File(filePath));

        pw.println(MessageProvider.getMessage("configurationInformation"));
        pw.println(configuration);
        pw.println();

        pw.println(MessageProvider.getMessage("algorithminformationWithColon"));
        pw.println(MessageProvider.getMessage("model") + "\t" + algorithmType.toString());
        pw.println(MessageProvider.getMessage("JAPResultsWithColon"));
        for (Jammer jammer : jammers){
            pw.println(jammer.getId() + "\t" + jammer.getActivityProbability());
        }
        pw.println(MessageProvider.getMessage("totalJAPSum") + " " + totalJAPSum);

        pw.close();
        // todo: is flush needed?
    }
}

package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.logic.exceptions.IncorrectMapException;
import temporaljammingoptimizer.logic.points.Jammer;
import temporaljammingoptimizer.logic.points.Point;
import temporaljammingoptimizer.logic.points.WitnessPoint;
import temporaljammingoptimizer.utils.MessageProvider;
import temporaljammingoptimizer.utils.StringUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Daniel Mernyei
 */
public class TemporalJammingOptimizer {

    private final Size mapSize;

    private AlgorithmType algorithmType;

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

    public TemporalJammingOptimizer(Size mapSize) {
        this.mapSize = mapSize;

        controlledRegion = new Polygon();
        storage = new Polygon();
        jammers = new ArrayList<>();
        witnessPoints = new ArrayList<>();
    }

    public AlgorithmType getAlgorithmType(){
        return algorithmType;
    }

    public void loadMap(String fileName) throws FileNotFoundException, IncorrectMapException {
        clearMapData();

        Scanner sc = new Scanner(new File(fileName));
        String line;
        String[] stringNumbers;
        int lineCount = 0;
        int readBlockCount = 0;
        Point currentPoint;

        // Loading points
        while (sc.hasNextLine() && 3 >= readBlockCount) {
            line = sc.nextLine();
            ++lineCount;

            if (!line.startsWith("#"))
                continue;

            if (line.isEmpty()) {
                ++readBlockCount;
                continue;
            }

            processLine(line, readBlockCount, lineCount);
        }
        sc.close();

        // Checking polygons
        checkPolygons();

        // Filtering out duplicate points
        filterDuplicatePoints();

        // Checking jammers and witness points
        checkJammers();
        checkWitnessPoints();
    }

    private void clearMapData(){
        controlledRegion.clear();
        storage.clear();
        jammers.clear();
        witnessPoints.clear();
    }

    private void processLine(String line, int readBlockCount, int lineCount) throws IncorrectMapException {
        String[] stringNumbers;
        Point Point;

        // Checking the point syntactically and semantically
        stringNumbers = line.split(",");
        if (2 != stringNumbers.length){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("incorrectDimensionCount"), lineCount));
        }

        try {
            Point = new Point(Integer.parseInt(stringNumbers[0]), Integer.parseInt(stringNumbers[1]));
        }
        catch (NumberFormatException ex){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("incorrectDimensionType"), lineCount));
        }

        if (!mapSize.isPointInsideArea(Point)){
            throw new IncorrectMapException(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("pointOutOfMap"), lineCount));
        }

        // Assigning the point to the proper map object
        switch (readBlockCount) {
            case 0:
                controlledRegion.addVertex(Point);
                break;
            case 1:
                storage.addVertex(Point);
                break;
            case 2:
                jammers.add(new Jammer(Point.getX(), Point.getY()));
                break;
            case 3:
                witnessPoints.add(new WitnessPoint(Point.getX(), Point.getY(), controlledRegion.isPointInsidePolygon(Point)));
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

    private void filterDuplicatePoints(){
        HashMap<Point, Integer> controlledRegionPointMap;
        // todo
    }

    private void checkJammers() throws IncorrectMapException {
        if (1 > jammers.size())
            throw new IncorrectMapException(MessageProvider.getMessage("missingJammers"));

        // Determining algorithm model type
        determineAlgorithmModelType();

        // Checking jammer positions
        for (Jammer jammer : jammers) {
            if (!controlledRegion.isPointInsidePolygon(jammer))
                throw new IncorrectMapException(MessageProvider.getMessage("jammerNotInsideControlledRegion"));

            if (storage.isPointInsidePolygon(jammer))
                throw new IncorrectMapException(MessageProvider.getMessage("jammerInsideStorage"));
        }
    }

    private void determineAlgorithmModelType(){
        ArrayList<Point> jammerPoints = jammers.stream()
                .map(jammer -> new Point(jammer.getX(), jammer.getY()))
                .collect(Collectors.toCollection(ArrayList<Point>::new));
        Polygon jammerPolygon = new Polygon(jammerPoints);
        algorithmType = jammerPolygon.isConvexAndHasArea() ? AlgorithmType.TWO_NEAREST_JAMMER : AlgorithmType.NEAREST_JAMMER;
    }

    private void checkWitnessPoints(){
        // todo
    }
}

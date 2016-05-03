package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.utilities.MessageProvider;

import java.io.Serializable;

/**
 * Created by Daniel Mernyei
 */
public class Configuration implements Serializable {
    private static final float minTBEPBound = 0;
    private static final float maxTBEPBound = 0.5f;
    private static final float TBEPBoundStep = 0.01f;
    private static final float minSignalDecayFactor = 0;
    private static final float maxSignalDecayFactor = 0.15f;
    private static final float signalDecayFactorStep = 0.01f;
    private static final float minJammingFactor = 0;
    private static final float maxJammingFactor = 10;
    private static final float jammingFactorStep = 0.1f;

    private float lowerETBEPBound;
    private float upperSTBEPBound;
    private float signalDecayFactor1;
    private float signalDecayFactor2;
    private float jammingFactor1;
    private float jammingFactor2;
    private boolean stepByStep;

    public Configuration(float lowerETBEPBound, float upperSTBEPBound, float signalDecayFactor1, float signalDecayFactor2, float jammingFactor1, float jammingFactor2, boolean stepByStep){
        this.lowerETBEPBound = lowerETBEPBound;
        this.upperSTBEPBound = upperSTBEPBound;
        this.signalDecayFactor1 = signalDecayFactor1;
        this.signalDecayFactor2 = signalDecayFactor2;
        this.jammingFactor1 = jammingFactor1;
        this.jammingFactor2 = jammingFactor2;
        this.stepByStep = stepByStep;
    }

    public static float getMinTBEPBound() {
        return minTBEPBound;
    }

    public static float getMaxTBEPBound() {
        return maxTBEPBound;
    }

    public static float getMinSignalDecayFactor() {
        return minSignalDecayFactor;
    }

    public static float getMaxSignalDecayFactor() {
        return maxSignalDecayFactor;
    }

    public static float getMinJammingFactor() {
        return minJammingFactor;
    }

    public static float getMaxJammingFactor() {
        return maxJammingFactor;
    }

    public static float getTBEPBoundStep() {
        return TBEPBoundStep;
    }

    public static float getSignalDecayFactorStep() {
        return signalDecayFactorStep;
    }

    public static float getJammingFactorStep() {
        return jammingFactorStep;
    }

    public float getLowerETBEPBound() {
        return lowerETBEPBound;
    }

    public float getUpperSTBEPBound() {
        return upperSTBEPBound;
    }

    public float getSignalDecayFactor1() {
        return signalDecayFactor1;
    }

    public float getSignalDecayFactor2() {
        return signalDecayFactor2;
    }

    public float getJammingFactor1() {
        return jammingFactor1;
    }

    public float getJammingFactor2() {
        return jammingFactor2;
    }

    public boolean isStepByStep() {
        return stepByStep;
    }

    @Override
    public String toString(){
        return MessageProvider.getMessage("lowerETBEPBound") + "\t" + lowerETBEPBound + "\n"
        + MessageProvider.getMessage("upperSTBEPBound") + "\t" + upperSTBEPBound + "\n"
        + MessageProvider.getMessage("signalDecayFactor1") + "\t" + signalDecayFactor1 + "\n"
        + MessageProvider.getMessage("signalDecayFactor2") + "\t" + signalDecayFactor2 + "\n"
        + MessageProvider.getMessage("jammingFactor1") + "\t" + (maxJammingFactor - jammingFactor1) + "\n"
        + MessageProvider.getMessage("jammingFactor2") + "\t" + (maxJammingFactor - jammingFactor2);
    }
}

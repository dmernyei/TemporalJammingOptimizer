package temporaljammingoptimizer.logic;

import temporaljammingoptimizer.utils.MessageProvider;

import java.io.Serializable;

/**
 * Created by Daniel Mernyei
 */
public class Configuration implements Serializable {
    private float lowerETBEPBound;
    private float upperSTBEPBound;
    private float propagationFactor1;
    private float propagationFactor2;
    private float jammingFactor1;
    private float jammingFactor2;
    private boolean stepByStep;

    public Configuration(float lowerETBEPBound, float upperSTBEPBound, float propagationFactor1, float propagationFactor2, float jammingFactor1, float jammingFactor2, boolean stepByStep){
        this.lowerETBEPBound = lowerETBEPBound;
        this.upperSTBEPBound = upperSTBEPBound;
        this.propagationFactor1 = propagationFactor1;
        this.propagationFactor2 = propagationFactor2;
        this.jammingFactor1 = jammingFactor1;
        this.jammingFactor2 = jammingFactor2;
        this.stepByStep = stepByStep;
    }

    public float getLowerETBEPBound() {
        return lowerETBEPBound;
    }

    public void setLowerETBEPBound(float lowerETBEPBound) {
        this.lowerETBEPBound = lowerETBEPBound;
    }

    public float getUpperSTBEPBound() {
        return upperSTBEPBound;
    }

    public void setUpperSTBEPBound(float upperSTBEPBound) {
        this.upperSTBEPBound = upperSTBEPBound;
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

    @Override
    public String toString(){
        return MessageProvider.getMessage("lowerTBEPThreshold") + "\t" + lowerETBEPBound + "\n"
        + MessageProvider.getMessage("upperTBEPThreshold") + "\t" + upperSTBEPBound + "\n"
        + MessageProvider.getMessage("propagationFactor1") + "\t" + propagationFactor1 + "\n"
        + MessageProvider.getMessage("propagationFactor2") + "\t" + propagationFactor2 + "\n"
        + MessageProvider.getMessage("jammingFactor1") + "\t" + jammingFactor1 + "\n"
        + MessageProvider.getMessage("jammingFactor2") + "\t" + jammingFactor2 + "\n"
        + MessageProvider.getMessage("showStepByStepWithColon") + "\t" + stepByStep;
    }
}

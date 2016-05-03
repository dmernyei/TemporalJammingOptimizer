package temporaljammingoptimizer.logic.algorithms;

import temporaljammingoptimizer.logic.Configuration;
import temporaljammingoptimizer.logic.entities.Jammer;

/**
 * Created by Daniel Mernyei
 */
public abstract class NearestJammerAlgorithm {
    protected Configuration configuration;
    protected Jammer[] jammers;

    public void setConfiguration(Configuration configuration){
        this.configuration = configuration;
    }

    public void setJammers(Jammer[] jammers){
        this.jammers = jammers;
    }

    protected float computeStorageDistanceFactor(float distance){
        return 0.5f * (1 - (float)Math.pow(Math.E, -configuration.getSignalDecayFactor1() * Math.pow(distance, configuration.getSignalDecayFactor2())));
    }

    protected float computeJammerDistanceFactor(float distance){
        return 0.5f * (float)Math.pow(Math.E, -configuration.getJammingFactor1() * Math.pow(distance, configuration.getJammingFactor2()));
    }

    public abstract void reset();
}

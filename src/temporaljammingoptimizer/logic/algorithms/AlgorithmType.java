package temporaljammingoptimizer.logic.algorithms;

import temporaljammingoptimizer.utils.MessageProvider;

/**
 * Created by Daniel Mernyei
 */
public enum AlgorithmType {
    NOT_APPLICABLE(MessageProvider.getMessage("NA")),
    NEAREST_JAMMER(MessageProvider.getMessage("nearestJammer")),
    TWO_NEAREST_JAMMER(MessageProvider.getMessage("twoNearestJammer"));

    private String name;

    AlgorithmType(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}

package temporaljammingoptimizer.logic.exceptions;

import temporaljammingoptimizer.utilities.MessageProvider;
import temporaljammingoptimizer.utilities.StringUtilities;

/**
 * Created by Daniel Mernyei
 */
public class UnAssignableJammerException extends Exception {
    public UnAssignableJammerException(String msg){
        super(msg);
    }

    public UnAssignableJammerException(String msg, int entityID){
        super(StringUtilities.appendIntegerToSentence(msg, entityID));
    }

    public UnAssignableJammerException(){
        super(MessageProvider.getMessage("defaultUnAssignableJammerExceptionMessageForTwoNearestJammerModel"));
    }
}

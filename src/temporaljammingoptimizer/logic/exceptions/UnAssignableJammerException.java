package temporaljammingoptimizer.logic.exceptions;

import temporaljammingoptimizer.utils.MessageProvider;
import temporaljammingoptimizer.utils.StringUtilities;

/**
 * Created by Daniel Mernyei
 */
public class UnAssignableJammerException extends Exception {
    public UnAssignableJammerException(String msg){
        super(msg);
    }

    public UnAssignableJammerException(int jammerID){
        super(StringUtilities.appendIntegerToSentence(MessageProvider.getMessage("defaultUnAssignableJammerExceptionMessage"), jammerID));
    }
}

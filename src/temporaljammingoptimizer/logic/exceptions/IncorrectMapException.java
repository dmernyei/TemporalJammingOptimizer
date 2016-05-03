package temporaljammingoptimizer.logic.exceptions;

import temporaljammingoptimizer.utilities.MessageProvider;

/**
 * Created by Daniel Mernyei
 */
public class IncorrectMapException extends Exception {
    public IncorrectMapException(String msg){
        super(msg);
    }

    public IncorrectMapException(){
        super(MessageProvider.getMessage("defaultIncorrectMapExceptionMessage"));
    }
}

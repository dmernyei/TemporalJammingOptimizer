package temporaljammingoptimizer.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Daniel Mernyei
 */
public class MessageProvider {
    private static final Locale defaultLocale = Locale.ENGLISH;

    private static ResourceBundle messages = ResourceBundle.getBundle("rao.resources.text.MessagesBundle", MessageProvider.defaultLocale);
    private static Locale currentLocale = MessageProvider.defaultLocale;

    public static void setLocale(Locale locale){
        if (!locale.equals(MessageProvider.currentLocale)){
            MessageProvider.messages = ResourceBundle.getBundle("rao.resources.text.MessagesBundle", locale);
            MessageProvider.currentLocale = locale;
        }
    }

    public static String getMessage(String key) {
        return messages.getString(key);
    }
}

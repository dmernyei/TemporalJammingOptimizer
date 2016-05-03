package temporaljammingoptimizer.gui.dialogs;

import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Daniel Mernyei
 */
public class AboutDialog extends AbstractDialog {
    public AboutDialog(JFrame owner){
        super(owner, MessageProvider.getMessage("about"), true, false);
    }

    protected JComponent createContent(){
        JEditorPane aboutEditorPane = new JEditorPane();
        aboutEditorPane.setContentType("text/html");
        aboutEditorPane.setEditable(false);
        aboutEditorPane.setBackground(null);
        aboutEditorPane.setPreferredSize(new Dimension(450, 360));
        aboutEditorPane.setText(MessageProvider.getMessage("aboutText"));

        return aboutEditorPane;
    }
}

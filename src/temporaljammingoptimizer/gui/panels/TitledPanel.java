package temporaljammingoptimizer.gui.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Daniel Mernyei
 */
public class TitledPanel extends JPanel {
    public TitledPanel(String title, JPanel contentPanel){
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(title));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);
    }
}

package temporaljammingoptimizer.gui.panels.mappanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Daniel Mernyei
 */
public class MapContainerPanel extends JPanel {
    public MapContainerPanel(MapPanel mapPanel){
        super(new BorderLayout());
        setPreferredSize(new Dimension(424, 400));
        add(mapPanel, BorderLayout.CENTER);
    }
}

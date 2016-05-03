package temporaljammingoptimizer.gui.panels;

import temporaljammingoptimizer.gui.frames.MainFrame;
import temporaljammingoptimizer.gui.panels.mappanel.MapPanel;
import temporaljammingoptimizer.logic.Optimizer;
import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.logic.exceptions.UnAssignableJammerException;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Daniel Mernyei
 */
public class ControlPanel extends JPanel {
    private Optimizer optimizer;
    private boolean errorOccurred;

    private MainFrame mainFrame;
    private ConfigurePanel configurePanel;
    private MapPanel mapPanel;
    private AlgorithmInformationPanel algorithmInformationPanel;

    private Action optimizeAction, nextStepAction;
    private JButton optimizeButton, nextStepButton;

    public ControlPanel(Optimizer optimizer, MainFrame mainFrame, ConfigurePanel configurePanel, MapPanel mapPanel, AlgorithmInformationPanel algorithmInformationPanel){
        super();
        this.optimizer = optimizer;
        this.mainFrame = mainFrame;
        this.configurePanel = configurePanel;
        this.mapPanel = mapPanel;
        this.algorithmInformationPanel = algorithmInformationPanel;

        createOperations();
        createContent();
    }

    public void setButtonsEnabled(boolean optimizeButtonEnabled, boolean nextStepButtonEnabled){
        optimizeButton.setEnabled(optimizeButtonEnabled);
        nextStepButton.setEnabled(nextStepButtonEnabled);
    }

    private void createOperations(){
        optimizeAction = new AbstractAction(MessageProvider.getMessage("optimize")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                configurePanel.setPropertiesEnabled(false, false);
                configurePanel.assignValuesToOptimizer();
                setButtonsEnabled(false, optimizer.getConfiguration().isStepByStep());

                try {
                    optimizer.optimize();
                } catch (UnAssignableJammerException ex) {
                    errorOccurred = true;
                    JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                }

                if (mapPanel.isDisplayingHighlights()){
                    mapPanel.render();
                }

                if (optimizer.isResultComputed() || errorOccurred){
                    errorOccurred = false;
                    algorithmInformationPanel.refreshInformation(false);
                    enableUIAfterOptimization();
                }
                else if (!optimizer.isResultComputed()){
                    algorithmInformationPanel.refreshInformation(true);
                }
            }
        };

        nextStepAction = new AbstractAction(MessageProvider.getMessage("nextStep")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    optimizer.step();
                } catch (UnAssignableJammerException ex) {
                    errorOccurred = true;
                    JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                }

                algorithmInformationPanel.refreshInformation(false);
                mapPanel.render();

                if (optimizer.isResultComputed() || errorOccurred){
                    errorOccurred = false;
                    enableUIAfterOptimization();
                }
            }
        };
    }

    private void enableUIAfterOptimization(){
        configurePanel.setPropertiesEnabled(true, AlgorithmType.NEAREST_JAMMER == optimizer.getAlgorithmType());
        setButtonsEnabled(true, false);
    }

    private void createContent(){
        setLayout(new BorderLayout(10, 0));

        optimizeButton = new JButton(optimizeAction);
        nextStepButton = new JButton(nextStepAction);
        setButtonsEnabled(false, false);

        add(optimizeButton, BorderLayout.WEST);
        add(nextStepButton, BorderLayout.EAST);
    }
}

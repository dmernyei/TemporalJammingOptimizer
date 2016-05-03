package temporaljammingoptimizer.gui.frames;

import temporaljammingoptimizer.gui.dialogs.AboutDialog;
import temporaljammingoptimizer.gui.dialogs.NotationsAbbreviationsDialog;
import temporaljammingoptimizer.gui.panels.*;
import temporaljammingoptimizer.gui.panels.mappanel.MapContainerPanel;
import temporaljammingoptimizer.gui.panels.mappanel.MapPanel;
import temporaljammingoptimizer.logic.Optimizer;
import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.logic.exceptions.IncorrectMapException;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Daniel Mernyei
 */
public class MainFrame extends JFrame {
    private final int panelPadding;

    private Optimizer optimizer;

    private AboutDialog aboutDialog;
    private NotationsAbbreviationsDialog notationsAbbreviationsDialog;

    private Action openMapAction, openConfigurationAction, saveConfigurationAction, saveResultsAction, notationsAbbreviationsAction, aboutAction;

    private MapPanel mapPanel;
    private ConfigurePanel configurePanel;
    private ControlPanel controlPanel;
    private AlgorithmInformationPanel algorithmInformationPanel;

    public MainFrame(){
        panelPadding = 10;
        optimizer = new Optimizer();

        createOperations();
        createContent();
        setMainGUIAttributes();
        optimizer.setMapSize(mapPanel.getMapSize());
        setVisible(true);
    }

    private void createOperations() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JFrame mainFrame = this;

        openMapAction = new AbstractAction(MessageProvider.getMessage("openMap")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (optimizer.isOptimizationRunning()
                        && 0 != JOptionPane.showConfirmDialog(rootPane, MessageProvider.getMessage("abortCurrentOptimizationMessage"), MessageProvider.getMessage("abortCurrentOptimization"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)){
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (.txt)", "txt");
                chooser.setFileFilter(filter);
                if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null)) {
                    boolean openingStarted = false;
                    boolean errorOccurred = false;

                    try {
                        File selectedFile = chooser.getSelectedFile();
                        if (!selectedFile.getName().endsWith(".txt")){
                            throw new IncorrectMapException(MessageProvider.getMessage("unsupportedFileExtension"));
                        }

                        // Opening map
                        openingStarted = true;
                        optimizer.openMap(selectedFile);
                    } catch (Exception ex) {
                        errorOccurred = true;
                        JOptionPane.showMessageDialog(mainFrame, MessageProvider.getMessage("defaultIncorrectMapExceptionMessage") + '\n' + ex.getMessage(), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                    }

                    // Refreshing GUI
                    if (openingStarted) {
                        if (!errorOccurred) {
                            mapPanel.render();
                        }
                        algorithmInformationPanel.refreshInformation(true);

                        boolean isTypeNearestJammer = AlgorithmType.NEAREST_JAMMER == optimizer.getAlgorithmType();
                        configurePanel.setPropertiesEnabled(true, isTypeNearestJammer);
                        if (!isTypeNearestJammer)
                            configurePanel.setStepByStepCheckBoxState(false);

                        controlPanel.setButtonsEnabled(!errorOccurred, false);
                    }
                }
            }
        };

        openConfigurationAction = new AbstractAction(MessageProvider.getMessage("openConfiguration")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (optimizer.isOptimizationRunning()
                        && 0 != JOptionPane.showConfirmDialog(rootPane, MessageProvider.getMessage("abortCurrentOptimizationMessage"), MessageProvider.getMessage("abortCurrentOptimization"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)){
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TJO config files (.tcfg)", "tcfg");
                chooser.setFileFilter(filter);
                if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null)) {
                    boolean openingStarted = false;
                    try {
                        File selectedFile = chooser.getSelectedFile();
                        if (!selectedFile.getName().endsWith(".tcfg")){
                            throw new IncorrectMapException(MessageProvider.getMessage("unsupportedFileExtension"));
                        }

                        // Opening configuration
                        openingStarted = true;
                        optimizer.openConfiguration(selectedFile);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, MessageProvider.getMessage("errorDuringOpeningConfiguration") + '\n' + ex.getMessage(), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                    }

                    // Refreshing GUI
                    if (openingStarted) {
                        configurePanel.refreshValuesFromOptimizer();

                        if (optimizer.isMapLoadedCorrectly()) {
                            algorithmInformationPanel.refreshInformation(true);
                            mapPanel.render();

                            boolean isTypeNearestJammer = AlgorithmType.NEAREST_JAMMER == optimizer.getAlgorithmType();
                            configurePanel.setPropertiesEnabled(true, isTypeNearestJammer);
                            if (!isTypeNearestJammer)
                                configurePanel.setStepByStepCheckBoxState(false);

                            controlPanel.setButtonsEnabled(true, false);
                        }
                    }
                }
            }
        };

        saveConfigurationAction = new AbstractAction(MessageProvider.getMessage("saveConfiguration")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TJO config files (.tcfg)", "tcfg");
                chooser.setFileFilter(filter);
                if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(null)) {
                    try {
                        configurePanel.assignValuesToOptimizer();
                        optimizer.saveConfiguration(chooser.getSelectedFile().getAbsolutePath());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(rootPane, MessageProvider.getMessage("errorDuringSavingConfiguration") + '\n' + ex.getMessage(), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        saveResultsAction = new AbstractAction(MessageProvider.getMessage("saveResults")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!optimizer.isResultComputed()){
                    JOptionPane.showMessageDialog(rootPane, MessageProvider.getMessage("noResultsToSave"), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files (.txt)", "txt");
                chooser.setFileFilter(filter);
                if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(null)) {
                    try {
                        optimizer.saveResult(chooser.getSelectedFile().getAbsolutePath());
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(rootPane, MessageProvider.getMessage("errorDuringSavingResults") + '\n' + ex.getMessage(), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        notationsAbbreviationsAction = new AbstractAction(MessageProvider.getMessage("notationsAbbreviations")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                notationsAbbreviationsDialog.showDialog();
            }
        };

        aboutAction = new AbstractAction(MessageProvider.getMessage("about")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutDialog.showDialog();
            }
        };
    }

    private void createContent() {
        createMenuBar();

        JPanel containerPanel = new JPanel(new BorderLayout(panelPadding, panelPadding));
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mapPanel = new MapPanel(optimizer);
        configurePanel = new ConfigurePanel(optimizer);
        algorithmInformationPanel = new AlgorithmInformationPanel(optimizer);
        controlPanel = new ControlPanel(optimizer, this, configurePanel, mapPanel, algorithmInformationPanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(new TitledPanel(MessageProvider.getMessage("configure"), configurePanel));
        centerPanel.add(Box.createRigidArea(new Dimension(0, panelPadding)));
        centerPanel.add(new TitledPanel(MessageProvider.getMessage("control"), controlPanel));

        MapContainerPanel mapContainerPanel = new MapContainerPanel(mapPanel);
        containerPanel.add(new TitledPanel(MessageProvider.getMessage("map"), mapContainerPanel), BorderLayout.WEST);
        containerPanel.add(centerPanel, BorderLayout.CENTER);
        containerPanel.add(new TitledPanel(MessageProvider.getMessage("algorithminformation"), algorithmInformationPanel), BorderLayout.EAST);

        add(containerPanel);

        aboutDialog = new AboutDialog(this);
        notationsAbbreviationsDialog = new NotationsAbbreviationsDialog(this);
    }

    private void createMenuBar(){
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu(MessageProvider.getMessage("file"));
        file.add(openMapAction);
        file.add(new JSeparator());
        file.add(openConfigurationAction);
        file.add(saveConfigurationAction);
        file.add(new JSeparator());
        file.add(saveResultsAction);
        mb.add(file);
        JMenu help = new JMenu(MessageProvider.getMessage("help"));
        help.add(notationsAbbreviationsAction);
        help.add(aboutAction);
        mb.add(help);
        setJMenuBar(mb);
    }

    private void setMainGUIAttributes() {
        setTitle(MessageProvider.getMessage("temporalJammingOptimizer"));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }
}

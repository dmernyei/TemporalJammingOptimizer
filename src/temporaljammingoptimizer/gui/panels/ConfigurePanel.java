package temporaljammingoptimizer.gui.panels;

import temporaljammingoptimizer.gui.utilities.SpringUtilities;
import temporaljammingoptimizer.logic.Configuration;
import temporaljammingoptimizer.logic.Optimizer;
import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by Daniel Mernyei
 */
public class ConfigurePanel extends JPanel {
    private Optimizer optimizer;

    private JSpinner lowerETBEPBoundSpinner;
    private JSpinner upperSTBEPBoundSpinner;
    private JSpinner signalDecayFactor1Spinner;
    private JSpinner signalDecayFactor2Spinner;
    private JSpinner jammingFactor1Spinner;
    private JSpinner jammingFactor2Spinner;
    private Checkbox stepByStepCheckBox;

    private ChangeListener propertyChangedListener;

    public ConfigurePanel(Optimizer optimizer){
        super(new BorderLayout(0, 26));
        this.optimizer = optimizer;

        createOperations();
        createContent();
    }

    public void setStepByStepCheckBoxState(boolean state){
        stepByStepCheckBox.setState(state);
    }

    public void setPropertiesEnabled(boolean numbersEnabled, boolean stepByStepEnabled){
        lowerETBEPBoundSpinner.setEnabled(numbersEnabled);
        upperSTBEPBoundSpinner.setEnabled(numbersEnabled);
        signalDecayFactor1Spinner.setEnabled(numbersEnabled);
        signalDecayFactor2Spinner.setEnabled(numbersEnabled);
        jammingFactor1Spinner.setEnabled(numbersEnabled);
        jammingFactor2Spinner.setEnabled(numbersEnabled);
        stepByStepCheckBox.setEnabled(stepByStepEnabled);
    }

    public void refreshValuesFromOptimizer(){
        Configuration configuration = optimizer.getConfiguration();
        lowerETBEPBoundSpinner.setValue((double)configuration.getLowerETBEPBound());
        upperSTBEPBoundSpinner.setValue((double)configuration.getUpperSTBEPBound());
        signalDecayFactor1Spinner.setValue((double)configuration.getSignalDecayFactor1());
        signalDecayFactor2Spinner.setValue((double)configuration.getSignalDecayFactor2());
        jammingFactor1Spinner.setValue((Double)((SpinnerNumberModel)jammingFactor1Spinner.getModel()).getMaximum() - (double)configuration.getJammingFactor1());
        jammingFactor2Spinner.setValue((Double)((SpinnerNumberModel)jammingFactor2Spinner.getModel()).getMaximum() - (double)configuration.getJammingFactor2());
        stepByStepCheckBox.setState(AlgorithmType.TWO_NEAREST_JAMMER != optimizer.getAlgorithmType() && configuration.isStepByStep());
    }

    public void assignValuesToOptimizer(){
        Configuration configuration = new Configuration(
                ((Double)lowerETBEPBoundSpinner.getValue()).floatValue(),
                ((Double)upperSTBEPBoundSpinner.getValue()).floatValue(),
                ((Double)signalDecayFactor1Spinner.getValue()).floatValue(),
                ((Double)signalDecayFactor2Spinner.getValue()).floatValue(),
                ((Double)((SpinnerNumberModel)jammingFactor1Spinner.getModel()).getMaximum()).floatValue() - ((Double)jammingFactor1Spinner.getValue()).floatValue(),
                ((Double)((SpinnerNumberModel)jammingFactor2Spinner.getModel()).getMaximum()).floatValue() - ((Double)jammingFactor2Spinner.getValue()).floatValue(),
                stepByStepCheckBox.getState()
        );

        optimizer.setConfiguration(configuration);
    }

    private void createOperations(){
        propertyChangedListener = e -> {
            if (optimizer.isResultComputed())
                optimizer.clearData(false);
        };
    }

    private void createContent(){
        createSpinnerProperties();
        stepByStepCheckBox = new Checkbox(MessageProvider.getMessage("showStepByStep"), true);
        add(stepByStepCheckBox, BorderLayout.SOUTH);

        refreshValuesFromOptimizer();
    }

    private void createSpinnerProperties() {
        JPanel spinnerPropertiesPanel = new JPanel(new SpringLayout());

        String[] labelStrings = {
                MessageProvider.getMessage("lowerETBEPBound"),
                MessageProvider.getMessage("upperSTBEPBound"),
                MessageProvider.getMessage("signalDecayFactor1"),
                MessageProvider.getMessage("signalDecayFactor2"),
                MessageProvider.getMessage("jammingFactor1"),
                MessageProvider.getMessage("jammingFactor2")
        };

        JLabel[] labels = new JLabel[labelStrings.length];
        JSpinner[] properties = new JSpinner[labelStrings.length];
        int propertyNum = 0;

        // Creating the properties
        lowerETBEPBoundSpinner = new JSpinner(new SpinnerNumberModel(Configuration.getMinTBEPBound(), Configuration.getMinTBEPBound(), Configuration.getMaxTBEPBound(), Configuration.getTBEPBoundStep()));
        properties[propertyNum++] = lowerETBEPBoundSpinner;
        upperSTBEPBoundSpinner = new JSpinner(new SpinnerNumberModel(Configuration.getMinTBEPBound(), Configuration.getMinTBEPBound(), Configuration.getMaxTBEPBound(), Configuration.getTBEPBoundStep()));
        properties[propertyNum++] = upperSTBEPBoundSpinner;
        signalDecayFactor1Spinner = new JSpinner(new SpinnerNumberModel(Configuration.getMinSignalDecayFactor(), Configuration.getMinSignalDecayFactor(), Configuration.getMaxSignalDecayFactor(), Configuration.getSignalDecayFactorStep()));
        properties[propertyNum++] = signalDecayFactor1Spinner;
        signalDecayFactor2Spinner = new JSpinner(new SpinnerNumberModel(Configuration.getMinSignalDecayFactor(), Configuration.getMinSignalDecayFactor(), Configuration.getMaxSignalDecayFactor(), Configuration.getSignalDecayFactorStep()));
        properties[propertyNum++] = signalDecayFactor2Spinner;
        jammingFactor1Spinner = new JSpinner(new SpinnerNumberModel(Configuration.getMinJammingFactor(), Configuration.getMinJammingFactor(), Configuration.getMaxJammingFactor(), Configuration.getJammingFactorStep()));
        properties[propertyNum++] = jammingFactor1Spinner;
        jammingFactor2Spinner = new JSpinner(new SpinnerNumberModel(Configuration.getMinJammingFactor(), Configuration.getMinJammingFactor(), Configuration.getMaxJammingFactor(), Configuration.getJammingFactorStep()));
        properties[propertyNum] = jammingFactor2Spinner;

        // Associating label/property pairs, adding everything, and laying it out
        for (int i = 0; i < labelStrings.length; ++i) {
            labels[i] = new JLabel(labelStrings[i]);
            spinnerPropertiesPanel.add(labels[i]);
            spinnerPropertiesPanel.add(properties[i]);

            properties[i].addChangeListener(propertyChangedListener);
        }

        SpringUtilities.makeCompactGrid(spinnerPropertiesPanel, labelStrings.length, 2, 0, 0, 20, 26);
        add(spinnerPropertiesPanel, BorderLayout.CENTER);
    }
}

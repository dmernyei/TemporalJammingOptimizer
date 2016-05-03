package temporaljammingoptimizer.gui.panels;

import temporaljammingoptimizer.gui.tables.JAPBoundsTableModel;
import temporaljammingoptimizer.gui.tables.JAPResultsTableModel;
import temporaljammingoptimizer.logic.Optimizer;
import temporaljammingoptimizer.logic.algorithms.AlgorithmType;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * Created by Daniel Mernyei
 */
public class AlgorithmInformationPanel extends JPanel {
    private Optimizer optimizer;

    private JLabel modelLabel;
    private JLabel jammerIdLabel;
    private JLabel minOfMaxJAPLabel;
    private JLabel maxOfMinJAPLabel;
    private JLabel totalJAPSumLabel;

    private JTable SWPJAPBoundsTable;
    private JTable EWPJAPBoundsTable;
    private JTable JAPResultsTable;

    private JPanel acceptableJAPValuesHolderPanel;

    public AlgorithmInformationPanel(Optimizer optimizer){
        super();
        this.optimizer = optimizer;

        createContent();
        refreshInformation(true);
    }

    public void refreshInformation(boolean clear){
        AlgorithmType algorithmType = optimizer.getAlgorithmType();

        if (clear){
            String modelString;
            switch (algorithmType){
                case NEAREST_JAMMER:
                    modelString = MessageProvider.getMessage("nearestJammer");
                    break;
                case TWO_NEAREST_JAMMER:
                    modelString = MessageProvider.getMessage("twoNearestJammer");
                    break;
                default:
                    modelString = MessageProvider.getMessage("na");
                    break;
            }
            modelLabel.setText(MessageProvider.getMessage("model")+ " " + modelString);

            if (AlgorithmType.TWO_NEAREST_JAMMER == algorithmType && acceptableJAPValuesHolderPanel.isVisible()){
                acceptableJAPValuesHolderPanel.setVisible(false);
            }
            else if(AlgorithmType.NOT_APPLICABLE == algorithmType || AlgorithmType.NEAREST_JAMMER == algorithmType){
                jammerIdLabel.setText(MessageProvider.getMessage("jammerId") + " " + MessageProvider.getMessage("na"));
                ((AbstractTableModel)SWPJAPBoundsTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel)EWPJAPBoundsTable.getModel()).fireTableDataChanged();
                minOfMaxJAPLabel.setText(MessageProvider.getMessage("minOfMaxJAP") + " " + MessageProvider.getMessage("na"));
                maxOfMinJAPLabel.setText(MessageProvider.getMessage("maxOfMinJAP") + " " + MessageProvider.getMessage("na"));

                if (!acceptableJAPValuesHolderPanel.isVisible()) {
                    acceptableJAPValuesHolderPanel.setVisible(true);
                }
            }

            JAPResultsTable.setModel(new JAPResultsTableModel(optimizer));
            totalJAPSumLabel.setText(MessageProvider.getMessage("totalJAPSum") + " " + MessageProvider.getMessage("na"));
        }
        else{
            if (AlgorithmType.NEAREST_JAMMER == algorithmType){
                int i = 0;
                while (i < optimizer.getJammerCount() && optimizer.getJammerAt(i).isJAPSet())
                    ++i;

                jammerIdLabel.setText(MessageProvider.getMessage("jammerId") + " " + optimizer.getJammerAt(Math.max(0, i - 1)).getId());

                ((AbstractTableModel)SWPJAPBoundsTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel)EWPJAPBoundsTable.getModel()).fireTableDataChanged();

                float JAPBound = optimizer.getMinStoragePointJAPBound();
                minOfMaxJAPLabel.setText(MessageProvider.getMessage("minOfMaxJAP") + " " + ((-1 == JAPBound) ? MessageProvider.getMessage("na") : JAPBound));
                JAPBound = optimizer.getMaxEavesdropperPointJAPBound();
                maxOfMinJAPLabel.setText(MessageProvider.getMessage("maxOfMinJAP") + " " + ((-1 == JAPBound) ? MessageProvider.getMessage("na") : JAPBound));
            }

            ((AbstractTableModel)JAPResultsTable.getModel()).fireTableDataChanged();
            if (optimizer.isResultComputed())
                totalJAPSumLabel.setText(MessageProvider.getMessage("totalJAPSum") + " " + optimizer.getTotalJAPSum());
        }
    }

    private void createContent(){
        setLayout(new BorderLayout(0, 10));

        modelLabel = new JLabel();
        add(modelLabel, BorderLayout.NORTH);

        add(createAcceptableJAPValuesPanel(), BorderLayout.CENTER);
        add(createJAPResultsPanel(), BorderLayout.SOUTH);
    }

    private JPanel createAcceptableJAPValuesPanel(){
        JPanel acceptableJAPValuesPanel = new JPanel(new BorderLayout(10, 10));

        jammerIdLabel = new JLabel();
        acceptableJAPValuesPanel.add(jammerIdLabel, BorderLayout.NORTH);

        minOfMaxJAPLabel = new JLabel("", JLabel.LEADING);
        SWPJAPBoundsTable = new JTable(new JAPBoundsTableModel(optimizer.getStoragePointJAPBoundInfos(), true));
        SWPJAPBoundsTable.getTableHeader().setReorderingAllowed(false);
        acceptableJAPValuesPanel.add(createWitnessPointDataPanel(SWPJAPBoundsTable, minOfMaxJAPLabel), BorderLayout.WEST);

        maxOfMinJAPLabel = new JLabel("", JLabel.LEADING);
        EWPJAPBoundsTable = new JTable(new JAPBoundsTableModel(optimizer.getEavesdropperPointJAPBoundInfos(), false));
        EWPJAPBoundsTable.getTableHeader().setReorderingAllowed(false);
        acceptableJAPValuesPanel.add(createWitnessPointDataPanel(EWPJAPBoundsTable, maxOfMinJAPLabel), BorderLayout.EAST);

        acceptableJAPValuesHolderPanel = new TitledPanel(MessageProvider.getMessage("acceptableJAPValues"), acceptableJAPValuesPanel);
        return acceptableJAPValuesHolderPanel;
    }

    private JPanel createWitnessPointDataPanel(JTable JAPBoundTable, JLabel extremalValueLabel){
        JPanel witnessPointDataPanel = new JPanel();
        witnessPointDataPanel.setLayout(new BoxLayout(witnessPointDataPanel, BoxLayout.Y_AXIS));

        JAPBoundTable.setCellSelectionEnabled(false);
        JScrollPane scrollPane = new JScrollPane(JAPBoundTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(150, 150));

        JPanel labelHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelHolder.add(extremalValueLabel);

        witnessPointDataPanel.add(scrollPane);
        witnessPointDataPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        witnessPointDataPanel.add(labelHolder);

        return witnessPointDataPanel;
    }

    private JPanel createJAPResultsPanel(){
        JPanel JAPResultsPanel = new JPanel();
        JAPResultsPanel.setLayout(new BoxLayout(JAPResultsPanel, BoxLayout.Y_AXIS));

        JAPResultsTable = new JTable(new JAPResultsTableModel(optimizer));
        JAPResultsTable.setCellSelectionEnabled(false);
        JAPResultsTable.getTableHeader().setReorderingAllowed(false);
        JAPResultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane = new JScrollPane(JAPResultsTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 54));

        JPanel labelHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        totalJAPSumLabel = new JLabel();
        labelHolder.add(totalJAPSumLabel);

        JAPResultsPanel.add(scrollPane);
        JAPResultsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        JAPResultsPanel.add(labelHolder);

        return new TitledPanel(MessageProvider.getMessage("JAPResults"), JAPResultsPanel);
    }
}

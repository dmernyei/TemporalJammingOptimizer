package temporaljammingoptimizer.gui.tables;

import temporaljammingoptimizer.logic.algorithms.onenearestjammeralgorithm.WitnessPointJAPBoundInfo;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Daniel Mernyei
 */
public class JAPBoundsTableModel extends AbstractTableModel {
    private ArrayList<WitnessPointJAPBoundInfo> witnessPointJAPBoundInfos;
    private String[] columnNames;

    public JAPBoundsTableModel(ArrayList<WitnessPointJAPBoundInfo> witnessPointJAPBoundInfos, boolean displayingStorageData){
        super();
        this.witnessPointJAPBoundInfos = witnessPointJAPBoundInfos;

        if (displayingStorageData){
            columnNames = new String[]{
                    MessageProvider.getMessage("SWPId"),
                    MessageProvider.getMessage("maxJAP"),
            };
        }
        else{
            columnNames = new String[]{
                    MessageProvider.getMessage("EWPId"),
                    MessageProvider.getMessage("minJAP"),
            };
        }
    }

    @Override
    public String getColumnName(int column){
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return witnessPointJAPBoundInfos.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return 1 == columnIndex ? Integer.class : Float.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WitnessPointJAPBoundInfo witnessPointJAPBoundInfo = witnessPointJAPBoundInfos.get(rowIndex);
        return 0 == columnIndex ? witnessPointJAPBoundInfo.getWitnessPointId() : witnessPointJAPBoundInfo.getJAPBound();
    }
}

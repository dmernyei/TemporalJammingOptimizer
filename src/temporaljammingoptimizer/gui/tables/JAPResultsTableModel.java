package temporaljammingoptimizer.gui.tables;

import temporaljammingoptimizer.logic.Optimizer;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.table.AbstractTableModel;

/**
 * Created by Daniel Mernyei
 */
public class JAPResultsTableModel extends AbstractTableModel {
    private Optimizer optimizer;

    public JAPResultsTableModel(Optimizer optimizer){
        super();
        this.optimizer = optimizer;
    }

    public String getColumnName(int column){
        return (column + 1) + "";
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return optimizer.getJammerCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        float JAP = optimizer.getJammerAt(columnIndex).getJAP();
        return -1 == JAP ? MessageProvider.getMessage("na") : JAP;
    }
}

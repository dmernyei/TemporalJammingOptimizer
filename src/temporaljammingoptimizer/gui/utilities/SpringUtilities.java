package temporaljammingoptimizer.gui.utilities;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Daniel Mernyei
 */
public class SpringUtilities {
    public static void makeCompactGrid(Container parent, int rowCount, int colCount, int initialXPos, int initialYPos, int xPadding, int yPadding) {
        SpringLayout layout = (SpringLayout)parent.getLayout();

        // Aligning all cells in each column and make them the same width
        Spring x = Spring.constant(initialXPos);
        for (int c = 0; c < colCount; ++c) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rowCount; ++r) {
                width = Spring.max(width, getConstraintsForCell(r, c, parent, colCount).getWidth());
            }

            for (int r = 0; r < rowCount; ++r) {
                SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, colCount);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, c < colCount - 1 ? Spring.sum(width, Spring.constant(xPadding)) : width);
        }

        // Aligning all cells in each row and make them the same height
        Spring y = Spring.constant(initialYPos);
        for (int r = 0; r < rowCount; ++r) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < colCount; ++c) {
                height = Spring.max(height, getConstraintsForCell(r, c, parent, colCount).getHeight());
            }

            for (int c = 0; c < colCount; ++c) {
                SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, colCount);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, r < rowCount - 1 ? Spring.sum(height, Spring.constant(yPadding)) : height);
        }

        // Setting the parent's size
        SpringLayout.Constraints parentConstraints = layout.getConstraints(parent);
        parentConstraints.setConstraint(SpringLayout.SOUTH, y);
        parentConstraints.setConstraint(SpringLayout.EAST, x);
    }

    private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int colCount) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * colCount + col);
        return layout.getConstraints(c);
    }
}

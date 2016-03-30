package temporaljammingoptimizer;

import temporaljammingoptimizer.gui.frames.MainFrame;

import javax.swing.*;

public class TemporalJammingOptimizer{
    public static void main(String[] args){
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
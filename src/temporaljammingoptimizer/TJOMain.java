package temporaljammingoptimizer;

import temporaljammingoptimizer.gui.frames.MainFrame;
import temporaljammingoptimizer.logic.Optimizer;
import temporaljammingoptimizer.logic.geometry.Polygon;
import temporaljammingoptimizer.logic.geometry.Size;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;

public class TJOMain {
    public static void main(String[] args){
//        Size s = new Size(1200, 1000);
//        Optimizer o = new Optimizer(s);
//        try {
//            o.loadMap("input2.txt");
//        }
//        catch (Exception ex){
//            System.out.println(ex.getMessage());
//        }
//
//        System.out.println(o.getAlgorithmType());
//        Polygon controlled = o.getControlledRegion();
//        Polygon storage = o.getStorage();
//
//        System.out.println("controlled:");
//        for (int i = 0; i < controlled.getVertexCount(); ++i){
//            System.out.println(controlled.getVertexAt(i));
//        }
//
//        System.out.println("storage:");
//        for (int i = 0; i < storage.getVertexCount(); ++i){
//            System.out.println(storage.getVertexAt(i));
//        }
//
//        System.out.println("jammers:");
//        for (int i = 0; i < o.getJammerCount(); ++i){
//            System.out.println(o.getJammerAt(i));
//        }
//
//
//        System.out.println("witness points:");
//        for (int i = 0; i < o.getWitnessPointCount(); ++i){
//            System.out.println(o.getWitnessPointAt(i));
//        }

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
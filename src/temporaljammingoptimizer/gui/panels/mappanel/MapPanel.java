package temporaljammingoptimizer.gui.panels.mappanel;

import temporaljammingoptimizer.logic.Optimizer;
import temporaljammingoptimizer.logic.entities.Entity;
import temporaljammingoptimizer.logic.entities.Jammer;
import temporaljammingoptimizer.logic.entities.WitnessPoint;
import temporaljammingoptimizer.logic.geometry.*;
import temporaljammingoptimizer.logic.geometry.Polygon;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * Created by Daniel Mernyei
 */
public class MapPanel extends JPanel {
    private final float entityRadius;
    private final float selectedEntityRadius;
    private final float idRadius;

    private Optimizer optimizer;

    private JLabel noMapIsOpenLabel;
    private boolean isLabelDisplayed = true;

    private boolean displayingHighlights;

    public MapPanel(Optimizer optimizer){
        super();
        entityRadius = 12.0f;
        idRadius = entityRadius * 2/3;
        selectedEntityRadius = idRadius + 2 * (entityRadius - idRadius);
        this.optimizer = optimizer;
        createContent();
    }

    public Size getMapSize(){
        return new Size(getWidth(), getHeight() + 10);
    }

    public boolean isDisplayingHighlights() {
        return displayingHighlights;
    }

    public void render(){
        if (isLabelDisplayed){
            remove(noMapIsOpenLabel);
            isLabelDisplayed = false;
            setBackground(new Color(217, 222, 228));
        }

        repaint();
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        if (isLabelDisplayed || !optimizer.isMapLoadedCorrectly())
            return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font(Font.DIALOG, Font.BOLD, 12));

        drawPolygon(g2, false);
        drawPolygon(g2, true);

        drawEntities(g2, true);
        drawEntities(g2, false);
    }

    private void drawPolygon(Graphics2D g2, boolean drawingStorage){
        // Setting stroke
        g2.setPaint(new Color(0, 0, 0));
        BasicStroke stroke;
        if (drawingStorage) {
            stroke = new BasicStroke(2.0f);
        }
        else{
            float dash[] = { 3.0f, 10.0f };
            stroke = new BasicStroke(
                    4.0f,
                    BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_MITER,
                    10.0f,
                    dash,
                    0.0f
            );
        }
        g2.setStroke(stroke);

        // Setting shape
        GeneralPath path = new GeneralPath();
        Polygon polygon = drawingStorage ? optimizer.getStorage() : optimizer.getControlledRegion();

        Vector2 vertex = polygon.getVertexAt(0);
        path.moveTo(vertex.getX(), vertex.getY());

        for (int i = 1; i < polygon.getVertexCount(); ++i){
            vertex = polygon.getVertexAt(i);
            path.lineTo(vertex.getX(), vertex.getY());
        }
        path.closePath();

        // Drawing
        g2.draw(path);

        // Filling
        g2.setPaint(drawingStorage ? new Color(200, 148, 77) : new Color(255, 255, 255));
        g2.fill(path);
    }

    private void drawEntities(Graphics2D g2, boolean drawingJammers){
        Color black = new Color(0, 0, 0);
        Color yellow = new Color(231, 197, 37);
        Color white = new Color(255, 255, 255);
        Color blue = new Color(61, 152, 255);
        Color red = new Color(255, 0, 0);

        // Setting stroke
        BasicStroke stroke = new BasicStroke(2.0f);
        g2.setStroke(stroke);

        // Setting, drawing, filling shapes
        Entity entity;
        int id;
        Vector2 position;
        Ellipse2D circle;
        int count = drawingJammers ? optimizer.getJammerCount() : optimizer.getWitnessPointCount();

        // If we have a currently examined jammer, find it
        displayingHighlights = optimizer.getConfiguration().isStepByStep();
        Jammer jammerHighlighted = null;
        if (displayingHighlights){
            int jammerCount = optimizer.getJammerCount();
            Jammer currentJammer;
            for (int i = 0; i < jammerCount; ++i){
                currentJammer = optimizer.getJammerAt(i);
                if (currentJammer.isJAPSet() && (i == jammerCount - 1 || !optimizer.getJammerAt(i + 1).isJAPSet())){
                    jammerHighlighted = currentJammer;
                    break;
                }
            }

            if (null == jammerHighlighted)
                displayingHighlights = false;
        }

        // Draw entities
        for (int i = 0; i < count; ++i){
            entity = drawingJammers ? optimizer.getJammerAt(i) : optimizer.getWitnessPointAt(i);
            id = entity.getId();
            position = entity.getPosition();

            // If we are drawing the currently examined jammer or a witness point which is affected by the currently examined jammer, mark it with red
            if (displayingHighlights && (drawingJammers && entity.equals(jammerHighlighted) || !drawingJammers && ((WitnessPoint)entity).isOneNearestJammer(jammerHighlighted))){
                circle = new Ellipse2D.Float(position.getX() - selectedEntityRadius, position.getY() - selectedEntityRadius, 2 * selectedEntityRadius, 2 * selectedEntityRadius);
                g2.setPaint(black);
                g2.draw(circle);
                g2.setPaint(red);
                g2.fill(circle);
            }

            // Drawing entity circle
            circle = new Ellipse2D.Float(position.getX() - entityRadius, position.getY() - entityRadius, 2 * entityRadius, 2 * entityRadius);
            g2.setPaint(black);
            g2.draw(circle);
            if (drawingJammers)
                g2.setPaint(yellow);
            else
                g2.setPaint(((WitnessPoint)entity).isStoragePoint() ? blue : black);
            g2.fill(circle);

            // Drawing number circle
            circle = new Ellipse2D.Float(position.getX() - idRadius, position.getY() - idRadius, idRadius * 2, idRadius * 2);
            g2.setPaint(black);
            g2.draw(circle);
            g2.setPaint(white);
            g2.fill(circle);

            // Drawing number
            g2.setPaint(black);
            g2.drawString(id + "", position.getX() - (10 > id ? 1 : 2) * 3, position.getY() + 5);
        }

        //displayingHighlights = false;
    }

    private void createContent(){
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 180));
        noMapIsOpenLabel = new JLabel(MessageProvider.getMessage("noMapIsOpen"));
        add(noMapIsOpenLabel);
    }
}

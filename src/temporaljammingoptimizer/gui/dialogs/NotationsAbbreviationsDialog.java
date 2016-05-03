package temporaljammingoptimizer.gui.dialogs;

import temporaljammingoptimizer.gui.panels.TitledPanel;
import temporaljammingoptimizer.gui.utilities.SpringUtilities;
import temporaljammingoptimizer.utilities.MessageProvider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Daniel Mernyei
 */
public class NotationsAbbreviationsDialog extends AbstractDialog {

    public NotationsAbbreviationsDialog(JFrame owner){
        super(owner, MessageProvider.getMessage("notationsAbbreviations"), false, true);
        setAlwaysOnTop(true);
    }

    @Override
    protected JComponent createContent() {
        JPanel content = new JPanel(new BorderLayout(0, padding));
        content.add(createNotationsPanel(), BorderLayout.NORTH);
        content.add(createAbbreviationsPanel(), BorderLayout.CENTER);
        return content;
    }

    private JPanel createNotationsPanel() {
        // Initializing
        JPanel notationsPanel = new JPanel();
        notationsPanel.setLayout(new BoxLayout(notationsPanel, BoxLayout.X_AXIS));

        JPanel notationsLeft = new JPanel(new SpringLayout());
        JPanel notationsRight = new JPanel(new SpringLayout());
        JPanel notationsRightHolder = new JPanel(new BorderLayout());
        notationsRightHolder.add(notationsRight, BorderLayout.NORTH);

        try {
            // Creating notationsLeft
            String[] labelStrings = {
                    MessageProvider.getMessage("uncontrolledRegion"),
                    MessageProvider.getMessage("controlledRegion"),
                    MessageProvider.getMessage("storage"),
                    MessageProvider.getMessage("jammer"),
                    MessageProvider.getMessage("jammerHighlighted"),
            };

            String imageFolderPath = MessageProvider.getMessage("imageFolderPath");
            JLabel[] images = {
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "uncontrolled_region.png")))),
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "controlled_region.png")))),
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "storage.png")))),
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "jammer.png")))),
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "jammer_highlighted.png"))))
            };

            JLabel[] labels = new JLabel[labelStrings.length];
            for (int i = 0; i < labelStrings.length; ++i) {
                labels[i] = new JLabel(labelStrings[i]);
                notationsLeft.add(images[i]);
                notationsLeft.add(labels[i]);
            }
            SpringUtilities.makeCompactGrid(notationsLeft, labelStrings.length, 2, 0, 0, 20, 5);

            // Creating notationsRight
            labelStrings = new String[] {
                    MessageProvider.getMessage("storagePoint"),
                    MessageProvider.getMessage("storagePointHighlighted"),
                    MessageProvider.getMessage("eavesdropperPoint"),
                    MessageProvider.getMessage("eavesdropperPointHighlighted")
            };

            images = new JLabel[] {
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "storage_point.png")))),
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "storage_point_highlighted.png")))),
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "eavesdropper.png")))),
                    new JLabel(new ImageIcon(ImageIO.read(new File(imageFolderPath + "eavesdropper_highlighted.png"))))
            };

            labels = new JLabel[labelStrings.length];
            for (int i = 0; i < labelStrings.length; ++i) {
                labels[i] = new JLabel(labelStrings[i]);
                notationsRight.add(images[i]);
                notationsRight.add(labels[i]);
            }
            SpringUtilities.makeCompactGrid(notationsRight, labelStrings.length, 2, 0, 0, 20, 5);
        }
        catch (IOException ex){
            JOptionPane.showMessageDialog(getOwner(), MessageProvider.getMessage("couldNotLoadImages"), MessageProvider.getMessage("error"), JOptionPane.ERROR_MESSAGE);
        }

        // Putting it all together
        notationsPanel.add(notationsLeft);
        notationsPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        notationsPanel.add(notationsRightHolder);

        return new TitledPanel(MessageProvider.getMessage("notations"), notationsPanel);
    }

    private JPanel createAbbreviationsPanel(){
        JPanel abbreviationsPanel = new JPanel();
        abbreviationsPanel.setLayout(new BoxLayout(abbreviationsPanel, BoxLayout.Y_AXIS));

        String[] labelStrings = {
                MessageProvider.getMessage("TBEPDescription"),
                MessageProvider.getMessage("ETBEPDescription"),
                MessageProvider.getMessage("STBEPDescription"),
                MessageProvider.getMessage("JAPDescription"),
                MessageProvider.getMessage("SWPDescription"),
                MessageProvider.getMessage("EWPDescription")
        };

        int labelCount = labelStrings.length;
        for (int i = 0; i < labelCount; ++i){
            abbreviationsPanel.add(new JLabel(labelStrings[i]));

            if (i < labelCount - 1)
                abbreviationsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return new TitledPanel(MessageProvider.getMessage("abbreviations"), abbreviationsPanel);
    }
}

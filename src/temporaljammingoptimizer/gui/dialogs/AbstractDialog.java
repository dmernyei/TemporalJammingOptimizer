package temporaljammingoptimizer.gui.dialogs;

import temporaljammingoptimizer.utilities.MessageProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Daniel Mernyei
 */
public abstract class AbstractDialog extends JDialog {
    final int padding = 10;

    private Action okAction;

    AbstractDialog(JFrame owner, String title, boolean isModal, boolean addPaddingToTop){
        super(owner, title, isModal);
        createActions();
        createMainPanel(addPaddingToTop);
        setMainGUIAttributes();
    }

    public void showDialog(){
        if (isVisible()){
            requestFocus();
        }
        else{
            setLocationRelativeTo(getOwner());
            setVisible(true);
        }
    }

    private void hideDialog(){
        setVisible(false);
    }

    private void createActions(){
        okAction = new AbstractAction(MessageProvider.getMessage("ok")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideDialog();
            }
        };
    }

    private void createMainPanel(boolean addPaddingToTop){
        // Initializing
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(padding, padding));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(addPaddingToTop ? padding : 0, padding, padding, padding));

        // Adding content
        mainPanel.add(createContent(), BorderLayout.CENTER);

        // Adding footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footer.add(new JButton(okAction));
        mainPanel.add(footer, BorderLayout.SOUTH);

        // Adding to JDialog
        add(mainPanel, BorderLayout.CENTER);
    }

    protected abstract JComponent createContent();

    private void setMainGUIAttributes(){
        setResizable(false);
        pack();
    }
}

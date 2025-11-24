package frameworks_and_drivers;

import entities.Pokemon;
import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;
import use_case.open_pack.OpenPackInputBoundary;
import use_case.open_pack.OpenPackInputData;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PreOpenPackView extends JPanel implements ActionListener {

    private final OpenPackController controller;
    private final OpenPackViewModel viewModel;
    private final Runnable onBack;

    // UI components
    private final JLabel titleLabel = new JLabel("Open a Booster Pack", SwingConstants.CENTER);
    private final JLabel currencyLabel = new JLabel("Currency: 0", SwingConstants.CENTER);
    private final JButton openPackButton = new JButton("Open Pack");
    private final JButton backButton = new JButton("Back");

    public PreOpenPackView(OpenPackController controller, OpenPackViewModel viewModel, Runnable onBack) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.onBack = onBack;

        setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        center.add(Box.createVerticalStrut(20));
        center.add(titleLabel);
        center.add(Box.createVerticalStrut(10));
        center.add(currencyLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(openPackButton);
        center.add(Box.createVerticalStrut(10));
        center.add(backButton);

        add(center, BorderLayout.CENTER);

        openPackButton.addActionListener(this);
        backButton.addActionListener(this);

        refresh();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == openPackButton) {
            controller.openPack();
            // Your main JFrame will switch to the OpenPackView after this
        }
        else if (src == backButton && onBack != null) {
            onBack.run();
        }
    }

    public void refresh() {
        OpenPackState state = viewModel.getState();
        if (state != null) {
            currencyLabel.setText("Currency: " + state.getRemainingCurrency());
        }
    }
}

package frameworks_and_drivers;

import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;
import interface_adapters.open_pack.OpenPackController;

import javax.swing.*;
import java.awt.*;

public class PreOpenPackView extends JPanel {

    private OpenPackController controller;

    private final JLabel currencyLabel = new JLabel("Currency: 0", SwingConstants.LEFT);
    private JButton openPackButton;

    public PreOpenPackView(OpenPackViewModel viewModel,
                           ViewManager viewManager) {

        setLayout(new BorderLayout());
        setBackground(new Color(225, 235, 245));

        viewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                OpenPackState state = (OpenPackState) evt.getNewValue();
                updateCurrency(state.getRemainingCurrency());
            }
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        currencyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        currencyLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> viewManager.showPreOpenPack());

        topBar.add(currencyLabel, BorderLayout.WEST);
        topBar.add(backButton, BorderLayout.EAST);

        JPanel cardsContainer = new JPanel(new GridBagLayout());
        cardsContainer.setOpaque(false);

        JPanel threeCards = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        threeCards.setOpaque(false);

        threeCards.add(createPackCard(140, 200));
        threeCards.add(createPackCard(180, 260));
        threeCards.add(createPackCard(140, 200));

        cardsContainer.add(threeCards);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        openPackButton = new JButton("Open Pack");
        openPackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openPackButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel costLabel = new JLabel("1,000 to open a pack", SwingConstants.CENTER);
        costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        costLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        bottomPanel.add(openPackButton);
        bottomPanel.add(costLabel);

        add(topBar, BorderLayout.NORTH);
        add(cardsContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /** NEW: controller injection */
    public void setController(OpenPackController controller) {
        this.controller = controller;

        for (var l : openPackButton.getActionListeners())
            openPackButton.removeActionListener(l);

        openPackButton.addActionListener(e -> controller.openPack());
    }

    private JPanel createPackCard(int width, int height) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(width, height));
        card.setBackground(new Color(230, 230, 230));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setLayout(new GridBagLayout());
        card.add(new JLabel("UofTCG"));
        return card;
    }

    public void updateCurrency(int amount) {
        currencyLabel.setText("Currency: " + amount);
    }
}
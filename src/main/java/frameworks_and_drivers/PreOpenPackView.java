package frameworks_and_drivers;

import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;
import interface_adapters.open_pack.OpenPackController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * PreOpenPackView
 * Shows:
 *  Currency
 *  Back button
 *  Three packs
 *  "Open Pack" button
 *  "1,000 to open a pack" label
 */
public class PreOpenPackView extends JPanel {

    private final OpenPackViewModel viewModel;
    private final Runnable onOpenPack;     // callback to switch screens
    private final Runnable onBack;         // callback to go back

    // UI Components
    private final JLabel currencyLabel = new JLabel("Currency: 0", SwingConstants.LEFT);
    private final JButton backButton = new JButton("Back");
    private final JButton openPackButton = new JButton("Open Pack");
    private final JLabel costLabel = new JLabel("1,000 to open a pack", SwingConstants.CENTER);

    public PreOpenPackView(OpenPackViewModel viewModel,
                           Runnable onOpenPack,
                           Runnable onBack) {

        this.viewModel = viewModel;
        this.onOpenPack = onOpenPack;
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setBackground(new Color(225, 235, 245)); // soft blue-ish

        this.viewModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName())) {
                    OpenPackState newState = (OpenPackState) evt.getNewValue();
                    updateCurrency(newState.getRemainingCurrency());
                }
            }
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        // Currency styling
        currencyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        currencyLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        // Back button styling
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.addActionListener(e -> onBack.run());

        topBar.add(currencyLabel, BorderLayout.WEST);
        topBar.add(backButton, BorderLayout.EAST);

        // packs
        JPanel cardsContainer = new JPanel();
        cardsContainer.setOpaque(false);
        cardsContainer.setLayout(new GridBagLayout());

        JPanel threeCards = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        threeCards.setOpaque(false);

        threeCards.add(createPackCard(140, 200)); // left
        threeCards.add(createPackCard(180, 260)); // middle (larger)
        threeCards.add(createPackCard(140, 200)); // right

        cardsContainer.add(threeCards);

        //open pack button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        openPackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openPackButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        openPackButton.setPreferredSize(new Dimension(200, 40));
        openPackButton.addActionListener(e -> onOpenPack.run()); // Option C: screen swap only

        costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        costLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        costLabel.setBorder(BorderFactory.createEmptyBorder(5,0,20,0));

        bottomPanel.add(openPackButton);
        bottomPanel.add(costLabel);

        // layout
        add(topBar, BorderLayout.NORTH);
        add(cardsContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a single pack placeholder card.
     */
    private JPanel createPackCard(int width, int height) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(width, height));
        card.setBackground(new Color(230, 230, 230));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setLayout(new GridBagLayout());

        JLabel label = new JLabel("UofTCG");
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        card.add(label);

        return card;
    }

    /** Update currency display */
    public void updateCurrency(int amount) {
        currencyLabel.setText("Currency: " + amount);
    }


}
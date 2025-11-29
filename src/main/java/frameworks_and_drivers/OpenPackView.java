package frameworks_and_drivers;

import entities.Pokemon;
import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class OpenPackView extends JPanel {

    private OpenPackController controller;
    private final OpenPackViewModel viewModel;
    private final ViewManager viewManager;

    private final JPanel cardsPanel = new JPanel();
    private final JLabel currencyLabel = new JLabel("Currency: 0");
    private final JButton openPackButton = new JButton("Open Pack");
    private final JButton nextButton = new JButton("Next");
    private final JLabel messageLabel = new JLabel("");
    private final JButton addCollectionButton = new JButton("Add to Collection");

    public OpenPackView(OpenPackViewModel viewModel, ViewManager viewManager) {
        this.viewModel = viewModel;
        this.viewManager = viewManager;

        setLayout(new BorderLayout());

        // Top
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");

        topPanel.add(currencyLabel);
        topPanel.add(openPackButton);
        topPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);

        // Center
        cardsPanel.setLayout(new GridLayout(1, 1));
        add(cardsPanel, BorderLayout.CENTER);

        // Bottom
        JPanel bottomPanel = new JPanel(new FlowLayout());
        messageLabel.setForeground(Color.RED);

        bottomPanel.add(messageLabel);
        bottomPanel.add(nextButton);
        bottomPanel.add(addCollectionButton);

        add(bottomPanel, BorderLayout.SOUTH);

        nextButton.setVisible(false);
        addCollectionButton.setVisible(false);

        nextButton.addActionListener(e -> revealNext());
        addCollectionButton.addActionListener(e -> finishCollection());
        backButton.addActionListener(e -> viewManager.showPreOpenPack());

        viewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                updateView((OpenPackState) evt.getNewValue());
            }
        });
    }

    /** Called by ViewManagerFrame */
    public void setController(OpenPackController controller) {
        this.controller = controller;

        for (var l : openPackButton.getActionListeners())
            openPackButton.removeActionListener(l);

        openPackButton.addActionListener(e -> controller.openPack());
    }

    private void revealNext() {
        OpenPackState oldState = viewModel.getState();
        OpenPackState newState = new OpenPackState(oldState);

        int nextIndex = oldState.getRevealIndex() + 1;

        if (nextIndex >= oldState.getOpenedCards().size()) {
            newState.setRevealMode(false);   // Switch to summary
        } else {
            newState.setRevealIndex(nextIndex);
            newState.setRevealMode(true);    // Stay in reveal mode
        }

        viewModel.setState(newState);
    }

    private void finishCollection() {
        messageLabel.setText("Cards added to your collection!");
        addCollectionButton.setVisible(false);
        viewManager.showPreOpenPack();
    }

    public void updateView(OpenPackState state) {
        currencyLabel.setText("Currency: " + state.getRemainingCurrency());
        cardsPanel.removeAll();

        List<Pokemon> opened = state.getOpenedCards();

        // ========== REVEAL MODE ==========
        if (state.isRevealMode()) {
            nextButton.setVisible(true);
            addCollectionButton.setVisible(false);

            int idx = state.getRevealIndex();
            Pokemon p = opened.get(idx);

            cardsPanel.add(makeCardPanel(p));

            boolean isDup = state.getDuplicateFlags().get(idx);
            messageLabel.setText(isDup ? "Duplicate!" : "NEW card!");

            // ⭐ FIX: Always allow next button to be clicked
            nextButton.setEnabled(true);
            openPackButton.setEnabled(false);

        } else {
            // ========== SUMMARY MODE ==========
            for (Pokemon p : opened) {
                cardsPanel.add(makeCardPanel(p));
            }

            nextButton.setVisible(false);
            addCollectionButton.setVisible(true);
            messageLabel.setText("Pack summary");
            openPackButton.setEnabled(false);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel makeCardPanel(Pokemon pokemon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        card.setBackground(Color.WHITE);

        JLabel spriteLabel;

        try {
            ImageIcon icon = new ImageIcon(new URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            spriteLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            spriteLabel = new JLabel("[missing sprite]", SwingConstants.CENTER);
        }

        String text = pokemon.isShiny() ? "⭐ Shiny " + pokemon.getName() : pokemon.getName();
        JLabel name = new JLabel(text, SwingConstants.CENTER);

        card.add(spriteLabel, BorderLayout.CENTER);
        card.add(name, BorderLayout.SOUTH);

        return card;
    }
}

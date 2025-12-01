package frameworks_and_drivers.open_pack;

import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;
import entities.Pokemon;
import org.jetbrains.annotations.NotNull;
import view.ViewManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class OpenPackView extends JPanel {

    private OpenPackController controller;
    private final OpenPackViewModel viewModel;
    private final ViewManager viewManager;

    private final JPanel cardsPanel = new JPanel();
    private final JButton openPackButton = new JButton("Open Pack");
    private final JButton nextButton = new JButton("Next");
    private final JLabel messageLabel = new JLabel("");
    private final JButton addCollectionButton = new JButton("Add to Collection");
    private final JButton backButton = new JButton("Back");


    public OpenPackView(OpenPackViewModel viewModel, ViewManager viewManager) {
        this.viewModel = viewModel;
        this.viewManager = viewManager;

        setLayout(new BorderLayout());

        // Top
        JPanel topPanel = new JPanel(new FlowLayout());

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

    /** Wiring controller at runtime */
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
            newState.setRevealMode(false);
        } else {
            newState.setRevealIndex(nextIndex);
            newState.setRevealMode(true);
        }

        viewModel.setState(newState);
    }

    private void finishCollection() {
        messageLabel.setText("Cards added to your collection!");
        addCollectionButton.setVisible(false);
        viewManager.showPreOpenPack();
    }

    public void updateView(OpenPackState state) {
        cardsPanel.removeAll();

        List<Pokemon> opened = state.getOpenedCards();

        if (state.isRevealMode()) {
            backButton.setVisible(false);
            nextButton.setVisible(true);
            addCollectionButton.setVisible(false);

            int idx = state.getRevealIndex();
            Pokemon p = opened.get(idx);
            boolean isDup = state.getDuplicateFlags().get(idx);

            cardsPanel.add(makeCardPanel(p, isDup));
            messageLabel.setText(isDup ? "Duplicate! (+ $50)" : "NEW card!");

            nextButton.setEnabled(true);
            openPackButton.setEnabled(false);

        } else {
            for (int i = 0; i < opened.size(); i++) {
                Pokemon p = opened.get(i);
                boolean isDup = state.getDuplicateFlags().get(i);
                cardsPanel.add(makeCardPanel(p, isDup));
            }

            nextButton.setVisible(false);
            addCollectionButton.setVisible(true);
            messageLabel.setText("Pack summary");
            openPackButton.setEnabled(false);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel makeCardPanel(Pokemon pokemon, boolean isDuplicate) {

        JPanel card = getJPanel(pokemon);

        JLabel spriteLabel;

        try {
            ImageIcon icon = new ImageIcon(new URL(pokemon.getSpriteUrl()));
            Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            spriteLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            spriteLabel = new JLabel("[missing sprite]", SwingConstants.CENTER);
        }

        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(245, 245, 245));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        if (isDuplicate) {
            statusLabel.setText("DUPLICATE! (+$50)");
            statusLabel.setForeground(new Color(180, 50, 50));
        } else {
            statusLabel.setText("NEW CARD!");
            statusLabel.setForeground(new Color(50, 130, 80));
        }

        String cleanName = pokemon.getName().replace("-", " ").toUpperCase();
        String text = pokemon.isShiny() ? "⭐ SHINY " + cleanName : cleanName;

        JLabel name = new JLabel(text, SwingConstants.CENTER);
        name.setFont(new Font("SansSerif", Font.BOLD, 18));
        name.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));
        name.setOpaque(true);
        name.setBackground(new Color(245, 245, 245));

        card.add(statusLabel, BorderLayout.NORTH);
        card.add(spriteLabel, BorderLayout.CENTER);
        card.add(name, BorderLayout.SOUTH);

        return card;
    }

    @NotNull
    private static JPanel getJPanel(Pokemon pokemon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        card.setBackground(Color.WHITE);

        if (pokemon.isShiny()) {
            card.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 4));

            Timer shine = new Timer(300, null);
            shine.addActionListener(e -> {
                if (card.getBorder() == null) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 4));
                } else {
                    card.setBorder(null);
                }
            });
            shine.start();
        }
        return card;
    }
}

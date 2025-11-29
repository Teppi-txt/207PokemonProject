package frameworks_and_drivers;

import entities.Pokemon;
import pokeapi.JSONLoader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

public class BattleSetupView extends JFrame {

    @FunctionalInterface
    public interface BattleStartHandler {
        void start(String player1Name, String player2Name, String deck1Key, String deck2Key);
    }

    private final Map<String, List<String>> deckOptions;
    private final BattleStartHandler startHandler;

    private JTextField player1NameField;
    private JTextField player2NameField;
    private JComboBox<String> player1DeckBox;
    private JComboBox<String> player2DeckBox;
    private JPanel player1DeckPreview;
    private JPanel player2DeckPreview;

    public BattleSetupView(Map<String, List<String>> deckOptions, BattleStartHandler startHandler) {
        this.deckOptions = deckOptions;
        this.startHandler = startHandler;
        initialize();
    }

    private void initialize() {
        setTitle("Pokemon Battle Setup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 500);
        setLocationRelativeTo(null);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout(10, 10));
        container.setBorder(new EmptyBorder(10, 10, 10, 10));
        container.setBackground(Color.WHITE);

        JPanel playersPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        playersPanel.setBorder(new TitledBorder("Players"));
        playersPanel.setBackground(Color.WHITE);
        playersPanel.add(buildPlayerPanel("Player 1", true));
        playersPanel.add(buildPlayerPanel("Player 2", false));

        JButton startButton = new JButton("Start Battle");
        startButton.setPreferredSize(new Dimension(160, 40));
        startButton.addActionListener(e -> fireStartBattle());

        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        footer.add(startButton);

        container.add(playersPanel, BorderLayout.CENTER);
        container.add(footer, BorderLayout.SOUTH);
        add(container, BorderLayout.CENTER);
    }

    private JPanel buildPlayerPanel(String title, boolean firstPlayer) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(title));
        panel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel("Name");
        JTextField nameField = new JTextField(firstPlayer ? "Ash" : "Gary");
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        if (firstPlayer) {
            player1NameField = nameField;
        } else {
            player2NameField = nameField;
        }

        JLabel deckLabel = new JLabel("Choose Deck");
        JComboBox<String> deckBox = new JComboBox<>(deckOptions.keySet().toArray(new String[0]));
        deckBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        if (firstPlayer) {
            player1DeckBox = deckBox;
        } else {
            player2DeckBox = deckBox;
        }

        JPanel deckPreview = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        deckPreview.setBackground(new Color(248, 248, 248));
        deckPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        updateDeckPreview(deckPreview, (String) deckBox.getSelectedItem());
        deckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateDeckPreview(deckPreview, (String) e.getItem());
            }
        });

        if (firstPlayer) {
            player1DeckPreview = deckPreview;
        } else {
            player2DeckPreview = deckPreview;
        }

        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(deckLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(deckBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Deck Preview"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JScrollPane(deckPreview));

        return panel;
    }

    private void updateDeckPreview(JPanel previewPanel, String deckKey) {
        previewPanel.removeAll();
        List<String> deck = deckOptions.get(deckKey);
        if (deck == null || deck.isEmpty()) {
            previewPanel.add(new JLabel("No Pokemon in this deck."));
        } else {
            for (String name : deck) {
                JLabel label = new JLabel(name, createPlaceholderIcon(80, 80), JLabel.CENTER);
                label.setVerticalTextPosition(JLabel.BOTTOM);
                label.setHorizontalTextPosition(JLabel.CENTER);
                label.setToolTipText(name);
                loadSpriteAsync(label, name, 64, 64);
                previewPanel.add(label);
            }
        }
        previewPanel.revalidate();
        previewPanel.repaint();
    }

    private void fireStartBattle() {
        if (startHandler == null) {
            return;
        }
        String p1Name = player1NameField != null ? player1NameField.getText().trim() : "";
        String p2Name = player2NameField != null ? player2NameField.getText().trim() : "";
        String deck1 = player1DeckBox != null && player1DeckBox.getSelectedItem() != null
                ? player1DeckBox.getSelectedItem().toString()
                : null;
        String deck2 = player2DeckBox != null && player2DeckBox.getSelectedItem() != null
                ? player2DeckBox.getSelectedItem().toString()
                : null;
        startHandler.start(p1Name, p2Name, deck1, deck2);
    }

    private void loadSpriteAsync(JLabel label, String pokemonName, int width, int height) {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    String spriteUrl = findSpriteUrl(pokemonName);
                    if (spriteUrl == null) {
                        return null;
                    }
                    Image img = ImageIO.read(new URL(spriteUrl));
                    if (img != null) {
                        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    }
                } catch (Exception ignored) {
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        label.setIcon(icon);
                    }
                } catch (Exception ignored) {
                }
            }
        };
        worker.execute();
    }

    private String findSpriteUrl(String pokemonName) {
        return JSONLoader.allPokemon.stream()
                .filter(p -> p.getName().equalsIgnoreCase(pokemonName))
                .findFirst()
                .map(Pokemon::getSpriteUrl)
                .orElse(null);
    }

    private ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(new Color(230, 230, 230));
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(0, 0, width - 1, height - 1);
        g2.dispose();
        return new ImageIcon(img);
    }
}

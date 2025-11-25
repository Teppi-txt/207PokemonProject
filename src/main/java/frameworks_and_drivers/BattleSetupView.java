package frameworks_and_drivers;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Map;

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
    private JTextArea player1DeckPreview;
    private JTextArea player2DeckPreview;

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

        JTextArea deckPreview = new JTextArea(8, 20);
        deckPreview.setEditable(false);
        deckPreview.setLineWrap(true);
        deckPreview.setWrapStyleWord(true);
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

    private void updateDeckPreview(JTextArea preview, String deckKey) {
        List<String> deck = deckOptions.get(deckKey);
        if (deck == null || deck.isEmpty()) {
            preview.setText("No Pokemon in this deck.");
            return;
        }
        preview.setText(String.join(", ", deck));
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
}

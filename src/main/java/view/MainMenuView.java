package view;

import entities.User;
import interface_adapters.ViewManagerModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Simple, clean main menu view.
 */
public class MainMenuView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "main_menu";
    private static final String POKEMON_LOGO_PATH = "src/assets/pokemon.png";

    private final User user;
    private JLabel currencyLabel;
    private JLabel pokemonCountLabel;
    private Runnable onCollectionClick;
    private Runnable onOpenPackClick;
    private Runnable onBattleAIClick;
    private Runnable onBattlePlayerClick;

    public MainMenuView(ViewManagerModel viewManagerModel, User user) {
        this.user = user;
        viewManagerModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Pokemon Logo
        try {
            BufferedImage logoImage = ImageIO.read(new File(POKEMON_LOGO_PATH));
            int logoWidth = 300;
            int logoHeight = (int) ((double) logoImage.getHeight() / logoImage.getWidth() * logoWidth);
            Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(logoLabel);
        } catch (Exception e) {
            JLabel title = new JLabel("POKEMON");
            title.setFont(new Font("SansSerif", Font.BOLD, 48));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(title);
        }

        panel.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel("BATTLE GAME");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        panel.add(Box.createVerticalStrut(30));

        // User info
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(248, 248, 248));
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.setMaximumSize(new Dimension(250, 90));

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(nameLabel);

        userPanel.add(Box.createVerticalStrut(6));

        currencyLabel = new JLabel("$ " + user.getCurrency());
        currencyLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        currencyLabel.setForeground(new Color(80, 80, 80));
        currencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(currencyLabel);

        pokemonCountLabel = new JLabel("Pokemon: " + user.getOwnedPokemon().size());
        pokemonCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pokemonCountLabel.setForeground(new Color(80, 80, 80));
        pokemonCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(pokemonCountLabel);

        panel.add(userPanel);

        panel.add(Box.createVerticalStrut(30));

        // Menu buttons
        JButton collectionBtn = createMenuButton("VIEW COLLECTION");
        collectionBtn.addActionListener(e -> {
            if (onCollectionClick != null) onCollectionClick.run();
        });
        panel.add(collectionBtn);

        panel.add(Box.createVerticalStrut(10));

        JButton openPackBtn = createMenuButton("OPEN PACK");
        openPackBtn.addActionListener(e -> {
            if (onOpenPackClick != null) onOpenPackClick.run();
        });
        panel.add(openPackBtn);

        panel.add(Box.createVerticalStrut(10));

        JButton battleAIBtn = createMenuButton("BATTLE vs AI");
        battleAIBtn.addActionListener(e -> {
            if (onBattleAIClick != null) onBattleAIClick.run();
        });
        panel.add(battleAIBtn);

        panel.add(Box.createVerticalStrut(10));

        JButton battlePlayerBtn = createMenuButton("BATTLE vs PLAYER");
        battlePlayerBtn.addActionListener(e -> {
            if (onBattlePlayerClick != null) onBattlePlayerClick.run();
        });
        panel.add(battlePlayerBtn);

        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void refreshUserInfo() {
        currencyLabel.setText("$ " + user.getCurrency());
        pokemonCountLabel.setText("Pokemon: " + user.getOwnedPokemon().size());
    }

    public void setOnCollectionClick(Runnable callback) {
        this.onCollectionClick = callback;
    }

    public void setOnOpenPackClick(Runnable callback) {
        this.onOpenPackClick = callback;
    }

    public void setOnBattleAIClick(Runnable callback) {
        this.onBattleAIClick = callback;
    }

    public void setOnBattlePlayerClick(Runnable callback) {
        this.onBattlePlayerClick = callback;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state") && VIEW_NAME.equals(evt.getNewValue())) {
            refreshUserInfo();
        }
    }
}

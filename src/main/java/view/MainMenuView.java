package view;

import entities.User;
import interface_adapters.ViewManagerModel;
import interface_adapters.ui.RetroButton;
import interface_adapters.ui.UIStyleConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Retro Pokemon-themed main menu view.
 * Features pixel art aesthetic with Pokemon logo and Charizard images.
 */
public class MainMenuView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "main_menu";
    private static final String POKEMON_LOGO_PATH = "src/assets/pokemon.png";
    private static final String CHARIZARD_PATH = "src/assets/charizard.png";

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
        setBackground(UIStyleConstants.DARK_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Gradient background - dark blue to red (Pokemon theme)
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 40, 80),
                    0, getHeight(), UIStyleConstants.PRIMARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Add pixel-style border at bottom
                g2d.setColor(UIStyleConstants.SECONDARY_COLOR);
                for (int i = 0; i < 4; i++) {
                    g2d.drawLine(0, getHeight() - 1 - i, getWidth(), getHeight() - 1 - i);
                }
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 200));

        // Pokemon Logo
        try {
            BufferedImage logoImage = ImageIO.read(new File(POKEMON_LOGO_PATH));
            int logoWidth = 350;
            int logoHeight = (int) ((double) logoImage.getHeight() / logoImage.getWidth() * logoWidth);
            Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(logoLabel);
        } catch (Exception e) {
            // Fallback to text title
            JLabel title = new JLabel("POKEMON");
            title.setFont(new Font("Courier New", Font.BOLD, 48));
            title.setForeground(UIStyleConstants.SECONDARY_COLOR);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(title);
        }

        panel.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("BATTLE GAME");
        subtitle.setFont(new Font("Courier New", Font.BOLD, 24));
        subtitle.setForeground(UIStyleConstants.TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIStyleConstants.DARK_BG);

        // Left side - Charizard image
        JPanel leftPanel = createCharizardPanel();
        container.add(leftPanel, BorderLayout.WEST);

        // Center - Menu
        JPanel menuPanel = createMenuPanel();
        container.add(menuPanel, BorderLayout.CENTER);

        // Right side - User info
        JPanel rightPanel = createUserInfoPanel();
        container.add(rightPanel, BorderLayout.EAST);

        return container;
    }

    private JPanel createCharizardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setLayout(new GridBagLayout());

        try {
            BufferedImage charizardImage = ImageIO.read(new File(CHARIZARD_PATH));
            int imgWidth = 200;
            int imgHeight = (int) ((double) charizardImage.getHeight() / charizardImage.getWidth() * imgWidth);
            Image scaledImg = charizardImage.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            panel.add(imgLabel);
        } catch (Exception e) {
            // No image fallback
        }

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(UIStyleConstants.DARK_BG);

        // Retro menu box
        JPanel menuBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int w = getWidth();
                int h = getHeight();
                int border = 6;

                // Outer border (white)
                g2d.setColor(UIStyleConstants.TEXT_LIGHT);
                g2d.fillRect(0, 0, w, h);

                // Dark border
                g2d.setColor(UIStyleConstants.BORDER_DARK);
                g2d.fillRect(border, border, w - border * 2, h - border * 2);

                // Inner background (cream color like Pokemon menus)
                g2d.setColor(UIStyleConstants.MENU_BG);
                g2d.fillRect(border * 2, border * 2, w - border * 4, h - border * 4);

                // 3D effect - light top-left
                g2d.setColor(Color.WHITE);
                g2d.drawLine(border * 2, border * 2, w - border * 2, border * 2);
                g2d.drawLine(border * 2, border * 2, border * 2, h - border * 2);

                // 3D effect - dark bottom-right
                g2d.setColor(UIStyleConstants.SHADOW_COLOR);
                g2d.drawLine(w - border * 2, border * 2, w - border * 2, h - border * 2);
                g2d.drawLine(border * 2, h - border * 2, w - border * 2, h - border * 2);
            }
        };
        menuBox.setLayout(new BoxLayout(menuBox, BoxLayout.Y_AXIS));
        menuBox.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        menuBox.setPreferredSize(new Dimension(380, 400));

        // Menu title
        JLabel menuTitle = new JLabel("- MAIN MENU -");
        menuTitle.setFont(UIStyleConstants.HEADING_FONT);
        menuTitle.setForeground(UIStyleConstants.TEXT_PRIMARY);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuBox.add(menuTitle);

        menuBox.add(Box.createVerticalStrut(25));

        // Menu buttons
        RetroButton collectionBtn = new RetroButton("VIEW COLLECTION");
        collectionBtn.setButtonColor(UIStyleConstants.POKEMON_BLUE);
        collectionBtn.setPreferredSize(new Dimension(280, 55));
        collectionBtn.setMaximumSize(new Dimension(280, 55));
        collectionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        collectionBtn.addActionListener(e -> {
            if (onCollectionClick != null) onCollectionClick.run();
        });
        menuBox.add(collectionBtn);

        menuBox.add(Box.createVerticalStrut(15));

        RetroButton openPackBtn = new RetroButton("OPEN PACK");
        openPackBtn.setButtonColor(UIStyleConstants.SECONDARY_COLOR);
        openPackBtn.setPreferredSize(new Dimension(280, 55));
        openPackBtn.setMaximumSize(new Dimension(280, 55));
        openPackBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        openPackBtn.addActionListener(e -> {
            if (onOpenPackClick != null) onOpenPackClick.run();
        });
        menuBox.add(openPackBtn);

        menuBox.add(Box.createVerticalStrut(15));

        RetroButton battleAIBtn = new RetroButton("BATTLE vs AI");
        battleAIBtn.setButtonColor(UIStyleConstants.PRIMARY_COLOR);
        battleAIBtn.setPreferredSize(new Dimension(280, 55));
        battleAIBtn.setMaximumSize(new Dimension(280, 55));
        battleAIBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        battleAIBtn.addActionListener(e -> {
            if (onBattleAIClick != null) onBattleAIClick.run();
        });
        menuBox.add(battleAIBtn);

        menuBox.add(Box.createVerticalStrut(15));

        RetroButton battlePlayerBtn = new RetroButton("BATTLE vs PLAYER");
        battlePlayerBtn.setButtonColor(new Color(80, 160, 80));
        battlePlayerBtn.setPreferredSize(new Dimension(280, 55));
        battlePlayerBtn.setMaximumSize(new Dimension(280, 55));
        battlePlayerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        battlePlayerBtn.addActionListener(e -> {
            if (onBattlePlayerClick != null) onBattlePlayerClick.run();
        });
        menuBox.add(battlePlayerBtn);

        outerPanel.add(menuBox);
        return outerPanel;
    }

    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setLayout(new GridBagLayout());

        // User info box
        JPanel infoBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int w = getWidth();
                int h = getHeight();
                int border = 4;

                // Border
                g2d.setColor(UIStyleConstants.SECONDARY_COLOR);
                g2d.fillRect(0, 0, w, h);

                // Inner background
                g2d.setColor(new Color(40, 40, 60));
                g2d.fillRect(border, border, w - border * 2, h - border * 2);
            }
        };
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoBox.setPreferredSize(new Dimension(200, 150));

        JLabel trainerLabel = new JLabel("TRAINER");
        trainerLabel.setFont(UIStyleConstants.BODY_FONT);
        trainerLabel.setForeground(UIStyleConstants.SECONDARY_COLOR);
        trainerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoBox.add(trainerLabel);

        infoBox.add(Box.createVerticalStrut(5));

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(UIStyleConstants.HEADING_FONT);
        nameLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoBox.add(nameLabel);

        infoBox.add(Box.createVerticalStrut(15));

        currencyLabel = new JLabel("$ " + user.getCurrency());
        currencyLabel.setFont(UIStyleConstants.BODY_FONT);
        currencyLabel.setForeground(UIStyleConstants.SECONDARY_COLOR);
        currencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoBox.add(currencyLabel);

        infoBox.add(Box.createVerticalStrut(5));

        pokemonCountLabel = new JLabel("POKeMON: " + user.getOwnedPokemon().size());
        pokemonCountLabel.setFont(UIStyleConstants.BODY_FONT);
        pokemonCountLabel.setForeground(UIStyleConstants.TEXT_LIGHT);
        pokemonCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoBox.add(pokemonCountLabel);

        panel.add(infoBox);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Yellow stripe at top
                g2d.setColor(UIStyleConstants.SECONDARY_COLOR);
                for (int i = 0; i < 4; i++) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        panel.setBackground(UIStyleConstants.DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel footer = new JLabel("CSC207 Pokemon Project - Press START!");
        footer.setFont(UIStyleConstants.SMALL_FONT);
        footer.setForeground(UIStyleConstants.TEXT_SECONDARY);
        panel.add(footer);

        return panel;
    }

    /**
     * Refreshes the user info display with current values.
     */
    public void refreshUserInfo() {
        currencyLabel.setText("$ " + user.getCurrency());
        pokemonCountLabel.setText("POKeMON: " + user.getOwnedPokemon().size());
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
        // Refresh user info when this view becomes active
        if (evt.getPropertyName().equals("state") && VIEW_NAME.equals(evt.getNewValue())) {
            refreshUserInfo();
        }
    }
}

package view;

import entities.User;
import interface_adapters.ViewManagerModel;
import interface_adapters.ui.StyledButton;
import interface_adapters.ui.UIStyleConstants;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Main menu view - the home screen of the Pokemon application.
 * Displays user info and navigation buttons to all features.
 */
public class MainMenuView extends JPanel implements PropertyChangeListener {

    public static final String VIEW_NAME = "main_menu";

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
        setBackground(UIStyleConstants.BACKGROUND);

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMenuPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIStyleConstants.PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JLabel title = new JLabel("POKEMON BATTLE GAME");
        title.setFont(UIStyleConstants.EXTRA_LARGE_FONT);
        title.setForeground(UIStyleConstants.SECONDARY_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("Collect, Build, Battle!");
        subtitle.setFont(UIStyleConstants.HEADING_FONT);
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(UIStyleConstants.BACKGROUND);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(UIStyleConstants.BACKGROUND);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // User info panel
        JPanel userInfoPanel = createUserInfoPanel();
        userInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(userInfoPanel);

        menuPanel.add(Box.createVerticalStrut(30));

        // Menu buttons
        JButton collectionBtn = createMenuButton("View Collection", "Browse your Pokemon collection", true);
        collectionBtn.addActionListener(e -> {
            if (onCollectionClick != null) onCollectionClick.run();
        });
        menuPanel.add(collectionBtn);
        menuPanel.add(Box.createVerticalStrut(15));

        // Open Pack button - disabled placeholder for future implementation
        JButton openPackBtn = createMenuButton("Open Pack", "Coming Soon - Spend currency to get new Pokemon", false);
        openPackBtn.setEnabled(false);
        openPackBtn.addActionListener(e -> {
            if (onOpenPackClick != null) onOpenPackClick.run();
        });
        menuPanel.add(openPackBtn);
        menuPanel.add(Box.createVerticalStrut(15));

        JButton battleAIBtn = createMenuButton("Battle AI", "Battle against computer opponent", true);
        battleAIBtn.addActionListener(e -> {
            if (onBattleAIClick != null) onBattleAIClick.run();
        });
        menuPanel.add(battleAIBtn);
        menuPanel.add(Box.createVerticalStrut(15));

        JButton battlePlayerBtn = createMenuButton("Battle Player", "Local 2-player battle", true);
        battlePlayerBtn.addActionListener(e -> {
            if (onBattlePlayerClick != null) onBattlePlayerClick.run();
        });
        menuPanel.add(battlePlayerBtn);

        container.add(menuPanel);
        return container;
    }

    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIStyleConstants.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "!");
        welcomeLabel.setFont(UIStyleConstants.HEADING_FONT);
        welcomeLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcomeLabel);

        panel.add(Box.createVerticalStrut(10));

        currencyLabel = new JLabel("Currency: " + user.getCurrency());
        currencyLabel.setFont(UIStyleConstants.BODY_FONT);
        currencyLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        currencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(currencyLabel);

        pokemonCountLabel = new JLabel("Pokemon Owned: " + user.getOwnedPokemon().size());
        pokemonCountLabel.setFont(UIStyleConstants.BODY_FONT);
        pokemonCountLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        pokemonCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pokemonCountLabel);

        return panel;
    }

    private JButton createMenuButton(String title, String description, boolean enabled) {
        JPanel buttonContent = new JPanel();
        buttonContent.setLayout(new BoxLayout(buttonContent, BoxLayout.Y_AXIS));
        buttonContent.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyleConstants.HEADING_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(UIStyleConstants.SMALL_FONT);
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        StyledButton button = new StyledButton("");
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.add(Box.createVerticalStrut(10));
        button.add(titleLabel);
        button.add(Box.createVerticalStrut(5));
        button.add(descLabel);
        button.add(Box.createVerticalStrut(10));

        button.setPreferredSize(new Dimension(400, 70));
        button.setMaximumSize(new Dimension(400, 70));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setEnabled(enabled);

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyleConstants.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JLabel footer = new JLabel("CSC207 Pokemon Project");
        footer.setFont(UIStyleConstants.SMALL_FONT);
        footer.setForeground(UIStyleConstants.TEXT_SECONDARY);
        panel.add(footer);

        return panel;
    }

    /**
     * Refreshes the user info display with current values.
     */
    public void refreshUserInfo() {
        currencyLabel.setText("Currency: " + user.getCurrency());
        pokemonCountLabel.setText("Pokemon Owned: " + user.getOwnedPokemon().size());
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

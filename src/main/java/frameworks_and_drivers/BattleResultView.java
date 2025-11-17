package frameworks_and_drivers;

import interface_adapters.battle_ai.BattleAIController;
import interface_adapters.ui.*;

import javax.swing.*;
import java.awt.*;

/**
 * View displayed after battle ends.
 * Shows winner, rewards, and options to battle again or exit.
 */
public class BattleResultView extends JFrame {

    private final String winner;
    private final BattleAIController controller;

    public BattleResultView(String winner, BattleAIController controller) {
        this.winner = winner;
        this.controller = controller;

        setTitle("Battle Results");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Result Banner
        JPanel bannerPanel = createBannerPanel();
        add(bannerPanel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createBannerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Determine if player won
        boolean playerWon = !winner.equals("AI Opponent");
        Color bgColor = playerWon ?
                new Color(255, 215, 0) : // Gold for victory
                new Color(180, 180, 200); // Gray-blue for defeat

        panel.setBackground(bgColor);

        String resultText = playerWon ? "🏆 VICTORY!" : "😔 DEFEAT";
        JLabel resultLabel = new JLabel(resultText);
        resultLabel.setFont(UIStyleConstants.EXTRA_LARGE_FONT);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(resultLabel);

        panel.add(Box.createVerticalStrut(10));

        JLabel winnerLabel = new JLabel("Winner: " + winner);
        winnerLabel.setFont(UIStyleConstants.TITLE_FONT);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(winnerLabel);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel statsTitle = new JLabel("Battle Summary");
        statsTitle.setFont(UIStyleConstants.HEADING_FONT);
        statsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statsTitle);

        panel.add(Box.createVerticalStrut(20));

        // Rewards
        boolean playerWon = !winner.equals("AI Opponent");
        int currency = playerWon ? 500 : 100;

        JPanel rewardsPanel = new JPanel();
        rewardsPanel.setLayout(new BoxLayout(rewardsPanel, BoxLayout.Y_AXIS));
        rewardsPanel.setBackground(new Color(255, 250, 205));
        rewardsPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        rewardsPanel.setMaximumSize(new Dimension(400, 100));

        JLabel rewardTitle = new JLabel("💰 Rewards");
        rewardTitle.setFont(UIStyleConstants.HEADING_FONT);
        rewardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rewardsPanel.add(rewardTitle);

        JLabel currencyLabel = new JLabel("+ " + currency + " Currency");
        currencyLabel.setFont(UIStyleConstants.TITLE_FONT);
        currencyLabel.setForeground(playerWon ? new Color(0, 150, 0) : new Color(100, 100, 100));
        currencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rewardsPanel.add(currencyLabel);

        panel.add(rewardsPanel);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(UIStyleConstants.BACKGROUND);

        StyledButton battleAgainBtn = new StyledButton("⚔ BATTLE AGAIN", UIStyleConstants.PRIMARY_COLOR);
        battleAgainBtn.setPreferredSize(new Dimension(180, 48));
        battleAgainBtn.addActionListener(e -> battleAgain());
        panel.add(battleAgainBtn);

        StyledButton exitBtn = new StyledButton("🏠 EXIT");
        exitBtn.setPreferredSize(new Dimension(150, 48));
        exitBtn.addActionListener(e -> dispose());
        panel.add(exitBtn);

        return panel;
    }

    private void battleAgain() {
        // TODO: Return to deck selection
        // For now, just close
        JOptionPane.showMessageDialog(this,
                "Battle Again feature coming soon!\nPlease restart the application.",
                "Feature Not Implemented", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}

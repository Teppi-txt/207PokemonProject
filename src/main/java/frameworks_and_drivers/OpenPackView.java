package frameworks_and_drivers;

import entities.Pokemon;
import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackState;
import interface_adapters.open_pack.OpenPackViewModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

public class OpenPackView extends JPanel implements ActionListener{

    private final OpenPackController controller;
    private final OpenPackViewModel viewModel;

    private boolean packOpened = false;
    private boolean addedToCollection = true;

    // Panels
    private final JPanel topPanel = new JPanel(new FlowLayout());
    private final JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 10, 10));
    private final JPanel bottomPanel = new JPanel(new FlowLayout());

    // Components
    private final JLabel currencyLabel = new JLabel("Currency: 0");
    private final JButton openPackButton = new JButton("Open Pack");
    private final JLabel messageLabel = new JLabel("");
    private final JButton addCollectionButton = new JButton("Add to Collection");
    private final JButton backButton = new JButton("Back");

    public OpenPackView(OpenPackController controller, OpenPackViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        setLayout(new BorderLayout());

        this.viewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                updateView((OpenPackState) evt.getNewValue());
            }
        });

        // currency and open pack and back
        topPanel.add(currencyLabel);
        topPanel.add(openPackButton);
        topPanel.add(backButton);

        //cards
        messageLabel.setForeground(Color.RED);
        bottomPanel.add(messageLabel);
        bottomPanel.add(addCollectionButton);


        add(topPanel, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button listener
        openPackButton.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == openPackButton){
            openPack();
        }
    }

    private void openPack(){
        controller.openPack();
    }

    public void updateView(OpenPackState state){
        if (state == null){
            return;
        }

        currencyLabel.setText("Currency: " + state.getRemainingCurrency());

        cardsPanel.removeAll();

        List<Pokemon> opened = state.getOpenedCards();
        if (opened != null){
            for (Pokemon pokemon : opened){
                cardsPanel.add(makeCardPanel(pokemon));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();

        if (state.getErrorMessage() != null) {
            messageLabel.setText(state.getErrorMessage());
        } else if (opened != null && !opened.isEmpty()) {
            messageLabel.setText("You opened a pack!");
        } else {
            messageLabel.setText("");
        }

    }

    private JPanel makeCardPanel(Pokemon pokemon){
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        cardPanel.setBackground(Color.WHITE);

        JLabel spriteLabel;

        try {
            // getRegularSpriteURL(), getShinySpriteURL(), getSpriteUrl()
            String spriteUrl = pokemon.getSpriteUrl();
            ImageIcon icon = new ImageIcon(new URL(spriteUrl));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            spriteLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception ex) {
            // Fallback if sprite can't be loaded
            spriteLabel = new JLabel("[no sprite]", SwingConstants.CENTER);
        }

        String labelText = pokemon.getName();
        if (pokemon.isShiny()) {
            labelText = "⭐ Shiny " + labelText;
        }

        JLabel nameLabel = new JLabel(labelText, SwingConstants.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 12f));

        cardPanel.add(spriteLabel, BorderLayout.CENTER);
        cardPanel.add(nameLabel, BorderLayout.SOUTH);

        return cardPanel;

    }


}



package frameworks_and_drivers;

import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackViewModel;

import javax.swing.*;

public class ViewManagerFrame extends JFrame implements ViewManager {

    private final PreOpenPackView preView;
    private final OpenPackView openView;

    public ViewManagerFrame(OpenPackViewModel vm, OpenPackController controller) {

        setTitle("UofTCG");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create PreOpenPackView
        preView = new PreOpenPackView(vm, controller, this::showOpenPack, this::showPreOpenPack);

        // Create OpenPackView
        openView = new OpenPackView(controller, vm, this);

        // Start on pre-open screen
        showPreOpenPack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void showPreOpenPack() {
        setContentPane(preView);
        revalidate();
        repaint();
    }

    @Override
    public void showOpenPack() {
        setContentPane(openView);
        revalidate();
        repaint();
    }
}

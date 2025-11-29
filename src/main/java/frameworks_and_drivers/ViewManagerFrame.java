package frameworks_and_drivers;

import interface_adapters.open_pack.OpenPackController;
import interface_adapters.open_pack.OpenPackViewModel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewManagerFrame extends JFrame implements ViewManager {

    private final PreOpenPackView preView;
    private final OpenPackView openView;
    private final Runnable onCloseCallback;

    public ViewManagerFrame(OpenPackViewModel vm, OpenPackController controller) {
        this(vm, controller, null);
    }

    public ViewManagerFrame(OpenPackViewModel vm, OpenPackController controller, Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;

        setTitle("Open Pack");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add window closing listener to trigger callback
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            }
        });

        // Create PreOpenPackView
        preView = new PreOpenPackView(vm, this);

        // Create OpenPackView
        openView = new OpenPackView(vm, this);

        // Start on pre-open screen
        showPreOpenPack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setController(OpenPackController controller) {
        openView.setController(controller);
        preView.setController(controller);
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

    @Override
    public void closeWindow() {
        dispose();
    }
}
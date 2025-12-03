package view;

import interface_adapters.ViewManagerModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The view manager for the Pokemon battler app
 */

public class ViewManager implements PropertyChangeListener {
    private final CardLayout cardLayout;
    private final JPanel views;
    private final ViewManagerModel viewManagerModel;

    public ViewManager(JPanel views, CardLayout cardLayout, ViewManagerModel viewManagerModel) {
        this.views = views;
        this.cardLayout = cardLayout;
        this.viewManagerModel = viewManagerModel;
        this.viewManagerModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            String viewName = (String) evt.getNewValue();
            cardLayout.show(views, viewName);
        }
    }

    public void showOpenPack() {
        viewManagerModel.setState("open_pack");
        viewManagerModel.firePropertyChanged();
    }

    public void showPreOpenPack() {
        viewManagerModel.setState("pre_open_pack");
        viewManagerModel.firePropertyChanged();
    }

    public void closeWindow() {
        viewManagerModel.setState("main_menu");
        viewManagerModel.firePropertyChanged();
    }
}
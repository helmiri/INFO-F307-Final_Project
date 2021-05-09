package be.ac.ulb.infof307.g06.views.settingsViews;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class SettingsViewController {
    //-------------- ATTRIBUTES ----------------
    private static final String hoverColor = "#95dbfa";
    private static final String defaultColor = "#4fc3f7";
    @FXML
    private Button profileBtn;
    @FXML
    private Button storageBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button aboutBtn;
    @FXML
    private Button helpBtn;
    @FXML
    private Button tagsBtn;
    @FXML
    private Pane subPane;
    private Button previousClicked;
    private ViewListener listener;

    private ObservableList<Node> currentChildren;

    //--------------- METHODS ----------------
    /**
     * The main method for button's events.
     *
     * @param actionEvent ActionEvent, the event.
     */
    @FXML
    private void events(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (profileBtn.equals(source)) {
            swapButtonStyles(profileBtn);
            setSubPaneScene(listener.getProfileScene());
            listener.showProfile();
        } else if (storageBtn.equals(source)) {
            swapButtonStyles(storageBtn);
            setSubPaneScene(listener.getStorageScene());
            listener.showStorage();
        } else if (aboutBtn.equals(source)) {
            swapButtonStyles(aboutBtn);
            setSubPaneScene(listener.getAboutScene());
            listener.showAbout();
        } else if (helpBtn.equals(source)) {
            swapButtonStyles(helpBtn);
            setSubPaneScene(listener.getHelpScene());
            listener.showHelp();
        } else if (tagsBtn.equals(source)) {
            swapButtonStyles(tagsBtn);
            setSubPaneScene(listener.getTagsScene());
            listener.showTags();
        } else if (backBtn.equals(source)) {
            swapButtonStyles(backBtn);
            listener.onBackButtonPressed();
        }
    }

    /**
     * Sets the sub pane scene.
     *
     * @param newAnchor AnchorPane, the anchorpane for the scene.
     */
    private void setSubPaneScene(AnchorPane newAnchor) {
        if (newAnchor == null) {
            return;
        }
        ObservableList<Node> children = subPane.getChildren();
        Scene newScene = new Scene(newAnchor);
        if (children.size() != 0 && currentChildren != null) {
            children.removeAll(currentChildren);
        }
        children.add(newScene.getRoot());
        children.addAll(newAnchor.getChildren());
        currentChildren = children;
    }

    /**
     * Highlights a button when the mouse is over it.
     *
     * @param mouseEvent MouseEvent, the mouse event to know when to highlight a button.
     */
    @FXML
    private void hover(MouseEvent mouseEvent) {
        setButtonStyle((Button) mouseEvent.getSource(), hoverColor);
    }

    /**
     * Sets style to a button.
     *
     * @param button Button, the button
     * @param hex String, the hexadecimal color.
     */
    private void setButtonStyle(Button button, String hex) {
        if (button == null) {
            return;
        }
        button.setStyle("-fx-background-color:" + hex + ";");
    }

    /**
     * Changes the style of the current button.
     *
     * @param current Button, the current button.
     */
    private void swapButtonStyles(Button current) {
        if (current.equals(previousClicked)) {
            return;
        }
        setButtonStyle(current, hoverColor);
        setButtonStyle(previousClicked, defaultColor);
        previousClicked = current;
    }

    /**
     * Unhighlight a button when we are not over it.
     *
     * @param mouseEvent MouseEvent, the event of the mouse.
     */
    @FXML
    private void exitHover(MouseEvent mouseEvent) {
        setIfNotClicked((Button) mouseEvent.getSource());
    }

    /**
     * Sets the color's button to default if not clicked.
     *
     * @param source Button, the button "selected"
     */
    private void setIfNotClicked(Button source) {
        if (previousClicked.equals(source)) {
            return;
        }
        setButtonStyle(source, defaultColor);
    }

    /**
     * Sets the default scene to the tags settings.
     */
    public void setDefaultScene() {
        profileBtn.fire();
    }

    //--------------- LISTENER ----------------
    /**
     * Sets the listener.
     *
     * @param newListener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener newListener) {
        listener = newListener;
    }

    public interface ViewListener {
        AnchorPane getTagsScene();

        AnchorPane getStorageScene();

        AnchorPane getHelpScene();

        AnchorPane getAboutScene();

        void showStorage();

        void showTags();

        void showHelp();

        void showAbout();

        void showProfile();

        void onBackButtonPressed();

        AnchorPane getProfileScene();
    }
}

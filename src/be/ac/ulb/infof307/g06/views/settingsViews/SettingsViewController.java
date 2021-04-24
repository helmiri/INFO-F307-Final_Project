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
    private final String hoverColor = "#95dbfa";
    private final String defaultColor = "#4fc3f7";
    @FXML
    private AnchorPane newAnchor;
    @FXML
    private Button languageBtn;
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

    public void events(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (languageBtn.equals(source)) {
            swapButtonStyles(languageBtn);
            setSubPaneScene(listener.getLanguageScene());
        } else if (storageBtn.equals(source)) {
            swapButtonStyles(storageBtn);
            setSubPaneScene(listener.getStorageScene());
            listener.showStorage();
        } else if (aboutBtn.equals(source)) {
            swapButtonStyles(aboutBtn);
            setSubPaneScene(listener.getAboutScene());
        } else if (helpBtn.equals(source)) {
            swapButtonStyles(helpBtn);
            setSubPaneScene(listener.getHelpScene());
        } else if (tagsBtn.equals(source)) {
            swapButtonStyles(tagsBtn);
            setSubPaneScene(listener.getTagsScene());
            listener.showTags();
        } else if (backBtn.equals(source)) {
            swapButtonStyles(backBtn);
            listener.onBackButtonPressed();
        }
    }

    public void setListener(ViewListener newListener) {
        listener = newListener;
    }

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

    public void hover(MouseEvent mouseEvent) {
        Object source = mouseEvent.getSource();
        if (languageBtn.equals(source)) {
            setButtonStyle(languageBtn, hoverColor);
        } else if (storageBtn.equals(source)) {
            setButtonStyle(storageBtn, hoverColor);
        } else if (aboutBtn.equals(source)) {
            setButtonStyle(aboutBtn, hoverColor);
        } else if (helpBtn.equals(source)) {
            setButtonStyle(helpBtn, hoverColor);
        } else if (tagsBtn.equals(source)) {
            setButtonStyle(tagsBtn, hoverColor);
        } else if (backBtn.equals(source)) {
            setButtonStyle(backBtn, hoverColor);
        }
    }

    private void setButtonStyle(Button button, String hex) {
        if (button == null) {
            return;
        }
        button.setStyle("-fx-background-color:" + hex + ";");
    }

    private void swapButtonStyles(Button current) {
        if (current == previousClicked) {
            return;
        }
        setButtonStyle(current, hoverColor);
        setButtonStyle(previousClicked, defaultColor);
        previousClicked = current;
    }

    public void exitHover(MouseEvent mouseEvent) {
        Object source = mouseEvent.getSource();
        if (languageBtn.equals(source)) {
            setIfNotClicked(languageBtn);
        } else if (storageBtn.equals(source)) {
            setIfNotClicked(storageBtn);
        } else if (aboutBtn.equals(source)) {
            setIfNotClicked(aboutBtn);
        } else if (helpBtn.equals(source)) {
            setIfNotClicked(helpBtn);
        } else if (tagsBtn.equals(source)) {
            setIfNotClicked(tagsBtn);
        } else if (backBtn.equals(source)) {
            setIfNotClicked(backBtn);
        }
    }

    private void setIfNotClicked(Button source) {
        if (previousClicked == source) {
            return;
        }
        setButtonStyle(source, defaultColor);
    }

    public void setDefaultScene() {
        tagsBtn.fire();
    }

    public interface ViewListener {
        AnchorPane getTagsScene();

        AnchorPane getStorageScene();

        AnchorPane getLanguageScene();

        AnchorPane getHelpScene();

        AnchorPane getAboutScene();

        void showStorage();

        void showTags();

        void showHelp();

        void showAbout();

        void onBackButtonPressed();
    }
}

package be.ac.ulb.infof307.g06.views.projectViews;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CodePromptViewController {
    //--------------- ATTRIBUTES ----------------
    @FXML
    private Text copyNotification;
    @FXML
    private TextField codeField;
    @FXML
    private Button okButton;
    @FXML
    private Hyperlink link;
    private ViewListener listener;
    private Stage stage;

    //--------------- METHODS ----------------

    /**
     * Initializes the stage
     *
     * @param newLink String, to set the text of the new link.
     * @param pane AnchorPane, the code prompt pane.
     */
    public void initialize(String newLink, AnchorPane pane) {
        Scene newScene = new Scene(pane);
        stage = new Stage();
        stage.setScene(newScene);
        link.setText(newLink);
        stage.showAndWait();
    }

    /**
     * The main method for button's events.
     *
     * @param actionEvent ActionEvent, the event.
     */
    public void events(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source.equals(okButton)) {
            listener.onOKClicked(codeField.getText());
            stage.close();
        } else if (source.equals(link)) {
            listener.onHyperlinkClicked(link.getText());
            link.setVisited(true);
            copyNotification.setVisible(true);
        }
    }

    //--------------- LISTENER ----------------
    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the controller.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener {
        void onOKClicked(String code);

        void onHyperlinkClicked(String url);
    }
}

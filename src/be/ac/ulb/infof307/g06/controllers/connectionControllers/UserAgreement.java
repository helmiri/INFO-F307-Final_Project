package be.ac.ulb.infof307.g06.controllers.connectionControllers;

import be.ac.ulb.infof307.g06.exceptions.WindowLoadException;
import be.ac.ulb.infof307.g06.views.connectionViews.UserAgreementViewController;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class UserAgreement implements UserAgreementViewController.ViewListener {

    private final Stage termsStage;
    private boolean accepted;

    public UserAgreement(Stage termsStage) {
        this.termsStage = termsStage;
    }

    /**
     * {@inheritDoc}
     */
    public void show() throws WindowLoadException {
        try {
            FXMLLoader loader = new FXMLLoader(UserAgreementViewController.class.getResource("UserAgreementView.fxml"));
            loader.load();
            UserAgreementViewController controller = loader.getController();
            controller.setListener(this);
            controller.show(termsStage);
        } catch (IOException e) {
            throw new WindowLoadException(e);
        }
    }

    /**
     * Set the boolean and close stage
     */
    @Override
    public void onConditionsAccepted() {
        termsStage.close();
        accepted = true;
    }

    /**
     * Set the boolean and close stage
     */
    @Override
    public void onConditionsDeclined() {
        termsStage.close();
        accepted = false;
    }

    /**
     * Getter
     *
     * @return true if the user accepted the agreement, false otherwise
     */
    public boolean termsAccepted() {
        return accepted;
    }
}

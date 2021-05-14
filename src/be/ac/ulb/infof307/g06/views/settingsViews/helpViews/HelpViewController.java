package be.ac.ulb.infof307.g06.views.settingsViews.helpViews;


import be.ac.ulb.infof307.g06.models.AlertWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Help section view controller
 */
public class HelpViewController {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private AnchorPane layout;
    @FXML
    private Button projectManagementHelpBtn;

    @FXML
    private Button storageHelpBtn;

    @FXML
    private Button exportImportHelpBtn;

    @FXML
    private Button calendarHelpBtn;

    @FXML
    private Button tagsHelpBtn;

    @FXML
    private Button profileHelpBtn;


    @FXML
    private Button playBtn;

    @FXML
    private Button stopBtn;

    @FXML
    private Button pauseBtn;
    
    @FXML
    private Button rewindBtn;

    @FXML
    private Button skipBtn;

    @FXML
    private Button verySlowSpeedBtn;

    @FXML
    private Button slowSpeedBtn;

    @FXML
    private Button normalSpeedBtn;

    @FXML
    private Button fastSpeedBtn;

    @FXML
    private Button veryFastSpeedBtn;

    @FXML
    private Label videoLabel;

    @FXML
    private Slider progressBar;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private MediaView mediaView;
    private ViewListener listener;

    //--------------- METHODS ----------------

    /**
     * Displays the tutorial window
     *
     * @param stage Window where the scene will be set
     */
    public void show(Stage stage) {
        stage.setScene(new Scene(layout));
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Listener setter
     *
     * @param listener This view's listener
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void helpEvents(ActionEvent event) {

        if (event.getSource() == projectManagementHelpBtn) {
            new AlertWindow("Soon...", "Coming soon...").showInformationWindow();
        } else if (event.getSource() == storageHelpBtn) {
            listener.loadVideo("storageHelp.mp4", "Storage management");
        } else if (event.getSource() == exportImportHelpBtn) {
            new AlertWindow("Soon...", "Coming soon...").showInformationWindow();
        } else if (event.getSource() == calendarHelpBtn) {
            listener.loadVideo("calendarHelp.mp4", "Storage management");
        } else if (event.getSource() == tagsHelpBtn) {
            listener.loadVideo("tagsHelp.mp4", "Tags management");
        } else if (event.getSource() == profileHelpBtn) {
            listener.loadVideo("profileHelp.mp4", "Profile Management");
        }
    }

    /**
     * Events for the buttons linked to the progress bar of the video.
     *
     * @param event MouseEvent
     */
    @FXML
    void progressBarEvents(MouseEvent event) {
        progressBar.setMin(0);
        progressBar.setMax(mediaPlayer.getTotalDuration().toSeconds());
        mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
    }


    /**
     * Plays the video when the button is clicked.
     *
     * @param event ActionEvent
     */
    @FXML
    void playBtnClicked(ActionEvent event) {
        setProgressBar();
        mediaPlayer.play();
    }

    /**
     * Sets the progress bar.
     */
    public void setProgressBar(){
        mediaPlayer.currentTimeProperty().addListener((observableValue, duration, currentDuration) -> progressBar.setValue(currentDuration.toSeconds()));
    }

    /**
     * Sets the video to pause when the button is clicked.
     *
     * @param event ActionEvent
     */
    @FXML
    void pauseBtnClicked(ActionEvent event) {
        mediaPlayer.pause();
    }

    /**
     * Stops the video when the button is clicked.
     *
     * @param event ActionEvent
     */
    @FXML
    void stopBtnClicked(ActionEvent event) {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED ){
            mediaPlayer.stop();
        }
        else {
            mediaPlayer.play();
        }
    }

    /**
     * Events for the buttons linked to the progress of the video.
     *
     * @param event ActionEvent
     */
    @FXML
    void videoProgressEvents(ActionEvent event) {
        if (event.getSource() == skipBtn){
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
        }
        else if (event.getSource() == rewindBtn){
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
        }
    }

    /**
     * Events for the buttons linked to the speed of the video.
     *
     * @param event ActionEvent
     */
    @FXML
    void videoSpeedEvents(ActionEvent event) {
        videoLabel.setVisible(false);
        if (event.getSource() == verySlowSpeedBtn){
            setVideoRate(0.5);
        }
        else if (event.getSource() == slowSpeedBtn){
            setVideoRate(0.75);
        }
        else if (event.getSource() == normalSpeedBtn){
            setVideoRate(1);
        }
        else if (event.getSource() == fastSpeedBtn){
            setVideoRate(1.75);
        }
        else if (event.getSource() == veryFastSpeedBtn){
            setVideoRate(2);
        }
    }

    /**
     * Sets the video rate.
     *
     * @param rateValue Rate value.
     */
    private void setVideoRate(double rateValue) {
        mediaPlayer.setRate(rateValue);
        videoLabel.setVisible(true);
        videoLabel.setText("x" + rateValue);
    }

    /**
     * Initializes the media player
     *
     * @param mediaPlayer The player to be used
     */
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        mediaView.setMediaPlayer(mediaPlayer);
    }

    /**
     * Controller communication interface
     */
    public interface ViewListener {

        /**
         * Sets media player and shows help window
         *
         * @param path  path to video file
         * @param title window title
         */
        void loadVideo(String path, String title);
    }
}

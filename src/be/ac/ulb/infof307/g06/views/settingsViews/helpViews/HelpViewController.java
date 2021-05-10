package be.ac.ulb.infof307.g06.views.settingsViews.helpViews;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Paths;

/**
 * Help section view controller
 */
public class HelpViewController {
    //-------------- ATTRIBUTES ----------------

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

    private MediaPlayer mediaPlayer;
    @FXML
    private MediaView mediaView;

    private ViewListener listener;

    //--------------- METHODS ----------------

    /**
     * The main method for button's events.
     *
     * @param event ActionEvent
     */
    @FXML
    private void helpEvents(ActionEvent event) {

        if (event.getSource() == projectManagementHelpBtn) {
            makeHelpWindow("projectManagementTest.mp4", "Project Management");
        } else if (event.getSource() == storageHelpBtn) {
            makeHelpWindow("storageHelp.mp4", "Storage management");
        } else if (event.getSource() == exportImportHelpBtn) {
            makeHelpWindow("exportImportHelp.mp4", "Import / Export");
        } else if (event.getSource() == calendarHelpBtn) {
            makeHelpWindow("calendarHelp.mp4", "Calendar");
        } else if (event.getSource() == tagsHelpBtn) {
            makeHelpWindow("tagsHelp.mp4", "Tags management");
        } else if (event.getSource() == profileHelpBtn) {
            makeHelpWindow("profileHelp.mp4", "Profile Management");
        }

    }

    /**
     * Sets media player and shows help window
     *
     * @param path  path to video file
     * @param title window title
     */
    private void makeHelpWindow(String path, String title) {
        File file = new File("src/be/ac/ulb/infof307/g06/resources/screenshots");
        path = file.getAbsolutePath() + "/" + path;
        Media media = new Media(Paths.get(path).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        listener.showHelp("TutorialView.fxml", title, mediaPlayer);
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
        mediaView.setMediaPlayer(mediaPlayer);
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
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.pause();
    }

    /**
     * Stops the video when the button is clicked.
     *
     * @param event ActionEvent
     */
    @FXML
    void stopBtnClicked(ActionEvent event) {
        mediaView.setMediaPlayer(mediaPlayer);
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
        mediaView.setMediaPlayer(mediaPlayer);
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
        mediaView.setMediaPlayer(mediaPlayer);
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
     * Sets the new media player to be used
     *
     * @param player The media player
     */
    public void setPlayer(MediaPlayer player) {
        mediaPlayer = player;
    }

    //--------------- LISTENER ----------------

    /**
     * Sets the listener.
     *
     * @param listener ViewListener, the listener to the view.
     */
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Listener that communicates with the controller
     */
    public interface ViewListener {
        /**
         * Displays a new stage with help information
         *
         * @param fileName The path to the video to be played
         * @param title    The title of the window
         * @param player   The player used
         */
        void showHelp(String fileName, String title, MediaPlayer player);
    }
}

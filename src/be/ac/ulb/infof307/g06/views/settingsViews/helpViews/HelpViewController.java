package be.ac.ulb.infof307.g06.views.settingsViews.helpViews;

import be.ac.ulb.infof307.g06.models.AlertWindow;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class HelpViewController implements Initializable {
    //-------------- ATTRIBUTES ----------------
// TODO : Changer structure des Controllers ! + EXCEPTIONS
    @FXML
    private AnchorPane  pane1;

    @FXML
    private AnchorPane  pane2;

    @FXML
    private AnchorPane  pane3;

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

    String path;

    MediaPlayer mediaPlayer;
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
    private void helpEvents(ActionEvent event) throws IOException {
        if (event.getSource() == projectManagementHelpBtn){
            listener.projectManagementHelp();
        }
        else if (event.getSource() == storageHelpBtn){
            new AlertWindow("Coming soon...", "Coming soon...").showInformationWindow();
            //listener.storageHelp();
        }
        else if (event.getSource() == exportImportHelpBtn){
            new AlertWindow("Coming soon...", "Coming soon...").showInformationWindow();
            //listener.exportImportHelp();
        }
        else if (event.getSource() == calendarHelpBtn){
            new AlertWindow("Coming soon...", "Coming soon...").showInformationWindow();
            //listener.calendarHelp();
        }
        else if (event.getSource() == tagsHelpBtn){
            new AlertWindow("Coming soon...", "Coming soon...").showInformationWindow();
            //listener.tagsHelp();
        }
        else if (event.getSource() == profileHelpBtn){
            new AlertWindow("Coming soon...", "Coming soon...").showInformationWindow();
            //listener.profileHelp();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        path = pathToVideo();
        Media media =new Media(Paths.get(path).toUri().toString());
        mediaPlayer = new MediaPlayer(media);

//        mediaView.setMediaPlayer(mediaPlayer);
    }

    public String pathToVideo(){
        return "/home/aline/Bureau/BA3/Genie/ProjetGenie/2021-groupe-6/src/be/ac/ulb/infof307/g06/resources/screenshots/projectManagementTest.mp4";
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
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration currentDuration) {
                progressBar.setValue(currentDuration.toSeconds());
            }
        });
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
        videoLabel.setText("x"+String.valueOf(rateValue));
    }

    /**
     * Animates the transition.
     *
     * @param duration double
     * @param node node
     * @param width double
     */
    public void translateAnimation(double duration, Node node, double width){
        TranslateTransition translateTransition= new TranslateTransition(Duration.seconds(duration), node);
        translateTransition.setByX(width);
        translateTransition.play();
    }
    int show = 0;

    /**
     * Goes to the next image.
     *
     * @param event ActionEvent
     */
    @FXML
    void next(ActionEvent event){
        if (show == 0){
            translateAnimation(0.1, pane1, -790);
            show ++;

        }
        else if (show == 1){
            translateAnimation(0.1, pane2, -790);
            show ++;

        }

    }

    /**
     * Goes to the previous image.
     *
     * @param event ActionEvent
     */
    @FXML
    void previous(ActionEvent event){
        if(show == 1){
            translateAnimation(0.1, pane1, 790);
            show--;

        }
        else if(show == 2){
            translateAnimation(0.1, pane2, 790);
            show--;
        }
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

    public interface ViewListener {
        void projectManagementHelp() throws IOException;
        void storageHelp() throws IOException;
        void calendarHelp() throws IOException;
        void exportImportHelp()throws IOException;
        void tagsHelp() throws IOException;
        void profileHelp() throws IOException;
        String okok();
    }
}

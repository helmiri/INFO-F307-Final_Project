package be.ac.ulb.infof307.g06.views.settingsViews;

import be.ac.ulb.infof307.g06.models.Tag;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.List;

public class TagsViewController {
    //-------------- ATTRIBUTES ----------------
    @FXML
    private Button addBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private ColorPicker tagsColorPicker;
    @FXML
    private TextField defaultTagNameTextField;
    @FXML
    private TableView<Tag> defaultTagsTableView;
    @FXML
    private TableColumn<Tag, String> defaultTagsColumn;

    private ViewListener listener;
    //--------------- METHODS ----------------

    /**
     * Returns the corresponding javafx.scene.paint.Color to HEX code
     *
     * @param hexCode String
     * @return javafx.scene.paint.Color
     */
    public static Color toColor(String hexCode) {
        java.awt.Color color = java.awt.Color.decode(hexCode);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return Color.rgb(r, g, b);
    }

    /**
     * The main method for button's events
     *
     * @param event ActionEvent
     */
    @FXML
    private void events(ActionEvent event) throws Exception {
        if (event.getSource() == addBtn) {
            listener.onAddButtonClicked(defaultTagNameTextField.getText(), toRGBCode(tagsColorPicker.getValue()));
        }
        if (event.getSource() == updateBtn) {
            listener.onUpdateButtonClicked(getSelectedTag(), defaultTagNameTextField.getText(), toRGBCode(tagsColorPicker.getValue()));
        }
    }

    /**
     * Convert Color objet to HEX code
     *
     * @param color javafx.scene.paint.Color
     * @return String
     */
    public String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }



    public void initialize(List<Tag> tags) {
        defaultTagsColumn.setCellValueFactory(new PropertyValueFactory<Tag, String>("description"));
        defaultTagsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        defaultTagsTableView.setStyle("-fx-selection-bar: gray; -fx-selection-bar-non-focused: gray; -fx-fill: black; -fx-control-inner-background-alt: -fx-control-inner-background ;");
        defaultTagsTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                if (tag != null) {
                    super.updateItem(tag, empty);
                    setText(tag.getDescription());
                    setStyle("-fx-background-color: " + tag.getColor() + ";");
                }
            }
        });
        defaultTagsTableView.setItems(FXCollections.observableArrayList(tags));
    }



    /**
     * Shows the Tags Table View
     */
    public void refresh(List<Tag> tags) {
        defaultTagsColumn.setCellValueFactory(new PropertyValueFactory<Tag, String>("description"));
        defaultTagsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        defaultTagsTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Tag tag, boolean empty) {
                if (tag != null) {
                    super.updateItem(tag, empty);
                    setText(tag.getDescription());
                    setStyle("-fx-background-color: " + tag.getColor() + ";");
                }
            }
        });
        defaultTagsTableView.setItems(FXCollections.observableArrayList(tags));
    }

    /**
     * Returns the selected Tag
     *
     * @return Tag
     */

    @FXML
    public Tag getSelectedTag(){
        return defaultTagsTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Updates the selected tag when clicked
     */
    @FXML
    public void onTagSelected() {
        if (getSelectedTag() != null) {
            defaultTagNameTextField.setText(getSelectedTag().getDescription());
            tagsColorPicker.setValue(toColor(getSelectedTag().getColor()));
        }
    }

    /**
     * Deletes selected tag
     * throws SQLException
     */

    @FXML
    public void deleteTag() throws SQLException {
        listener.deleteSelectedTag(getSelectedTag());
    }

    //--------------- LISTENER ----------------

    public void setListener(ViewListener listener) {
        this.listener = listener; }

    public interface ViewListener {

        void onAddButtonClicked(String text, String toRGBCode);

        void onUpdateButtonClicked(Tag selectedTag, String text, String toRGBCode);

        void deleteSelectedTag(Tag selectedTag);
    }
}

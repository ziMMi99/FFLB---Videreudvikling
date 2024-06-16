package com.project.fflb.controllers;

import com.project.fflb.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * An abstract class that has the general methods used for scene controlling,
 * such as switching scenes and making pop-up prompts.
 *
 * @author Sebastian
 */
public abstract class SceneController {
    /**
     * Switch to a new scene by loading an FXML file.
     * @param event Event containing the active stage.
     * @param fxmlFile .fxml file for the new scene.
     */
    protected void switchToScene(ActionEvent event, String fxmlFile) {
        try {
            //Gets the stage we are on using the ActionEvent
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            //Gets the fxmlFile that should get loaded
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlFile + ".fxml"));
            //Gets the scene from said fxmlFile
            Scene scene = new Scene(fxmlLoader.load());
            //Retrieve the css file for the fxml file.
            String css = App.class.getResource(fxmlFile + ".css").toExternalForm();
            //Add the stylesheet to the fxml file.
            scene.getStylesheets().add(css);
            //Sets scene to the new scene
            stage.setScene(scene);
            //Shows it
            stage.show();
        } catch (Exception e) {
            System.out.println("Error switching scenes! - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creat pop-up window by loading an FXML file.
     * @param event Event containing the active stage.
     * @param fxmlFile .fxml file for the new window.
     */
    protected void makePopUp(ActionEvent event, String fxmlFile) {
        try {
            //Makes new stage(window)
            Stage stage = new Stage();
            //Gets resources from the fxmlFile
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlFile));
            //Gets the scene from said fxmlFile
            Scene scene = new Scene(fxmlLoader.load());
            //Block events to other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            //Sets scene to the new scene
            stage.setScene(scene);
            //Shows it and waits
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an Alert with the AlertType "ERROR"
     *
     * @param title The title of the popup
     * @param msg The Message of the popup
     */
    protected void createErrorPopup(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Creates an Alert with the AlertType "INFORMATION"
     *
     * @param title The title of the popup
     * @param msg The Message of the popup
     */
    protected void createInformationPopup(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Creates an Alert with the AlertType "CONFIRMATION"
     *
     * @param title The title of the popup
     * @param msg The Message of the popup
     * @return Result of button press in an {@link Optional} object
     */
    protected Optional<ButtonType> createConfirmationPopup(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        return alert.showAndWait();
    }
}

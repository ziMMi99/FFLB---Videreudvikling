package com.project.fflb.controllers.car;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CarData;
import com.project.fflb.dbo.Car;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller responsible for carInformation.fxml
 *
 * @author Sebastian
 */
public class CarInformationController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    Label IDLabel;
    @FXML
    TextField modelNameTextField, priceTextField;
    @FXML
    Button ConfirmChangesButton;

    // =============
    //   VARIABLES
    // =============

    /**
     * Selected car object
     */
    private Car selectedCar;

    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - Switch back to the table view.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCarTable(ActionEvent event){
        CarTableController.setSelectedCar(null);
        switchToScene(event, "CarTable");
    }

    /**
     * "Confirm Changes" button - Verify the typed information is correct, then update entry in the database.
     * @param event {@link ActionEvent} from button press
     */
    public void confirmUpdate(ActionEvent event){
        //Disables the button, which both gives the user feedback, and prevents issues with the button being pressed again before it is done
        ConfirmChangesButton.setDisable(true);
        //Retrieves all updated info from the scene
        String updatedModelName = modelNameTextField.getText();

        double updatedPrice;
        //Check if the newly entered price is a valid number before proceeding
        try {
            updatedPrice = Double.parseDouble(priceTextField.getText());
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered price is invalid.");
            ConfirmChangesButton.setDisable(false);
            return;
        }

        //Makes new car object with the updated info, with same id and hasPaymentPlan
        Car car = new Car(selectedCar.getCarID(), updatedModelName, updatedPrice, selectedCar.hasPaymentPlan());
        //Updates the database
        CarData.update(car);

        //Makes the selected car match the updated car
        selectedCar = new Car(car);

        createInformationPopup("Success", "Updated car in the Database.");

        CarTableController.setSelectedCar(selectedCar);
        //Switches back to the customer table
        switchToScene(event, "CarTable");
    }

    /**
     * "Delete" button - Create "are you sure?" prompt, delete the selected car from the database.
     * @param event {@link ActionEvent} from button press
     */
    public void deleteCar(ActionEvent event) {
        //Alert asking if the user is sure they want to delete the car
        Optional<ButtonType> result =
                createConfirmationPopup("Alert", "Are you sure you want to delete " + selectedCar.getModelName() + " from the database?");

        if (result.isEmpty()) {
            System.out.println("Alert closed");
            return;
        } else if (result.get() == ButtonType.OK) { //User confirmed deletion
            //Connects to database and deletes the car
            CarData.delete(selectedCar.getCarID());

            //Feedback alert
            createInformationPopup("System alert", selectedCar.getModelName() + " was deleted from the database.");
        } else if (result.get() == ButtonType.CANCEL) {//User cancelled deletion
            System.out.println("Deletion cancelled");
            return;
        }

        //Update the selected car so it matches the edited one
        CarTableController.setSelectedCar(selectedCar);
        //Switches back to car table
        switchToCarTable(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Retrieves the selected car from car table
        selectedCar = CarTableController.getSelectedCar();
        //Sets scene elements to show the car information
        IDLabel.setText("ID: " + selectedCar.getCarID());
        modelNameTextField.setText(selectedCar.getModelName());
        priceTextField.setText("" + selectedCar.getPrice());
    }
}

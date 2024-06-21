package com.project.fflb.controllers.car;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CarData;
import com.project.fflb.dbo.Car;
import com.project.fflb.dbo.FerrariCarList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.controlsfx.control.SearchableComboBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller responsible for CarCreation.fxml
 *
 * @author Sebastian
 */
public class CarCreationController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    SearchableComboBox<String> modelNameTextField;
    @FXML
    TextField priceTextField;
    @FXML
    Button CreateCarButton;

    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - Switch back to the table view.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCarTable(ActionEvent event){
        //Uses the sceneController method to switch the scene
        switchToScene(event, "CarTable");
    }

    /**
     * "Create Car" button - Verify the typed information is correct and then add the car to the database.
     * @param event {@link ActionEvent} from button press
     */
    public void createNewCar(ActionEvent event){
        //Retrieves info from text boxes
        String modelName = modelNameTextField.getSelectionModel().getSelectedItem();
        if (modelName.isEmpty()) {
            createErrorPopup("System alert", "Model name is required.");
            return;
        }

        boolean validPrice = true;
        double price = 0;

        //Check if the inputted price is valid before continuing
        //Check if valid number
        try {
            price = Double.parseDouble(priceTextField.getText());
        } catch (NumberFormatException e) {
            validPrice = false;
        }

        //Check if price is in the positive range
        if (price <= 0) {
            validPrice = false;
        }

        //Spit out an alert if the price isn't valid
        if (!validPrice) {
            createErrorPopup("System alert", "The entered price is invalid.");
            return;
        }

        //Create car object and send to database
        Car car = new Car(modelName, price, false);
        CarData.create(car);

        //Feedback alert
        createInformationPopup("Success", "Added car to the database.");

        //Switches back to the car table
        switchToScene(event, "CarTable");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        modelNameTextField.getItems().addAll(FerrariCarList.getCars());
    }
}

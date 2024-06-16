package com.project.fflb.controllers.salesman;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.SalesmanData;
import com.project.fflb.dbo.Salesman;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller responsible for SalesmanInformation.fxml
 *
 * @author Victor
 */
public class SalesmanInformationController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    Button ConfirmChangesButton, DeleteSalesmanButton;
    @FXML
    TextField FirstNameTextField, LastNameTextField, EmailTextField, PhoneNumberTextField, LoanLimitTextField;
    @FXML
    Label IDLabel;
    @FXML
    CheckBox IsActiveCheckbox;

    // =============
    //   VARIABLES
    // =============

    private Salesman selectedSalesman;

    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - switch back to the salesman table view.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToSalesmanTable(ActionEvent event) {
        SalesmanTableController.setSelectedSalesman(null);
        switchToScene(event, "SalesmanTable");
    }

    /**
     * Construct new Salesman object with the newly updated information and apply changes to database.
     * @param event {@link ActionEvent} from button press
     */
    public void confirmUpdate(ActionEvent event) {
        //Retrieves info from the textFields
        String updatedFirstName = FirstNameTextField.getText();
        String updatedLastName = LastNameTextField.getText();
        String updatedEmail = EmailTextField.getText();
        int updatedPhoneNumber = Integer.parseInt(PhoneNumberTextField.getText());
        double updatedLoanLimit = Double.parseDouble(LoanLimitTextField.getText());

        //Check if one or more of the fields are empty
        if (updatedFirstName.isEmpty() ||
                updatedLastName.isEmpty() ||
                updatedEmail.isEmpty() ||
                PhoneNumberTextField.getText().isEmpty() ||
                LoanLimitTextField.getText().isEmpty()) {
            createErrorPopup("System alert", "One or more fields are empty" );
            return;
        }
        boolean updatedIsActive = IsActiveCheckbox.isSelected();

        //Makes a new salesman object with the updated info
        Salesman salesman = new Salesman(
                selectedSalesman.getPersonID(),
                updatedFirstName,
                updatedLastName,
                updatedEmail,
                updatedPhoneNumber,
                updatedLoanLimit,
                updatedIsActive
        );

        //Updates the database
        SalesmanData.update(salesman);

        //Makes the selected salesman match the updated salesman
        selectedSalesman = new Salesman(selectedSalesman);

        createInformationPopup("System alert","Changes updated to the database");

        //Clear selection on table view before switching back
        SalesmanTableController.setSelectedSalesman(null);
        switchToSalesmanTable(event);
    }

    /**
     * Delete the selected salesman from the database
     * @param event {@link ActionEvent} from button press
     */
    public void deleteSalesman(ActionEvent event) {
        //Alert to see if you really want to delete this salesman
        Optional<ButtonType> result = createConfirmationPopup(
                "System alert",
                "Are you sure you want to delete " +
                        selectedSalesman.getFirstName() + " " +
                        selectedSalesman.getLastName() + " from the database?"
        );

        //Delete if user said OK
        if (result.isEmpty()) {
            System.out.println("Alert closed");
            return;
        } else if (result.get() == ButtonType.OK) {
            SalesmanData.delete(selectedSalesman.getPersonID());
        } else if (result.get() == ButtonType.CANCEL) {
            System.out.println("Deletion cancelled");
            return;
        }

        //Feedback alert
        createInformationPopup("Success", selectedSalesman.getFirstName() + " " + selectedSalesman.getLastName() + " was deleted from the database");

        //Clear selection on table view before switching back
        SalesmanTableController.setSelectedSalesman(null);
        switchToSalesmanTable(event);
    }

    public void initialize(URL location, ResourceBundle resources) {
        //Gets the selected salesman from salesman table
        selectedSalesman = SalesmanTableController.getSelectedSalesman();
        //Sets scene elements to show information
        IDLabel.setText("ID: " + selectedSalesman.getPersonID());
        FirstNameTextField.setText(selectedSalesman.getFirstName());
        LastNameTextField.setText(selectedSalesman.getLastName());
        EmailTextField.setText(selectedSalesman.getEmail());
        PhoneNumberTextField.setText(String.valueOf(selectedSalesman.getPhoneNumber()));
        LoanLimitTextField.setText(String.valueOf(selectedSalesman.getSalesmanLoanLimit()));
        IsActiveCheckbox.setSelected(selectedSalesman.getIsActive());
    }
}

package com.project.fflb.controllers.salesman;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.SalesmanData;
import com.project.fflb.dbo.Salesman;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller responsible for SalesmanCreation.fxml
 *
 * @author Victor
 */
public class SalesmanCreationController extends SceneController {
    // ========
    //   FXML
    // ========

    @FXML
    TextField FirstNameTextField, LastNameTextField, EmailTextField, PhoneNumberTextField, LoanLimitTextField;
    @FXML
    Button CreateSalesmanButton;

    // ===========
    //   METHODS
    // ===========

    /**
     * "Back" button - switch back to the salesman table view.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToSalesmanTable(ActionEvent event) {
        switchToScene(event, "SalesmanTable");
    }

    /**
     * Creates a new Salesman object and adds it to the database
     * @param event {@link ActionEvent} from button press
     */
    public void createNewSalesman(ActionEvent event) {
        //Retrieves info from the text fields
        String firstName = FirstNameTextField.getText();
        String lastName = LastNameTextField.getText();
        String email = EmailTextField.getText();

        //Check if one or more of the fields are empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            createErrorPopup("System alert","One or more fields are empty");
            return;
        }

        int phoneNumber;
        //Check if phone number is a number
        try {
            phoneNumber = Integer.parseInt(PhoneNumberTextField.getText());
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered phone number is not a valid phone number");
            return;
        }

        double salesmanLoanLimit;
        //Check if loan limit is a number
        try {
            salesmanLoanLimit = Double.parseDouble(LoanLimitTextField.getText());
        } catch (NumberFormatException e) {
            createErrorPopup("System alert", "The entered loan limit is not a valid loan limit");
            return;
        }

        //Create salesman object and sends it to database

        Salesman salesman = new Salesman(
                firstName,
                lastName,
                email,
                phoneNumber,
                salesmanLoanLimit,
                true //isActive is true, since all newly added salesmen are active
        );

        SalesmanData.create(salesman);

        //Feedback alert
        createInformationPopup("Success", "Added Salesman to the Database");

        //Switch back to SalesmanTable
        switchToScene(event, "SalesmanTable");
    }
}

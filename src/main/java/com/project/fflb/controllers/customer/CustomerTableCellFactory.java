package com.project.fflb.controllers.customer;

import api.com.ferrari.finances.dk.rki.Rating;
import com.project.fflb.dbo.Customer;
import javafx.scene.control.TableCell;


/**
 * A Factory Class that handles each cell in the customer listview
 * It Changes the color of the cell based on credit score
 *
 * @author Sebastian
 */
public class CustomerTableCellFactory<T> extends TableCell<Customer, T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        //calling superclass method to ensure proper handling of the cells state
        /*
         * Source: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Cell.html#updateItem-T-boolean-
         * It is very important that subclasses of Cell override the updateItem method,
         * as failure to do so will lead to issues such as blank cells or cells with unexpected content appearing within them.
         */
        super.updateItem(item, empty);

        //If the cell is empty or the items is null, clear the cell
        if (empty || item == null) {
            setText(null);
            setStyle("");
        } else {
            //if the cells is not empty, print the info from the payment plan into the cell
            setText(item.toString());

            //Get customer from current row
            Customer customer = getTableView().getItems().get(getIndex());

            //Set appropriate cell colour according to the customer's credit rating

            //Credit score A
            if(customer.getCreditScore().equals(Rating.A)) {
                setStyle("-fx-background-color: limegreen");
            }
            //Credit score B
            if(customer.getCreditScore().equals(Rating.B)) {
                setStyle("-fx-background-color: lightgreen");
            }
            //Credit score C
            if(customer.getCreditScore().equals(Rating.C)) {
                setStyle("-fx-background-color: palegoldenrod");
            }
            //Credit score D
            if(customer.getCreditScore().equals(Rating.D)) {
                setStyle("-fx-background-color: lightcoral");
            }
        }
    }
}

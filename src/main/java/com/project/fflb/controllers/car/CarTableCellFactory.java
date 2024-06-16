package com.project.fflb.controllers.car;

import com.project.fflb.dbo.Car;
import javafx.scene.control.TableCell;

/**
 * A Factory Class that handles each cell in the car listview
 * It Changes the color of the cell based on their plan
 *
 * @author Sebastian
 */
public class CarTableCellFactory<T> extends TableCell<Car, T> {
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
        if ( empty || (item == null) ) {
            setText(null);
            setStyle("");
        } else {
            //if the cell is not empty, print the info from the paymentplan into the cell
            setText(item.toString());

            //Get the car at the current row
            Car car = getTableView().getItems().get(getIndex());

            //Checks if car has a payment plan, sets colour accordingly
            if (car.hasPaymentPlan()) {
                setStyle("-fx-background-color: lightcoral");
            } else {// if car does not have a payment plan
                setStyle("-fx-background-color: limegreen;");
            }
        }
    }
}

package com.project.fflb.controllers.paymentplan;

import com.project.fflb.dbo.PaymentPlan;
import javafx.scene.control.TableCell;

/**
 * A Factory Class that handles each cell in the payment plan table listview.
 * It Changes the color of the cell based on if its started, not started or finished.
 *
 * @author Simon
 */
public class PaymentPlanTableCellFactory<S, T> extends TableCell<S, T> {
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

            //Get the payment plan for the current row
            PaymentPlan paymentPlan = (PaymentPlan) getTableView().getItems().get(getIndex());

            //Set the colour of the cell depending on the state of the payment plan

            //Has started
            if (paymentPlan.isStarted()) {
                setStyle("-fx-background-color: lightgoldenrodyellow");
            }
            //Hasn't started
            if (!paymentPlan.isStarted()) {
                setStyle("-fx-background-color: lightcoral");
            }
            //Is finished
            if (paymentPlan.isFinished()) {
                setStyle("-fx-background-color: limegreen");
            }
        }
    }
}

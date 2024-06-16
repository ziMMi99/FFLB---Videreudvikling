package com.project.fflb.controllers.salesman;

import com.project.fflb.dbo.Salesman;
import javafx.scene.control.TableCell;

/**
 * A Factory Class that handles each cell in the salesman listview
 * It Changes the color of the cell based on if they are active or not
 *
 * @author Sebastian
 */
public class SalesmanTableCellFactory<T> extends TableCell<Salesman, T> {
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
            //if the cells is not empty, print the info from the salesman into the cell
            setText(item.toString());

            //Get selected salesman
            Salesman salesman = getTableView().getItems().get(getIndex());

            //salesman is active
            if (salesman.getIsActive()) {
                setStyle("-fx-background-color: limegreen");
            //salesman is inactive
            } else {
                setStyle("-fx-background-color: lightcoral");
            }

        }
    }
}

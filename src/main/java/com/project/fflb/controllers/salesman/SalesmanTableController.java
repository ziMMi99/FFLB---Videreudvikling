package com.project.fflb.controllers.salesman;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.SalesmanData;
import com.project.fflb.dbo.Salesman;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

/**
 * Controller responsible for SalesmanTable.fxml
 *
 * @author Victor
 * @Bugfixes: Simon
 */
public class SalesmanTableController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    Button CreateSalesmanButton, SelectSalesmanButton,BackButton;
    @FXML
    TextField SearchBar;
    @FXML
    TableColumn<Salesman, Double> LoanLimitColumn;
    @FXML
    TableColumn<Salesman, String> EmailColumn;
    @FXML
    TableColumn<Salesman, String> FirstNameColumn;
    @FXML
    TableColumn<Salesman, String> LastNameColumn;
    @FXML
    TableColumn<Salesman, Integer> PhoneColumn;
    @FXML
    TableView<Salesman> SalesmanTable;

    // =============
    //   VARIABLES
    // =============

    /**
     * The currently selected salesman. This variable is static such that it can be accessed by other scene controllers.
     */
    private static Salesman selectedSalesman;

    // ===========
    //   METHODS
    // ===========

    /**
     * Get the currently selected salesman from the table screen - static such that it is accessible from anywhere.
     * @return Selected {@link Salesman} object
     */
    public static Salesman getSelectedSalesman() {
        return selectedSalesman;
    }

    /**
     * Static method for other scenes to set the selected salesman
     * @param newSelectedSalesman The new selected salesman
     */
    public static void setSelectedSalesman(Salesman newSelectedSalesman){
        selectedSalesman = newSelectedSalesman;
    }

    /**
     * "Back" button - switch back to the home page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToHomePage(ActionEvent event){
        switchToScene(event, "HomePage");
    }

    /**
     * "Create salesman" button - switch to salesman creation
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCreationSalesman(ActionEvent event) {
        switchToScene(event, "SalesmanCreation");
    }

    /**
     * "Select salesman" button - switch to  salesman information
     * @param event {@link ActionEvent} from button press
     */
    public void switchToSelectSalesman(ActionEvent event) {
        //Make sure a salesman is selected
        if (selectedSalesman == null) {
            createErrorPopup("System alert", "No salesman selected");
            return;
        }

        switchToScene(event, "SalesmanInformation");
    }

    /**
     * This method goes through the database and returns a list of all salesman that contain the keyword.
     * @param keyword The word that is searched for
     * @return All salesman that contain the keyword
     */
    public ObservableList<Salesman> search(String keyword) {
        //Retrieves all cars from the database
        ArrayList<Salesman> listOfSalesman = SalesmanData.getAll();
        ObservableList<Salesman> searchResults = FXCollections.observableArrayList();
        //Loops through all cars in the database
        for (Salesman salesman : listOfSalesman) {
            //Adds the car to the searchResult set, if it contains the keyword
            if (salesman.getFirstName().toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(salesman);
            }
        }
        //Returns the list of salesman that contain the keyword
        return searchResults;
    }

    /**
     * "Search" button - filter table by text typed into the search bar.
     * @param event {@link ActionEvent} from button press
     */
    public void searchButtonAction(ActionEvent event) {
        filterTable();
    }

    /**
     * Filter salesman table based on the word inputted into the {@link #SearchBar}.
     */
    private void filterTable() {
        //Get the list of salesmen containing the searched word
        ObservableList<Salesman> foundSalesman = search(SearchBar.getText());
        //Set the table's contents based on this list
        SalesmanTable.setItems(foundSalesman);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            updateTable();

            //Add an ActionListener to the salesman table, which listens for a click on an element.
            //This in turn sets the selected salesman to the selected element in the list.
            SalesmanTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Salesman> observableValue, Salesman salesman, Salesman t1) {
                    selectedSalesman = SalesmanTable.getSelectionModel().getSelectedItem();
                }
            });

            //Add a listener to the text property of the searchbar to filter the results whenever new information is typed in
            SearchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        } catch (Exception e) {
            System.out.println("Error initializing table: " + e.getMessage());
        }
    }

    /**
     * Updates the list of salesman.
     * Gets a list of salesman from the database, formats them depending on whether they're active or not .
     */
    private void updateTable() {
        //Gets all the salesman from the database
        ArrayList<Salesman> listOfSalesman = SalesmanData.getAll();

        //Check if the connection was successful
        if (!SalesmanData.hasConnection()) {
            createErrorPopup("Database Error", "Could not establish connection with database.");
            return;
        }

        //Sorts the list if salesman
        listOfSalesman.sort(Comparator.comparing(Salesman::getIsActive).reversed());

        //Sets the tableview to the list of all salesman
        SalesmanTable.getItems().addAll(listOfSalesman);

        //setCellValueFactory is used to decide what each cell in the column shows, since adding the list of customers to the table doesn't mean it shows the data
        LoanLimitColumn.setCellValueFactory(cellData -> {
            //cellData contains a salesman, since we added the list of salesmen to the table. All cells along a row has the same salesman.
            Salesman salesman = cellData.getValue();
            if (salesman != null) {
                //Returns an observable value, since that is what setCellValueFactory can take. That observable value is the thing that the cell then shows.
                return new ReadOnlyObjectWrapper<>(salesman.getSalesmanLoanLimit());
            } else {
                //Null handling
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        //Set the cell factory to our custom class that changes the background colour of the cell
        LoanLimitColumn.setCellFactory(tv -> new SalesmanTableCellFactory<>());

        /*
         * The code beneath sets the columns contents the same way the above code does, so there is no need to describe that code again
         */

        EmailColumn.setCellValueFactory(cellData -> {
            Salesman salesman = cellData.getValue();
            if (salesman != null) {
                return new ReadOnlyObjectWrapper<>(salesman.getEmail());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        EmailColumn.setCellFactory(tv -> new SalesmanTableCellFactory<>());

        FirstNameColumn.setCellValueFactory(cellData -> {
            Salesman salesman = cellData.getValue();
            if (salesman != null) {
                return new ReadOnlyObjectWrapper<>(salesman.getFirstName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        FirstNameColumn.setCellFactory(tv -> new SalesmanTableCellFactory<>());

        LastNameColumn.setCellValueFactory(cellData -> {
            Salesman salesman = cellData.getValue();
            if (salesman != null) {
                return new ReadOnlyObjectWrapper<>(salesman.getLastName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        LastNameColumn.setCellFactory(tv -> new SalesmanTableCellFactory<>());

        PhoneColumn.setCellValueFactory(cellData -> {
            Salesman salesman = cellData.getValue();
            if (salesman != null) {
                return new ReadOnlyObjectWrapper<>(salesman.getPhoneNumber());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        PhoneColumn.setCellFactory(tv -> new SalesmanTableCellFactory<>());
    }
}

package com.project.fflb.controllers.car;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CarData;
import com.project.fflb.dbo.Car;
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
 * Controller responsible for carTable.fxml
 *
 * @author Sebastian
 */
public class CarTableController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    Button CreateCarButton, SelectCarButton,BackButton;
    @FXML
    TextField SearchBar;
    @FXML
    TableView<Car> Table;
    @FXML
    TableColumn<Car, String> ModelNameColumn;
    @FXML
    TableColumn<Car, Double> PriceColumn;

    // =============
    //   VARIABLES
    // =============

    /**
     * Static variable referenced across scenes to synchronize the selected item in the list.
     */
    private static Car selectedCar;

    // ===========
    //   METHODS
    // ===========

    //Scene switches
    /**
     * "Back" button - Switch back to the home page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToHomePage(ActionEvent event){
        //Uses the sceneController method to switch the scene
        switchToScene(event, "HomePage");
    }
    /**
     * "Create Car" button - Switch to car creation page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCarCreation(ActionEvent event){
        //Uses the sceneController method to switch the scene
        switchToScene(event, "CarCreation");
    }
    /**
     * "Edit Car" button - Switch to car editing page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCarInformation(ActionEvent event){
        //Check the selected car isn't null before continuing
        if (selectedCar == null) {
            createErrorPopup("System alert", "No car selected");
            return;
        }
        //Uses the sceneController method to switch the scene
        switchToScene(event, "CarInformation");
    }

    //Search Functionalities
    /**
     * This method goes through the database and returns a list of all cars that contain the keyword
     * @param keyword The word that is searched for
     * @return {@link ObservableList} of all cars that contain the keyword
     */
    public ObservableList<Car> search(String keyword) {
        //Retrieves all cars from the database
        ArrayList<Car> listOfCars = CarData.getAll();

        //Create new ObservableList
        ObservableList<Car> searchResults = FXCollections.observableArrayList();

        //Loops through all cars in the database
        for (Car car : listOfCars) {
            //Adds the car to the searchResult set, if it contains the keyword
            if (car.getModelName().toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(car);
            }
        }

        //Returns the list of cars, that contain the keyword
        return searchResults;
    }

    /**
     * Filter the table with the contents of the search bar.
     */
    private void filterTable() {
        //Searches for all cars that contain the searchbar contents
        ObservableList<Car> foundCars = search(SearchBar.getText());
        //Sets the car table to only show the search results
        Table.setItems(foundCars);
    }

    /**
     * The method that is called when the search button is pressed
     */
    public void searchButtonAction() {
        filterTable();
    }

    /**
     * Static method for other scenes to fetch the selected car.
     * @return {@link Car} object selected in the list
     */
    public static Car getSelectedCar() {
        return selectedCar;
    }

    /**
     * Static method for other scenes to set the selected car.
     * @param newSelectedCar The new selected car
     */
    static void setSelectedCar(Car newSelectedCar){
        selectedCar = newSelectedCar;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //Update the table upon the scene loading
            updateTable();

            //Add an action listener to the car table, which listens for a click on an element.
            //This in turn sets the selected car to the selected element in the list.
            Table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Car> observableValue, Car car, Car t1) {
                    selectedCar = Table.getSelectionModel().getSelectedItem();
                }
            });

            //Add a listener to the text property of the searchbar to filter the results whenever new information is typed in
            SearchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        } catch (Exception e) {
            System.out.println("Error initializing table: " + e.getMessage());
        }
    }

    /**
     * Updates the list of cars.
     * Gets a list of cars from the database, formats them depending on whether they're part of a payment plan.
     */
    private void updateTable() {
        //Gets all the cars from the database
        ArrayList<Car> listOfCars = CarData.getAll();

        //Check if the connection was successful
        if (!CarData.hasConnection()) {
            createErrorPopup("Database Error", "Could not establish connection with database.");
            return;
        }
        //Sorts the list of cars based on their name
        listOfCars.sort(Comparator.comparing(Car::getModelName));

        //Sets the tableview to the list of all cars
        Table.getItems().addAll(listOfCars);


        //setCellValueFactory is used to decide what each cell in the column shows, since adding the list of customers to the table doesn't mean it shows the data
        ModelNameColumn.setCellValueFactory(cellData -> {
            //cellData contains a car, since we added the list of cars to the table. All cells along a row has the same car.
            Car car = cellData.getValue();
            if (car != null) {
                //Returns an observable value, since that is what setCellValueFactory can take. That observable value is the thing that the cell then shows.
                return new ReadOnlyObjectWrapper<>(car.getModelName());
            } else {
                //Null handling
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        //Set the cell factory to our custom class that changes the background colour of the cell
        ModelNameColumn.setCellFactory(tv -> new CarTableCellFactory<>());

        /*
         * The code beneath sets the columns contents the same way the above code does, so there is no need to describe that code again
         */

        PriceColumn.setCellValueFactory(cellData -> {
            Car car = cellData.getValue();

            if (car != null) {
                return new ReadOnlyObjectWrapper<>(car.getPrice());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        PriceColumn.setCellFactory(tv -> new CarTableCellFactory<>());
    }
}

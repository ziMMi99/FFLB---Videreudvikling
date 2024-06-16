package com.project.fflb.controllers.paymentplan;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.PaymentPlanData;
import com.project.fflb.dbo.Car;
import com.project.fflb.dbo.Customer;
import com.project.fflb.dbo.PaymentPlan;
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
 * @author Sebastian
 */
public class PaymentPlanTableController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    TableView<PaymentPlan> PaymentPlanTable;
    @FXML
    TableColumn<PaymentPlan, String> CarModelNameColumn; //car
    @FXML
    TableColumn<PaymentPlan, Double> CarPriceColumn; //Car
    @FXML
    TableColumn<PaymentPlan, String> CustomerFirstNameColumn; //customer
    @FXML
    TableColumn<PaymentPlan, String> CustomerLastNameColumn; //customer
    @FXML
    TableColumn<PaymentPlan, Double> DownPaymentColumn;
    @FXML
    TableColumn<PaymentPlan, String> MonthlyPaymentColumn;
    @FXML
    TableColumn<PaymentPlan, Integer> PlanLengthColumn;
    @FXML
    TableColumn<PaymentPlan, String> RentColumn;
    @FXML
    TableColumn<PaymentPlan, String> SalesmanFirstNameColumn; //Salesman
    @FXML
    TableColumn<PaymentPlan, String> SalesmanLastNameColumn; //Salesman
    @FXML
    TableColumn<PaymentPlan, String> StartDateColumn;
    @FXML
    Button CreatePaymentPlanButton, SelectPaymentPlanButton;
    @FXML
    TextField SearchBar;

    // =============
    //   VARIABLES
    // =============

    /**
     * Currently selected payment plan
     */
    private static PaymentPlan selectedPaymentPlan;

    // ===========
    //   METHODS
    // ===========

    /**
     * Static method to retrieve the selected payment plan, to share among controllers.
     * @return Selected payment plan
     */
    public static PaymentPlan getSelectedPaymentPlan() {
        return selectedPaymentPlan;
    }

    /**
     * Static method to set the selected payment plan, to share among controllers.
     * @param newSelectedPaymentPlan New value for the selected payment plan
     */
    public static void setSelectedPaymentPlan(PaymentPlan newSelectedPaymentPlan){
        selectedPaymentPlan = newSelectedPaymentPlan;
    }

    /**
     * "Back" button - switch back to the table view
     * @param event {@link ActionEvent} from button press
     */
    public void switchToHomePage(ActionEvent event){
        switchToScene(event, "HomePage");
    }

    /**
     * "Create Payment Plan" button - Switch to payment plan creation page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToPaymentPlanCreation(ActionEvent event){
        switchToScene(event, "PaymentPlanCreation");
    }

    /**
     * "Edit Payment Plan" button - Switch to payment plan editing page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToPaymentPlanInformation(ActionEvent event){
        //If no plan is selected, abort
        if (selectedPaymentPlan == null) {
            createErrorPopup("System alert", "No payment plan selected");
            return;
        }

        switchToScene(event, "PaymentPlanInformation");
    }

    //Search Functionalities

    /**
     * This method goes through the database and returns a list of all cars that contain the keyword
     * @param keyword The word that is searched for
     * @return {@link ObservableList} of all cars that contain the keyword
     */
    public ObservableList<PaymentPlan> search(String keyword) {
        //Retrieves all cars from the database
        ArrayList<PaymentPlan> listOfPaymentPlans = PaymentPlanData.getAll();
        ObservableList<PaymentPlan> searchResults = FXCollections.observableArrayList();
        //Loops through all cars in the database
        for (PaymentPlan paymentPlan : listOfPaymentPlans) {
            //Adds the car to the searchResult set, if it contains the keyword
            if (paymentPlan.getCustomer().getFirstName().toLowerCase().contains(keyword.toLowerCase())
                    || paymentPlan.getCustomer().getCPR().toLowerCase().contains(keyword.toLowerCase())
                    || paymentPlan.getCar().getModelName().toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(paymentPlan);
            }
        }

        //Returns the list of cars, that contain the keyword
        return searchResults;
    }

    /**
     * The method that is called when the search button is pressed
     */
    public void searchButtonAction() {
        filterTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            updateTable();

            //Add an action listener to the payment plan table, which listens for a click on an element.
            //This in turn sets the selected payment plan to the selected element in the list.
            PaymentPlanTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends PaymentPlan> observableValue, PaymentPlan paymentPlan, PaymentPlan t1) {
                    selectedPaymentPlan = PaymentPlanTable.getSelectionModel().getSelectedItem();
                }
            });

            //Add a listener to the text property of the searchbar to filter the results whenever new information is typed in
            SearchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        } catch (Exception e) {
            System.out.println("Error initializing table: " + e.getMessage());
        }
    }

    /**
     * Get all payment plans from the database and populate the table with them.
     */
    private void updateTable() {
        //Gets all the payment plans from the database
        ArrayList<PaymentPlan> listOfPaymentPlans = PaymentPlanData.getAll();

        //Check if the connection was successful
        if (!PaymentPlanData.hasConnection()) {
            createErrorPopup("Database Error", "Could not establish connection with database.");
            return;
        }

        //Sort the list of payment plans based on start date, and reversed to get the oldest/completed ones at the bottom.
        listOfPaymentPlans.sort(Comparator.comparing(PaymentPlan::getStartDate));

        //Sets the list view to the list of all cars
        PaymentPlanTable.getItems().addAll(listOfPaymentPlans);

        //Set the data used for each column in the TableView

        //setCellValueFactory is used to decide what each cell in the column shows, since adding the list of customers to the table, doesn't mean it shows the data
        CarModelNameColumn.setCellValueFactory(cellData -> {
            //cellData contains a paymentPlan, since we added the list of paymentPlans to the table. All cells along a row has the same paymentPlan.
            Car car = cellData.getValue().getCar();
            if (car != null) {
                //Returns an observable value, since that is what setCellValueFactory can take. That observable value is the thing that the cell then shows.
                return new ReadOnlyObjectWrapper<>(car.getModelName());
            } else {
                //Null handling
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        //Set the cell factory to our custom class that changes the background colour of the cell
        CarModelNameColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        /*
         * The code beneath sets the columns contents the same way the above code does, so there is no need to describe that code again
         */

        CarPriceColumn.setCellValueFactory(cellData -> {
            PaymentPlan paymentPlan = cellData.getValue();
            if (paymentPlan != null) {
                return new ReadOnlyObjectWrapper<>(paymentPlan.getFixedCarPrice());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        CarPriceColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        CustomerFirstNameColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue().getCustomer();
            if (customer != null){
                return new ReadOnlyObjectWrapper<>(customer.getFirstName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
            });
        CustomerFirstNameColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        CustomerLastNameColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue().getCustomer();
            if (customer != null){
                return new ReadOnlyObjectWrapper<>(customer.getLastName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
            });
        CustomerLastNameColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        DownPaymentColumn.setCellValueFactory(cellData -> {
            PaymentPlan paymentPlan = cellData.getValue();
            if (paymentPlan != null) {
                return new ReadOnlyObjectWrapper<>(paymentPlan.getDownPayment());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        DownPaymentColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        MonthlyPaymentColumn.setCellValueFactory(cellData -> {
            PaymentPlan paymentPlan = cellData.getValue();
            if (paymentPlan != null){
                //Add "DKK" to the end of the string.
                return new ReadOnlyObjectWrapper<>(String.format("%.2f DKK",
                        PaymentPlan.calcMonthlyPayment(
                                paymentPlan.getFixedCarPrice(),
                                paymentPlan.getDownPayment(),
                                paymentPlan.getMonthlyRent(),
                                paymentPlan.getPlanLength()
                        )
                ));
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        MonthlyPaymentColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        PlanLengthColumn.setCellValueFactory(cellData -> {
            PaymentPlan paymentPlan = cellData.getValue();
            if (paymentPlan != null) {
                return new ReadOnlyObjectWrapper<>(paymentPlan.getPlanLength());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        PlanLengthColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        RentColumn.setCellValueFactory(cellData -> {
            PaymentPlan paymentPlan = cellData.getValue();
            if (paymentPlan != null){
                //To add a percent char after the number you need to add 2 percent chars, because a single percent char represents a value when formatting strings
                return new ReadOnlyObjectWrapper<>(String.format("%.2f%%", paymentPlan.getMonthlyRent()));
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        RentColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        SalesmanFirstNameColumn.setCellValueFactory(cellData -> {
            Salesman salesman = cellData.getValue().getSalesman();
            if (salesman != null){
                return new ReadOnlyObjectWrapper<>(salesman.getFirstName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        SalesmanFirstNameColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        SalesmanLastNameColumn.setCellValueFactory(cellData -> {
            Salesman salesman = cellData.getValue().getSalesman();
            if (salesman != null){
                return new ReadOnlyObjectWrapper<>(salesman.getLastName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        SalesmanLastNameColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());

        StartDateColumn.setCellValueFactory(cellData -> {
            PaymentPlan paymentPlan = cellData.getValue();
            if (paymentPlan != null) {
                return new ReadOnlyObjectWrapper<>(paymentPlan.getStartDate()).asString();
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        StartDateColumn.setCellFactory(tv -> new PaymentPlanTableCellFactory<>());
    }

    /**
     * Filter paymentPlan table based on the word inputted into the {@link #SearchBar}.
     */
    private void filterTable() {
        //Searches for all cars that contain the searchbar contents
        ObservableList<PaymentPlan> foundPaymentPlan = search(SearchBar.getText());
        //Sets the car table to only show the search results
        PaymentPlanTable.setItems(foundPaymentPlan);
    }
}

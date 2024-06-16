package com.project.fflb.controllers.customer;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.dboData.CustomerData;
import com.project.fflb.dbo.Customer;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
 * Controller responsible for CustomerTable.fxml
 *
 * @author Simon
 */
public class CustomerTableController extends SceneController implements Initializable {
    // ========
    //   FXML
    // ========

    @FXML
    TableView<Customer> CustomerTable;
    @FXML
    TableColumn<Customer, String> EmailColumn;
    @FXML
    TableColumn<Customer, String> FirstNameColumn;
    @FXML
    TableColumn<Customer, String> LastNameColumn;
    @FXML
    TableColumn<Customer, Integer> PhoneColumn;
    @FXML
    TableColumn<Customer, Integer> PostcodeColumn;
    @FXML
    TableColumn<Customer, String> CreditScoreColumn;
    @FXML
    TableColumn<Customer, String> CPRColumn;
    @FXML
    TableColumn<Customer, String> AddressColumn;
    @FXML
    Button CreateCustomerButton, SelectCustomerButton, BackButton;
    @FXML
    TextField SearchBar;

    // =============
    //   VARIABLES
    // =============

    /**
     * Static variable with the currently selected customer, so other scene controllers can access it.
     */
    private static Customer selectedCustomer;

    // ===========
    //   METHODS
    // ===========

    /**
     * Static method that returns the selected customer. It is static such that it can be fetched from any other controller.
     * @return The selected {@link Customer} object
     */
    static Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    /**
     * Static method that sets the selected customer. It is static such that it can be set from any other controller.
     * @param newSelectedCustomer New {@link Customer} object
     */
    public static void setSelectedCustomer(Customer newSelectedCustomer){
        selectedCustomer = newSelectedCustomer;
    }

    /**
     * Filter the list of customers based on the keyword.
     * @param keyword Keyword to search by
     * @return {@link ObservableList} with resulting customers
     */
    public ObservableList<Customer> search(String keyword) {
        //List of all customers
        ArrayList<Customer> listOfCustomers = CustomerData.getAll();
        //Empty list
        ObservableList<Customer> searchResults = FXCollections.observableArrayList();
        //Loops through all customers
        for (Customer customer : listOfCustomers) {
            if (customer.getFirstName().toLowerCase().contains(keyword.toLowerCase()) || customer.getCPR().toLowerCase().contains(keyword.toLowerCase())) {
                //Adds customers that contain the keyword to the list of searchResults
                searchResults.add(customer);
            }
        }

        return searchResults;
    }

    /**
     * Called upon OnAction with the searchbar or the search button
     */
    public void searchButtonAction() {
        filterTable();
    }

    /**
     * Filter the list of customers based on what is typed in the {@link #SearchBar}
     */
    private void filterTable() {
        ObservableList<Customer> foundCustomer = search(SearchBar.getText());
        //Set the items in the table equal to the result of the search
        CustomerTable.setItems(foundCustomer);
    }

    /**
     * "Back" button - Switch back to the home page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToHomePage(ActionEvent event){
        switchToScene(event, "HomePage");
    }

    /**
     * "Create Customer" button - Switch to customer creation page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCustomerCreation(ActionEvent event) {
        switchToScene(event, "CustomerCreation");
    }

    /**
     * "Edit Customer" button - Switch to customer editing page.
     * @param event {@link ActionEvent} from button press
     */
    public void switchToCustomerInformation(ActionEvent event) {
        //Do nothing if no customer is selected
        if (selectedCustomer == null) {
            createErrorPopup("System alert", "No customer selected");
            return;
        }

        switchToScene(event, "CustomerInformation");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            updateTable();

            //Add an actionlistener to the customer table, which listens for a click on an element.
            //This in turn sets the selected customer to the selected element in the list.
            CustomerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        selectedCustomer = CustomerTable.getSelectionModel().getSelectedItem();
            });

            //Add a listener to the text property of the searchbar to filter the results whenever new information is typed in
            SearchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        } catch (Exception e) {
            System.out.println("Error initializing table: " + e.getMessage());
        }
    }

    /**
     * Updates the table of customers based on the contents of the database.
     */
    private void updateTable() {
        //Gets all customers from the database
        ArrayList<Customer> listOfCustomers = CustomerData.getAll();

        //Check if the connection was successful
        if (!CustomerData.hasConnection()) {
            createErrorPopup("Database Error", "Could not establish connection with database.");
            return;
        }

        //Sort the list of customers by their credit score
        listOfCustomers.sort(Comparator.comparing(Customer::getCreditScore));
        //Adds the sorted list to the tableview
        CustomerTable.getItems().addAll(listOfCustomers);

        //setCellValueFactory is used to decide what each cell in the column shows, since adding the list of customers to the table doesn't mean it shows the data
        EmailColumn.setCellValueFactory(cellData -> {
            //cellData contains a customer, since we added the list of customers to the table. All cells along a row has the same customer.
            Customer customer = cellData.getValue();
            if (customer != null) {
                //Returns an observable value, since that is what setCellValueFactory accepts. That observable value is the thing that the cell then shows.
                return new ReadOnlyObjectWrapper<>(customer.getEmail());
            } else {
                //Null handling
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        //Set the cell factory to our custom class that changes the background colour of the cell
        EmailColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());

        /*
         * The code beneath sets the columns contents the same way the above code does, so there is no need to describe that code again
         */

        FirstNameColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getFirstName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        FirstNameColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());

        LastNameColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getLastName());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        LastNameColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());

        PhoneColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getPhoneNumber());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        PhoneColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());

        PostcodeColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getPostCode());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        PostcodeColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());

        CPRColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getCPR());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        CPRColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());

        CreditScoreColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getCreditScore()).asString();
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        CreditScoreColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());

        AddressColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            if (customer != null) {
                return new ReadOnlyObjectWrapper<>(customer.getAddress());
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        });
        AddressColumn.setCellFactory(tv -> new CustomerTableCellFactory<>());
    }
}

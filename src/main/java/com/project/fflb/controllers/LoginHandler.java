package com.project.fflb.controllers;

import com.project.fflb.controllers.SceneController;
import com.project.fflb.data.DataHandler;
import com.project.fflb.data.dboData.LoginData;
import com.project.fflb.data.dboData.SalesmanData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginHandler extends SceneController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void switchToRegister(ActionEvent event) {
        switchToScene(event, "LoginForm");
    }


    public void setLoginButtonAction(ActionEvent event) {
        String inputUsername = usernameField.getText();
        String inputPhoneNumber = passwordField.getText();

        System.out.println(inputUsername);
        System.out.println(inputPhoneNumber);
        try {

            try {
                SalesmanData.getByName(inputUsername).getFirstName();
            } catch (NullPointerException e) {
                createErrorPopup("ERROR", "No such username in database");
                usernameField.clear();
                passwordField.clear();
                return;
            }

            boolean matching = LoginData.checkUsernameAndPhonenumberCompatability(inputUsername, inputPhoneNumber);

            if (matching) {
                switchToScene(event, "HomePage");
            } else {
                createErrorPopup("Login Error", "Username and PhoneNumber did not match");
                usernameField.clear();
                passwordField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

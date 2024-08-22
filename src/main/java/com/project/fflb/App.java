package com.project.fflb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main App class.
 *
 * @author Victor
 */

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //Load main page fxml
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("LoginForm.fxml"));
        //Set the current scene to the loaded fxml file
        Scene scene = new Scene(fxmlLoader.load());
        System.out.println("Loaded Home page");
        //Load the css file
        String css = App.class.getResource("HomePage.css").toExternalForm();
        //Add the css file
        scene.getStylesheets().add(css);
        System.out.println("Loaded Home page css file");
        //Set title of page
        stage.setTitle("Ferrari Loan Calculator");
        //Set the current stage to the newly created scene
        stage.setScene(scene);
        //Show the stage
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
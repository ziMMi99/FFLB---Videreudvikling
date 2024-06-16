package com.project.fflb.threading.api;

import com.project.fflb.controllers.APISceneController;
import com.project.fflb.enums.APICallType;
import com.project.fflb.enums.ThreadResult;

/**
 * Handles all threading for API calls.
 *
 * @author Simon
 */
public class APIHandler {
    /**
     * Controller to call upon its process finishing.
     */
    APISceneController controller;

    /**
     * Construct handler with a referenced controller.
     * @param controller Controller implementing the {@link APISceneController} interface, which will be notified upon the handler finishing its call.
     */
    public APIHandler(APISceneController controller) {
        setScene(controller);
    }

    /**
     * Executes a call to RKI. Instantiates a thread to make the call.
     * @param CPR CPR-number attached to the customer
     */
    public void executeAPICallToRKI(String CPR) {
        APIThread apiThread = new APIThread(this, APICallType.RKI, CPR);

        Thread thread = new Thread(apiThread);
        //So the process doesn't hang around if the user closes the program
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Executes a call to the bank. Instantiates a thread to make the call.
     */
    public void executeAPICallToBank() {
        APIThread apiThread = new APIThread(this, APICallType.BANK);

        Thread thread = new Thread(apiThread);
        //So the process doesn't hang around if the user closes the program
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Called upon the API thread finishing.
     *
     * @param result Result of the operation.
     * @param data Data fetched from API.
     */
    public void threadUpdate(ThreadResult result, APIData data) {
        if (controller != null) {
            controller.APIUpdate(result, data);
        } else {
            System.out.println("Handler unable to notify controller, because it is null.");
        }
    }

    /**
     * Set the scene controller that the handler refers to.
     * Any scene that uses this handler must implement the {@link APISceneController} interface.
     *
     * @param controller The scene controller the handler will notify after an operation finishes. Usually the scene controller passes itself.
     */
    public void setScene(APISceneController controller) {
        this.controller = controller;
    }
}

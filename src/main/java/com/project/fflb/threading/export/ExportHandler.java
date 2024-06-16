package com.project.fflb.threading.export;

import com.project.fflb.controllers.ExportSceneController;
import com.project.fflb.dbo.PaymentPlan;
import com.project.fflb.enums.ThreadResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles assigning threads to export payment plans to CSV.
 *
 * @author Simon
 */
public class ExportHandler {
    /**
     * The scene controller implementing {@link ExportSceneController} which is referenced by the handler
     * and notified upon it finishing the export operation.
     */
    ExportSceneController controller;

    public ExportHandler(ExportSceneController controller) {
        setScene(controller);
    }

    /**
     * Exports a payment plan to a .csv file.
     *
     * @param plan The payment plan to be exported
     * @param file The file location where the plan is being exported to
     */
    public void exportPlanToCSV(PaymentPlan plan, File file) {
        String path = file.getPath();
        //Check if filepath contains csv.
        if (!path.contains(".csv")) {
            if (controller != null) {
                controller.IOUpdate(ThreadResult.IOEXCEPTION);
            } else {
                System.out.println("Handler unable to notify controller, because it is null.");
            }

            return;
        }

        //Check if the directory of the file exists
        if (!Files.exists( Path.of( file.getParent() ) )) {
            if (controller != null) {
                controller.IOUpdate(ThreadResult.IOEXCEPTION);
            } else {
                System.out.println("Handler unable to notify controller, because it is null.");
            }
            return;
        }

        //Create thread
        CSVThread csvThread = new CSVThread(this, plan, file);

        Thread thread = new Thread(csvThread);
        //So the process doesn't hang around if the user closes the program
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Called by the thread upon the operation finishing.
     *
     * @param result Result of the operation.
     */
    public void threadUpdate(ThreadResult result) {
        if (controller != null) {
            controller.IOUpdate(result);
        } else {
            System.out.println("Handler unable to notify controller, because it is null.");
        }
    }

    /**
     * Set the scene controller that the handler refers to.
     * Any scene that uses this handler must implement the {@link ExportSceneController} interface.
     *
     * @param controller The scene controller the handler will notify after an operation finishes. Usually the scene controller passes itself.
     */
    public void setScene(ExportSceneController controller) {
        this.controller = controller;
    }
}

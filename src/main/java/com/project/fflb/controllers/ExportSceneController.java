package com.project.fflb.controllers;

import com.project.fflb.enums.ThreadResult;

/**
 * Implemented by any scene controllers that wish to make calls to {@link com.project.fflb.threading.export.ExportHandler},
 * which is responsible for exporting payment plans to CSV.
 *
 * <p> Its method is {@link #IOUpdate(ThreadResult)}, which is called upon the thread
 * finishing, and contains a result of the operation.
 *
 * @author Ebbe
 */
public interface ExportSceneController {
    /**
     * Called upon the export operation finishing.
     *
     * @param threadResult Result of the operation.
     */
    void IOUpdate(ThreadResult threadResult);
}
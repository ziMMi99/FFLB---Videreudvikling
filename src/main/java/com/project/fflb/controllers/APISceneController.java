package com.project.fflb.controllers;

import com.project.fflb.enums.ThreadResult;
import com.project.fflb.threading.api.APIData;

/**
 * Implemented by any scene controllers that wish to make calls to {@link com.project.fflb.threading.api.APIHandler},
 * which is responsible for fetching data from the API.
 *
 * <p> Its method is {@link #APIUpdate(ThreadResult, APIData)}, which is called upon the thread
 * finishing, and contains a result of the operation along with the data.
 *
 * @author Ebbe
 */
public interface APISceneController {
    /**
     * Called upon the api call finishing.
     *
     * @param result Result of the operation.
     * @param data Data fetched from the API call.
     */
    void APIUpdate(ThreadResult result, APIData data);
}

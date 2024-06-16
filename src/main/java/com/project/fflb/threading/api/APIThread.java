package com.project.fflb.threading.api;

import api.com.ferrari.finances.dk.rki.CreditRator;
import api.com.ferrari.finances.dk.rki.Rating;
import com.project.fflb.enums.APICallType;
import com.project.fflb.enums.ThreadResult;

/**
 * Runnable thread that fetches data from an API, based on the selected {@link APICallType}.
 * A CPR number in the form of a {@link String} must be supplied if making a call to get a customer's credit score.
 * Calls its own handler when process is finished, sending over the data.
 *
 * @author Ebbe
 */
public class APIThread implements Runnable {
    private APIHandler handler;
    private APICallType callType;
    private String CPR;

    /**
     * Use this constructor when making a call to get the interest rate.
     * @param handler {@link APIHandler} instance
     * @param callType The type of call to make (this should always be "BANK")
     */
    public APIThread(APIHandler handler, APICallType callType) {
        this.handler = handler;
        this.callType = callType;
    }
    /**
     * Use this constructor when making a call to get the credit score for a customer.
     * @param handler {@link APIHandler} instance
     * @param callType The type of call to make (this should always be "RKI")
     * @param CPR CPR number (String)
     */
    public APIThread(APIHandler handler, APICallType callType, String CPR) {
        this.handler = handler;
        this.callType = callType;
        this.CPR = CPR;
    }

    @Override
    public void run() {
        //If the handler is null, we're kinda stuck
        assert handler != null;

        //If critical data is null, return with an error
        if (callType == null) {
            handler.threadUpdate(ThreadResult.GENERICEXCEPTION, null);
            return;
        }

        APIData data; //Data wrapper we will return upon a successful call

        //Execute code based on the selected call type
        switch(callType) {
            case RKI:
                //CPR is critical for this call
                if (CPR == null) {
                    handler.threadUpdate(ThreadResult.GENERICEXCEPTION, null);
                    return;
                }

                //Call the API
                Rating score = CreditRator.i().rate(CPR);

                //In case the handler somehow became null during the process
                if (handler == null) {
                    System.out.println("Thread unable to notify handler because it is null.");
                    return;
                }

                //Return an error if API didn't return correct data
                if (score == null) {
                    handler.threadUpdate(ThreadResult.APIEXCEPTION, null);

                    return;
                }

                //Create data wrapper and send it back
                data = new APIData(score);
                handler.threadUpdate(ThreadResult.SUCCESS, data);

                return;
            case BANK:
                double rate = BankRateHandler.i().getRate();

                //In case the handler somehow became null during the process
                if (handler == null) {
                    System.out.println("Thread unable to notify handler because it is null.");
                    return;
                }

                //Return an error if API didn't return correct data
                if (rate <= 0.0) {
                    handler.threadUpdate(ThreadResult.APIEXCEPTION, null);
                    return;
                }

                //Create data wrapper and send it back
                data = new APIData(rate);
                handler.threadUpdate(ThreadResult.SUCCESS, data);

                return;
            default:
                //Somehow, the call type wasn't any we were expecting
                handler.threadUpdate(ThreadResult.GENERICEXCEPTION, null);
        }
    }
}

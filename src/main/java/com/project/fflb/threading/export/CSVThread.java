package com.project.fflb.threading.export;

import com.opencsv.CSVWriter;
import com.project.fflb.dbo.PaymentPlan;
import com.project.fflb.enums.ThreadResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Runnable thread that takes a payment plan and prints it to a spreadsheet in CSV-format.
 * Calls its own handler when the process is finished with the result of the operation.
 *
 * @author Ebbe
 * @Co-Author: Simon
 */
public class CSVThread implements Runnable {
    private ExportHandler handler;
    private PaymentPlan plan;
    private File file;

    public CSVThread(ExportHandler handler, PaymentPlan plan, File file) {
        this.handler = handler;
        this.plan = plan;
        this.file = file;
    }

    @Override
    public void run() {
        //If the handler is null, we're kinda stuck
        assert handler != null;

        if (plan == null || file == null) {
            handler.threadUpdate(ThreadResult.GENERICEXCEPTION);
            return;
        }

        /*
        Spreadsheet formatted as such:

        Monthly Rent | Loan Amount | Months          | Monthly Payment | Start Date
        -----------------------------------------------------------------------------
        [value]      | [value]     | [value]         | [value]         | [value]
        -----------------------------------------------------------------------------
        Month        | Loan Amount | Monthly Payment | Remaining       | Payment Date
        -----------------------------------------------------------------------------
        [value]      | [value]     | [value]         | [value]         | [value]
        ...

         */

        //Obtain information from the payment plan
        double loanAmount = plan.getCar().getPrice() - plan.getDownPayment();
        double monthlyRent = plan.getMonthlyRent();
        int months = plan.getPlanLength();
        double monthlyPayment = PaymentPlan.calcMonthlyPayment(plan.getFixedCarPrice(), plan.getDownPayment(), plan.getMonthlyRent(), plan.getPlanLength());
        Date startDate = plan.getStartDate();

        //Makes sure to instantiate our own version of the CSVWriter, to specify the separator.
        //Default separator is comma which is for english countries like UK, US and Australia.
        //In the EU the comma is reserved for decimal symbol, so the separator cannot be comma
        //So we have to specify that it is semicolon, or it will not work when exporting to excel
        try (CSVWriter writer = new CSVWriter(new FileWriter(file), ';', '"', '"', "\n")) {
            //For every line in the file, feed the writer an array of strings.

            //First two lines are headers for basic info
            String[] headerText = {
                    "Monthly Rent",
                    "Loan Amount",
                    "Months",
                    "Monthly Payment",
                    "Start Date"
            };

            //Write line
            writer.writeNext(headerText);

            String[] headerInfo = {
                    String.format("%.2f", monthlyRent),
                    String.format("%.2f", loanAmount),
                    Integer.toString(months),
                    String.format("%.2f", monthlyPayment),
                    startDate.toString()
            };

            writer.writeNext(headerInfo);

            //Following lines are for info regarding each month
            String[] monthsText = {
                    "Month",
                    "Loan Amount",
                    "Monthly Payment",
                    "Remaining",
                    "Payment Date"
            };

            writer.writeNext(monthsText);

            //Write a line for each month with numbers corresponding to the previously written headers
            for (int i = 0; i < months; i++) {
                loanAmount += loanAmount * (monthlyRent/100.0); //Add rent

                //Create string array with info about the month
                String[] monthsInfo = new String[5];

                monthsInfo[0] = Integer.toString(i+1);
                monthsInfo[1] = String.format("%.2f", loanAmount);
                monthsInfo[2] = String.format("%.2f", monthlyPayment);

                if (i == months-1) {
                    //Special case: Write 0 if we're at the end of the plan, as there should always be no more money to pay.
                    //This is so inaccuracies due to float-double conversions won't have an effect.
                    monthsInfo[3] = "0";
                } else {
                    loanAmount -= monthlyPayment; //Calculate remaining
                    monthsInfo[3] = String.format("%.2f", loanAmount);
                }

                //Insert date plus the amount of months along in the plan
                LocalDate localStartDate = startDate.toLocalDate();
                monthsInfo[4] = localStartDate.plusMonths(i).toString();

                //Write the info into the file before next loop
                writer.writeNext(monthsInfo);
            }

            writer.flush();

        } catch (IOException e) {
            System.out.println("Could not export file to csv: " + e.getMessage());

            //In case the handler somehow became null during the process
            if (handler == null) {
                System.out.println("Thread unable to notify handler because it is null.");
                return;
            }

            handler.threadUpdate(ThreadResult.IOEXCEPTION);
            return;
        }

        //In case the handler somehow became null during the process
        if (handler == null) {
            System.out.println("Thread unable to notify handler because it is null.");
            return;
        }

        handler.threadUpdate(ThreadResult.SUCCESS);
    }
}

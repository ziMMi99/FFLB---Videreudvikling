package com.project.fflb.dbo;

import api.com.ferrari.finances.dk.rki.Rating;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Holds information about a payment plan.
 *
 * @author Sebastian
 */
public class PaymentPlan {
    // ===============
    //   VARIABLES
    // ===============
    /**
     * ID equivalent to the primary key of the entity in the database
     */
    private int id;
    /**
     * Amount of money paid upfront on the plan starting.
     */
    private double downPayment;
    /**
     * Length of the plan in months.
     */
    private int planLength;
    //todo: the monthly rent should rely on the parameters like the rating
    /**
     * The percentage the remaining amount of payment will increase before the next month's payment.
     */
    private double monthlyRent;
    /**
     * The customer involved with the plan.
     */
    private Customer customer;
    /**
     * The seller responsible for the plan.
     */
    private Salesman salesman;
    /**
     * The car being sold.
     */
    private Car car;
    /**
     * The start date of the plan.
     */
    private Date startDate;
    /**
     * The car's price at the moment of the plan's creation.
     * Fluctuations in the car's price would affect the math for the rent and monthly payment, this it must be fixed from the start.
     */
    private double fixedCarPrice;

    // ===============
    //   CONSTRUCTORS
    // ===============

    //Constructor with ID
    public PaymentPlan(double downPayment, int planLength, double monthlyRent, Customer customer, Salesman salesman, Car car, Date startDate, double fixedCarPrice){
        this.downPayment = downPayment;
        this.planLength = planLength;
        this.monthlyRent = monthlyRent;
        this.customer = customer;
        this.salesman = salesman;
        this.car = car;
        this.startDate = startDate;
        this.fixedCarPrice = fixedCarPrice;
    }

    //Constructor without ID
    public PaymentPlan(int id, double downPayment, int planLength, double monthlyRent, Customer customer, Salesman salesman, Car car, Date startDate, double fixedCarPrice){
        this.id = id;
        this.downPayment = downPayment;
        this.planLength = planLength;
        this.monthlyRent = monthlyRent;
        this.customer = customer;
        this.salesman = salesman;
        this.car = car;
        this.startDate = startDate;
        this.fixedCarPrice = fixedCarPrice;
    }

    /**
     * Simple copy-constructor.
     * @param paymentPlan Object to copy
     */
    public PaymentPlan(PaymentPlan paymentPlan){
        this.id = paymentPlan.id;
        this.downPayment = paymentPlan.downPayment;
        this.planLength = paymentPlan.planLength;
        this.monthlyRent = paymentPlan.monthlyRent;
        this.startDate = paymentPlan.startDate;
        this.fixedCarPrice = paymentPlan.fixedCarPrice;
        //Use copy-constructors
        this.customer = new Customer(paymentPlan.customer);
        this.salesman = new Salesman(paymentPlan.salesman);
        this.car = new Car(paymentPlan.car);
    }

    // ===============
    //   SETTERS
    // ===============

    public void setId(int id) {
        this.id = id;
    }

    public void setDownPayment(double downPayment) {
        this.downPayment = downPayment;
    }

    public void setPlanLength(int planLength) {
        this.planLength = planLength;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setSalesman(Salesman salesman) {
        this.salesman = salesman;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setFixedCarPrice(double fixedCarPrice) {
        this.fixedCarPrice = fixedCarPrice;
    }

    // ===============
    //   GETTERS
    // ===============

    public int getId() {
        return id;
    }

    public double getDownPayment() {
        return downPayment;
    }

    public int getPlanLength() {
        return planLength;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Salesman getSalesman() {
        return salesman;
    }

    public Car getCar() {
        return car;
    }

    public Date getStartDate() {
        return startDate;
    }

    public double getFixedCarPrice() {
        return fixedCarPrice;
    }

    // ===============
    //   OTHER METHODS
    // ===============

    /**
     * Calculates the amount of money the customer has to pay each month.
     * @return Monthly payment in a double
     */
    public static double calcMonthlyPayment(double carPrice, double downPayment, double monthlyRent, int planLength) {
        //Guard clauses
        if (downPayment < 0) { return 0; }
        //Can't pay more than the car is worth
        if (downPayment >= carPrice) { return 0; }
        if (monthlyRent <= 0) { return 0; }
        if (planLength <= 0) { return 0; }

        //PMT is what the customer has to pay each month
        double PMT;
        //P is the size of the loan
        double P = carPrice - downPayment;
        //r is the monthly rent
        double r = monthlyRent/100;
        //n is the amount of months in the plan
        double n = planLength;

        /*  Formula for calculating the monthly payment
         *         P * r * (1 + r)^n
         *  PMT = -------------------
         *          (1 + r)^n - 1
         */
        PMT = (P * r * Math.pow((1 + r), n)) / (Math.pow((1 + r), n) - 1);

        return PMT;
    }

    /**
     * Calculate the monthly rent from the bank's interest rate, based on factors like
     * the customer's credit score and the length of the plan.
     *
     * @param baseRent Interest rate reported by the bank.
     * @return Monthly rent.
     */
    public static double calcMonthlyRent(double baseRent, Customer customer, Car car, double downPayment, int planLength) {
        if (car == null) { return 0; }
        if (customer == null) { return 0; }
        if (downPayment < 0) { return 0; }
        //Can't pay more than the car is worth
        if (downPayment >= car.getPrice()) { return 0; }
        if (baseRent <= 0) { return 0; }
        if (planLength <= 0) { return 0; }

        //Depending on customer credit score
        if (customer.getCreditScore() == Rating.A) {
            baseRent += 1;
        } else if (customer.getCreditScore() == Rating.B) {
            baseRent += 2;
        } else if (customer.getCreditScore() == Rating.C) {
            baseRent += 3;
        } else if (customer.getCreditScore() == Rating.D) {
            //Don't serve customers with D-rating
            return 0;
        }

        //If down payment is under half of the car's price
        if ( downPayment < (car.getPrice() * 0.5) ) {
            baseRent += 1;
        }

        //If plan lasts more than 3 years
        if (planLength > 36) {
            baseRent += 1;
        }

        return baseRent;
    }

    /**
     * Check if a payment plan has been finished.
     * @return True if the current date has reached the expected end date of the plan.
     */
    public boolean isFinished() {
        //Convert from Date to LocalDate
        LocalDate localStartedDate = startDate.toLocalDate();
        //Add the plan length to the date
        LocalDate dateAtPlanEnd = localStartedDate.plusMonths(planLength);

        //Check if the date now, is after X months of the original start date.
        return !LocalDate.now().isBefore(dateAtPlanEnd);
    }

    /**
     * Check if a plan has started.
     * @return True if the plan's start date is after or equal to the current date.
     */
    public boolean isStarted() {
        LocalDate localStartedDate = startDate.toLocalDate();

        //Return true of the start date is NOT after the current date
        return !localStartedDate.isAfter(LocalDate.now());
    }

    public String toString() {
        return String.format("Payment plan for: %s %s | Plan Length: %d months | Monthly rent: %.2f%%",
                customer.getFirstName(), customer.getLastName(), planLength, monthlyRent);
    }
}

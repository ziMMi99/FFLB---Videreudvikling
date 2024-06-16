package com.project.fflb.dbo;

import api.com.ferrari.finances.dk.rki.Rating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ebbe
 * @Co-Author Victor
 */
class PaymentPlanTest {
    /**
     * Test for intended values
     */
    @Test
    void testCalcMonthlyPayment() {
        Car car = createStubCar();
        car.setPrice(200500);
        double downPayment = 50000;
        int planLength = 12;
        double monthlyRent = 3;
        double monthlyPayment;

        monthlyPayment = PaymentPlan.calcMonthlyPayment(car.getPrice(), downPayment, monthlyRent, planLength);

        //Number derived from formula
        assertEquals(15119.54, monthlyPayment, 0.05);
        System.out.println("Expected: 15119.54, Received: " + monthlyPayment);
    }

    /**
     * Test that a negative values will be handled correctly
     */
    @Test
    void testCalcMonthlyPayment_NegativeValues() {
        Car car = createStubCar();
        car.setPrice(200500);
        double downPayment = -5000;
        int planLength = -1;
        double monthlyRent = -3;
        double monthlyPayment;

        monthlyPayment = PaymentPlan.calcMonthlyPayment(car.getPrice(), downPayment, monthlyRent, planLength);

        assertEquals(0, monthlyPayment, 0.05);
        System.out.println("Expected: 0, Received: " + monthlyPayment);
    }

    /**
     * Test monthly rent for a customer with a rating of A
     */
    @Test
    void testCalcMonthlyRent_Customer_A() {
        Customer customer = createStubCustomer();
        customer.setCreditScore(Rating.A);
        Car car = createStubCar();
        car.setPrice(200500);
        double downPayment = 50000;
        int planLength = 12;
        double baseRent;

        baseRent = PaymentPlan.calcMonthlyRent(4, customer, car, downPayment, planLength);

        assertEquals(6, baseRent);
        System.out.println("Expected: 6, Received: " + baseRent);
    }

    /**
     * Test monthly rent for a customer with a rating of B
     */
    @Test
    void testCalcMonthlyRent_Customer_B() {
        Customer customer = createStubCustomer();
        customer.setCreditScore(Rating.B);
        Car car = createStubCar();
        car.setPrice(200500);
        double downPayment = 50000;
        int planLength = 12;
        double baseRent;

        baseRent = PaymentPlan.calcMonthlyRent(4, customer, car, downPayment, planLength);

        assertEquals(7, baseRent);
        System.out.println("Expected: 7, Received: " + baseRent);
    }

    /**
     * Test monthly rent for a customer with a rating of C
     */
    @Test
    void testCalcMonthlyRent_Customer_C() {
        Customer customer = createStubCustomer();
        customer.setCreditScore(Rating.C);
        Car car = createStubCar();
        car.setPrice(200500);
        double downPayment = 50000;
        int planLength = 12;
        double baseRent;

        baseRent = PaymentPlan.calcMonthlyRent(4, customer, car, downPayment, planLength);

        assertEquals(8, baseRent);
        System.out.println("Expected: 8, Received: " + baseRent);
    }

    /**
     * Test monthly rent for a customer with a rating of D
     */
    @Test
    void testCalcMonthlyRent_Customer_D() {
        Customer customer = createStubCustomer();
        customer.setCreditScore(Rating.D);
        Car car = createStubCar();
        car.setPrice(200500);
        double downPayment = 50000;
        int planLength = 12;
        double baseRent;

        baseRent = PaymentPlan.calcMonthlyRent(4, customer, car, downPayment, planLength);

        assertEquals(0, baseRent);
        System.out.println("Expected: 0, Received: " + baseRent);
    }

    Car createStubCar() {
        return new Car("Stub", 0, false);
    }

    Customer createStubCustomer() {
        return new Customer(
                "Stub",
                "Stubson",
                "StubEmail@gmail.com",
                0,
                "Stub Street 21",
                0,
                "0000000000",
                Rating.A
        );
    }
}
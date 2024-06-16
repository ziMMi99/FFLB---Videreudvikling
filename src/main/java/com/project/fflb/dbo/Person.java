package com.project.fflb.dbo;

/**
 * An abstract class, that has all the features/elements a person should have.
 *
 * @author Sebastian
 */
public abstract class Person {
    // =============
    //   VARIABLES
    // =============
    //todo: personID should probably communicate with the database to ensure that the ID isn't already in use
    /**
     * ID equivalent to the primary key of the entity in the database
     */
    private int personID;
    private int phoneNumber;
    private String firstName, lastName, email;

    // ===============
    //   CONSTRUCTOR
    // ===============

    //Constructor with ID
    public Person(int personID, String firstName, String lastName, String email, int phoneNumber){
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    //Constructor without ID
    public Person(String firstName, String lastName, String email, int phoneNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    //Copy-constructor
    public Person(Person person){
        this.personID = person.personID;
        this.firstName = person.firstName;
        this.lastName = person.lastName;
        this.email = person.email;
        this.phoneNumber = person.phoneNumber;
    }

    // ===========
    //   SETTERS
    // ===========
    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ===========
    //   GETTERS
    // ===========
    public int getPersonID() {
        return personID;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }


}

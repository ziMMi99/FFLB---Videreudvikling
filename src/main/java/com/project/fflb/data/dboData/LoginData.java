package com.project.fflb.data.dboData;

import com.project.fflb.data.DataHandler;
import com.project.fflb.dbo.Salesman;

public class LoginData extends DataHandler {

    public static boolean checkUsernameAndPhonenumberCompatability(String username, String phoneNumber) {
        Salesman salesman = SalesmanData.getByName(username);

        System.out.println(salesman);
        return (salesman.getFirstName().equals(username) && String.valueOf(salesman.getPhoneNumber()).equals(phoneNumber));

    }
}

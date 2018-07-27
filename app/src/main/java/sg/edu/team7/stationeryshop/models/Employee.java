package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;

public class Employee extends HashMap<String, String> implements Serializable {

    public Employee(String name, String email) {
        put("name", name);
        put("email", email);
    }
}

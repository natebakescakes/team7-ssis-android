package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class DepartmentOptions extends HashMap<String, Object> implements Serializable {

    public DepartmentOptions(String department, String representative, List<Delegation> delegations) {
        put("department", department);
        put("representative", representative);
        put("delegations", delegations);
    }
}
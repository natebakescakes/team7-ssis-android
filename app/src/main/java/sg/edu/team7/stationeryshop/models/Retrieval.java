package sg.edu.team7.stationeryshop.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import sg.edu.team7.stationeryshop.fragments.StationeryRetrievalFragment;

public class Retrieval extends HashMap<String, Object> implements Serializable {

    public Retrieval(String retrievalId, String createdBy, String createdDate, String status, List<RetrievalDetailByDept> retrievalDetails) {
        put("retrievalId", retrievalId);
        put("createdBy", createdBy);
        put("createdDate", createdDate);
        put("status", status);
        put("retrievalDetails", retrievalDetails);
    }

    public static List<Retrieval> findAllRetrievals(StationeryRetrievalFragment fragment) {
        return null;
    }
}

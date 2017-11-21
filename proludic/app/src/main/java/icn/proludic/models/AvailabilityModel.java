package icn.proludic.models;

import java.util.List;

/**
 * Author:  Bradley Wilson
 * Date: 02/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class AvailabilityModel {

    private List<String> availableNames;
    private String parkType;

    public AvailabilityModel(List<String> availableNames, String parkType) {
        this.availableNames = availableNames;
        this.parkType = parkType;
    }

    public List<String> getAvailableNames() {
        return availableNames;
    }

    public String getParkType() {
        return parkType;
    }
}

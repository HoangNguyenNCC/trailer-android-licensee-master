package devx.app.licensee.webapi.response.requelist;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddress {
    private String text,pincode;
    private List<Double> coordinates=new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}

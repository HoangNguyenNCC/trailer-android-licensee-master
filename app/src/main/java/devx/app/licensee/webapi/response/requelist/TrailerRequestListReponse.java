package devx.app.licensee.webapi.response.requelist;

import java.util.ArrayList;
import java.util.List;

public class TrailerRequestListReponse {
    private boolean success;
    private String message;
    private List<RentalRequest> requestList= new ArrayList<RentalRequest>();

    private RentalObj rentalObj=new RentalObj();

    public RentalObj getRentalObj() {
        return rentalObj;
    }

    public void setRentalObj(RentalObj rentalObj) {
        this.rentalObj = rentalObj;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RentalRequest> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<RentalRequest> requestList) {
        this.requestList = requestList;
    }
}


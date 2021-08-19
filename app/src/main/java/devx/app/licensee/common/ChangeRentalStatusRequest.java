package devx.app.licensee.common;

public class ChangeRentalStatusRequest {
    String rentalId;
    String status;

    public ChangeRentalStatusRequest(String rentalId, String status) {
        this.rentalId = rentalId;
        this.status = status;
    }
}

package devx.app.licensee.webapi.response.requelist;

import java.util.ArrayList;
import java.util.List;

public class RentalRequest {
    private String customerId,customerName,licenseeName,
            rentalId,requestType,customerPhoto,rentalPeriodStart,
            rentalPeriodEnd,licenseeID,revisionId;
    private int isApproved,invoiceNumber;
    private double total;
    private List<RentelItems> rentedItems= new ArrayList<RentelItems>();
    private LicenseeAddress licenseeAddress=new LicenseeAddress();
    private CustomerAddress customerAddress=new CustomerAddress();
    private boolean todayDropOff, todayPickUp;


    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }



    public String getLicenseeID() {
        return licenseeID;
    }

    public void setLicenseeID(String licenseeID) {
        this.licenseeID = licenseeID;
    }

    public String getRentalPeriodStart() {
        return rentalPeriodStart;
    }

    public void setRentalPeriodStart(String rentalPeriodStart) {
        this.rentalPeriodStart = rentalPeriodStart;
    }

    public String getRentalPeriodEnd() {
        return rentalPeriodEnd;
    }

    public void setRentalPeriodEnd(String rentalPeriodEnd) {
        this.rentalPeriodEnd = rentalPeriodEnd;
    }

    public String getCustomerPhoto() {
        return customerPhoto;
    }

    public void setCustomerPhoto(String customerPhoto) {
        this.customerPhoto = customerPhoto;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLicenseeName() {
        return licenseeName;
    }

    public void setLicenseeName(String licenseeName) {
        this.licenseeName = licenseeName;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public List<RentelItems> getRentedItems() {
        return rentedItems;
    }

    public void setRentedItems(List<RentelItems> rentedItems) {
        this.rentedItems = rentedItems;
    }

    public LicenseeAddress getLicenseeAddress() {
        return licenseeAddress;
    }

    public void setLicenseeAddress(LicenseeAddress licenseeAddress) {
        this.licenseeAddress = licenseeAddress;
    }

    public CustomerAddress getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(CustomerAddress customerAddress) {
        this.customerAddress = customerAddress;
    }

    public boolean isTodayDropOff() {
        return todayDropOff;
    }

    public void setTodayDropOff(boolean todayDropOff) {
        this.todayDropOff = todayDropOff;
    }

    public boolean isTodayPickUp() {
        return todayPickUp;
    }

    public void setTodayPickUp(boolean todayPickUp) {
        this.todayPickUp = todayPickUp;
    }
}

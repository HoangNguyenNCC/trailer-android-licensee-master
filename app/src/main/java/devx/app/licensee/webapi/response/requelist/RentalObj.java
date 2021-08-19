package devx.app.licensee.webapi.response.requelist;

import java.util.ArrayList;
import java.util.List;

public class RentalObj {
    private String customerId,customerName,licenseeName,
            rentalId,requestType,customerPhoto,rentalPeriodStart,
            rentalPeriodEnd,licenseeId,revisionId,_id,bookedByUserId, rentalStatus;
    private int isApproved,invoiceNumber;
    private double total;
    private List<RentelItems> rentedItems= new ArrayList<RentelItems>();
    private LicenseeAddress licenseeAddress=new LicenseeAddress();
    private CustomerAddress customerAddress=new CustomerAddress();
    private RentalPeriod rentalPeriod=new RentalPeriod();
    private BookedByUser bookedByUser=new BookedByUser();
    private boolean isTrackingPickUp,isTrackingDropOff,isPickUp;
    private List<Revisions> revisions= new ArrayList<Revisions>();

    private LicenseeAddress pickUpLocation=new LicenseeAddress();
    private LicenseeAddress dropOffLocation=new LicenseeAddress();

    public LicenseeAddress getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(LicenseeAddress pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public LicenseeAddress getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(LicenseeAddress dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }
    public List<Revisions> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<Revisions> revisions) {
        this.revisions = revisions;
    }

    public RentalPeriod getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(RentalPeriod rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public BookedByUser getBookedByUser() {
        return bookedByUser;
    }

    public void setBookedByUser(BookedByUser bookedByUser) {
        this.bookedByUser = bookedByUser;
    }

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

    public String getLicenseeId() {
        return licenseeId;
    }

    public void setLicenseeId(String licenseeId) {
        this.licenseeId = licenseeId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBookedByUserId() {
        return bookedByUserId;
    }

    public void setBookedByUserId(String bookedByUserId) {
        this.bookedByUserId = bookedByUserId;
    }

    public boolean isTrackingPickUp() {
        return isTrackingPickUp;
    }

    public void setTrackingPickUp(boolean trackingPickUp) {
        isTrackingPickUp = trackingPickUp;
    }

    public boolean isTrackingDropOff() {
        return isTrackingDropOff;
    }

    public void setTrackingDropOff(boolean trackingDropOff) {
        isTrackingDropOff = trackingDropOff;
    }

    public boolean isPickUp() {
        return isPickUp;
    }

    public void setPickUp(boolean pickUp) {
        isPickUp = pickUp;
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

    public String getRentalStatus() {
        return rentalStatus;
    }
}

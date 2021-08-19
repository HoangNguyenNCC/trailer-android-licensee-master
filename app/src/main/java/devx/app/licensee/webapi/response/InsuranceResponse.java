package devx.app.licensee.webapi.response;

import devx.app.seller.webapi.response.trailerslist.Photos;

public class InsuranceResponse {
    private String itemType,_id,itemId,licenseeId,issueDate,expiryDate,document,insuranceRef;
    private String name,nextDueDate,serviceDate,servicingRef,photo,trailerName;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(String nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServicingRef() {
        return servicingRef;
    }

    public void setServicingRef(String servicingRef) {
        this.servicingRef = servicingRef;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getLicenseeId() {
        return licenseeId;
    }

    public void setLicenseeId(String licenseeId) {
        this.licenseeId = licenseeId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getInsuranceRef() {
        return insuranceRef;
    }

    public void setInsuranceRef(String insuranceRef) {
        this.insuranceRef = insuranceRef;
    }
}

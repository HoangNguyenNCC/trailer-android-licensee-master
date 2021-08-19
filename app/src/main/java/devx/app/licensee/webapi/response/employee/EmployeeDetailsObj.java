package devx.app.licensee.webapi.response.employee;

import java.util.ArrayList;
import java.util.Map;

import devx.app.licensee.modules.inviteEmployees.PermissionItem;
import devx.app.seller.webapi.response.Address;

public class EmployeeDetailsObj {
    private String _id,email,mobile,photo,name;
    private String address,dob;
    private Boolean isMobileVerified,isEmailVerified;
    private Map<String, ArrayList<String>> acl;
    private ArrayList<PermissionItem> arrACL;
    private Boolean isOwner;

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMobileVerified() {
        return isMobileVerified;
    }

    public void setMobileVerified(Boolean mobileVerified) {
        isMobileVerified = mobileVerified;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    public ArrayList<PermissionItem> getArrACL() {
        return arrACL;
    }

    public void setArrACL(ArrayList<PermissionItem> arrACL) {
        this.arrACL = arrACL;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Map<String, ArrayList<String>> getAcl() {
        return acl;
    }

    public void setAcl(Map<String, ArrayList<String>> acl) {
        this.acl = acl;
    }
}

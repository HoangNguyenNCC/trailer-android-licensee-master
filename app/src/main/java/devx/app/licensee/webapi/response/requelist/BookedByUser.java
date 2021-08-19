package devx.app.licensee.webapi.response.requelist;

import devx.app.seller.webapi.response.DriverLicense;
import devx.app.seller.webapi.response.trailerslist.Photos;

public class BookedByUser {

    private String name,mobile;
    private Photos photo;

    private CustomerAddress address=new CustomerAddress();
    private DriverLicense driverLicense;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Photos getPhoto() {
        return photo;
    }

    public void setPhoto(Photos photo) {
        this.photo = photo;
    }

    public CustomerAddress getAddress() {
        return address;
    }

    public void setAddress(CustomerAddress address) {
        this.address = address;
    }

    public DriverLicense getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(DriverLicense driverLicense) {
        this.driverLicense = driverLicense;
    }
}

package devx.app.licensee.webapi.response.requelist;

import devx.app.licensee.webapi.response.MyProfile.trailerFinancial.TotalCharges;
import devx.app.seller.webapi.response.trailerslist.Photos;

public class RentelItems {
    private String itemId,name,itemType,itemName;
    private Photos photo;
    private Photos itemPhoto;
    private TotalCharges totalCharges;

    public Photos getPhotos() {
        return photo;
    }

    public void setPhotos(Photos photos) {
        this.photo = photos;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Photos getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(Photos itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Photos getPhoto() {
        return photo;
    }

    public void setPhoto(Photos photo) {
        this.photo = photo;
    }

    public TotalCharges getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(TotalCharges totalCharges) {
        this.totalCharges = totalCharges;
    }
}

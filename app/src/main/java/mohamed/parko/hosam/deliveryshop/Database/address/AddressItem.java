package mohamed.parko.hosam.deliveryshop.Database.address;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;


@Entity(tableName = "Address" , primaryKeys = {"uid", "addressName"})
public class AddressItem {

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @NonNull
    @ColumnInfo(name = "addressName")
    private String addressName;

    @ColumnInfo(name = "buildNumber")
    private String buildNumber;

    @ColumnInfo(name = "buildName")
    private String buildName;

    @ColumnInfo(name = "streetName")
    private String streetName;

    @ColumnInfo(name = "floorNumber")
    private String floorNumber;

    @ColumnInfo(name = "flatNumber")
    private String flatNumber;

    @ColumnInfo(name = "zone")
    private String zone;

    @ColumnInfo(name = "details")
    private String details;


    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(@NonNull String addressName) {
        this.addressName = addressName;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

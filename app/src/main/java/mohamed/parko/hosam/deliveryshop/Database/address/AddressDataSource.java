package mohamed.parko.hosam.deliveryshop.Database.address;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface AddressDataSource {

    Flowable<List<AddressItem>> getAllAddresses(String uid);

    Single<AddressItem> getItemInAddress(String addressName , String uid);

    Completable insertOrReplaceAll(AddressItem address);

    Single<Integer> updateAddressItem(AddressItem address);

    Single<Integer> deleteAddressItem(AddressItem address);

    Single<AddressItem> getItemWithAllOptionsInAddress(String uid , String addressName,String streetName , String zone);

    Flowable<List<String>> getHeaderForAllAddresses(String uid);

}

package mohamed.parko.hosam.deliveryshop.Database.address;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


public class LocalAddressDatabase implements AddressDataSource {

    AddressDao addressDao;

    public LocalAddressDatabase(AddressDao addressDao) {
        this.addressDao = addressDao;
    }


    @Override
    public Flowable<List<AddressItem>> getAllAddresses(String uid) {
        return addressDao.getAllAddresses(uid);
    }

    @Override
    public Single<AddressItem> getItemInAddress(String addressName, String uid) {
        return addressDao.getItemInAddress(addressName, uid);
    }

    @Override
    public Completable insertOrReplaceAll(AddressItem address) {
        return addressDao.insertOrReplaceAll(address);
    }

    @Override
    public Single<Integer> updateAddressItem(AddressItem address) {
        return addressDao.updateAddressItem(address);
    }


    @Override
    public Single<Integer> deleteAddressItem(AddressItem address) {
        return addressDao.deleteAddressItem(address);
    }


    @Override
    public Single<AddressItem> getItemWithAllOptionsInAddress(String uid, String addressName, String streetName, String zone) {
        return addressDao.getItemWithAllOptionsInAddress(uid, addressName, streetName, zone);
    }

    @Override
    public Flowable<List<String>> getHeaderForAllAddresses(String uid) {
        return addressDao.getHeaderForAllAddresses(uid);
    }

}

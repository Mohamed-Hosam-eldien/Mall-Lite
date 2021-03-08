package mohamed.parko.hosam.deliveryshop.Database.address;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


@Dao
public interface AddressDao {

    @Query("SELECT * FROM Address WHERE uid=:uid")
    Flowable<List<AddressItem>> getAllAddresses(String uid);

    @Query("SELECT * FROM Address WHERE addressName =:addressName AND uid=:uid")
    Single<AddressItem> getItemInAddress(String addressName , String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(AddressItem address);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateAddressItem(AddressItem address);

    @Delete
    Single<Integer> deleteAddressItem(AddressItem address);

    @Query("SELECT * FROM Address WHERE uid=:uid AND addressName=:addressName AND streetName=:streetName AND zone=:zone")
    Single<AddressItem> getItemWithAllOptionsInAddress(String uid , String addressName,String streetName , String zone);

    @Query("SELECT addressName FROM Address WHERE uid=:uid")
    Flowable<List<String>> getHeaderForAllAddresses(String uid);

}

package mohamed.parko.hosam.deliveryshop.Database.address;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;



@Database(version = 1, entities = AddressItem.class, exportSchema = false)
public abstract class AddressDatabase extends RoomDatabase {

    public abstract AddressDao addressDao();
    private static AddressDatabase instance;

    public static AddressDatabase getInstance(Context context) {

        if(instance == null)
            instance = Room.databaseBuilder(context, AddressDatabase.class, "delivery_shop2").build();

        return instance;

    }

}

package mohamed.parko.hosam.deliveryshop.Database.favorite;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(version = 1, entities = FavoriteItem.class, exportSchema = false)
public abstract class FavoriteDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();
    private static FavoriteDatabase instance;

    public static FavoriteDatabase getInstance(Context context) {

        if(instance == null)
            instance = Room.databaseBuilder(context, FavoriteDatabase.class, "delivery_shop3").build();

        return instance;

    }

}

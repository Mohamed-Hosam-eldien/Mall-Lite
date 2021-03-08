package mohamed.parko.hosam.deliveryshop.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;
import mohamed.parko.hosam.deliveryshop.Model.TokenModel;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.R;

public class Common {

    public static final String UserReference = "Users";
    public static final String POPULAR_CATEGORY = "Popular";
    public static final int DEFAULT_COMMON_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static final String CATEGORY_REF = "Category";
    public static final String COMMENT_REF = "Comments";
    public static final String LIKE_REF = "Likes";
    public static final String TOKEN_REF = "Tokens";
    public static UserModel currentUser;
    public static CategoryModel Category_Selected;
    public static FoodModel SelectedFood;
    public static String order_ref = "Orders";
    public static String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "content";
    public static String currentToken = "";
    public static String orderCustomerRef = "OrderOnCustomer";
    public static String banner_ref = "Banner";
    public static String IS_OPEN_ORDER_ACTIVITY = "isOpenOrderActivity";
    public static String discount_ref = "discount";
    public static String chat_ref = "chat";
    public static String message_details = "chatDetails";
    public static String IS_OPEN_MessageActivity = "isOpenMessageActivity";

    public static final String IS_SEND_MESSAGE = "isSendMessage";
    public static String ImageUrL = "imageUrl";



    public static String createOrderNumber() {

        return String.valueOf(System.currentTimeMillis()) + // current time in millisecond
                Math.abs(new Random().nextInt()); // generate random number

    }

    public static String getDayOfWeek(int i) {

        switch (i) {

            case 2:
                return "الأثنين";

            case 3:
                return "الثلاثاء";

            case 4:
                return "الأربعاء";

            case 5:
                return "الخميس";

            case 6:
                return "الجمعة";

            case 7:
                return "السبت";

            case 1:
                return "الأحد";

            default:
                return "";

        }

    }

    public static String convertNumberToState(int orderState) {

        switch (orderState) {

            case 1:
                return "قيد الإنتظار..";

            case 2:
                return "جاري الفحص..";

            case 3:
                return "تمت الموافقة";

            case 4:
                return "جاري التوصيل..";

            case 5:
                return "تم التوصيل";

            case 6:
                return "تم الإلغاء";

            default:
                return "";

        }

    }

    public static void showNotification(Context context, int id, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String NOTIFICATION_CHANNEL_ID = "mohamed.parko.hosam.deliveryshop";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "delivery_shop", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("delivery_shop");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.ic_noti_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_noti_icon));

            if(pendingIntent != null)
                builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();
            notificationManager.notify(id,notification);


        } else {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "delivery_shop");

            notificationBuilder.setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_noti_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_noti_icon))
                    .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setVibrate(new long[]{0,1000,500,1000})
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManagerOldVersion = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManagerOldVersion.notify(id, notificationBuilder.build());

        }

    }

    public static void showNotificationWithImage(Context context, int id, String title, String content, Bitmap bitmap, Intent intent) {

        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        String NOTIFICATION_CHANNEL_ID = "mohamed.parko.hosam.deliveryshop";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "delivery_shop", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("delivery_shop");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setLargeIcon(bitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigLargeIcon(bitmap))
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.ic_noti_icon);

            if(pendingIntent != null)
                builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();
            notificationManager.notify(id,notification);


        } else {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "delivery_shop");

            notificationBuilder.setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_noti_icon)
                    .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                    .setContentTitle(title)
                    .setContentText(content)
                    .setLargeIcon(bitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigLargeIcon(bitmap))
                    .setVibrate(new long[]{0,1000,500,1000})
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManagerOldVersion = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManagerOldVersion.notify(id, notificationBuilder.build());

        }

    }


    public static void updateToken(String newToken, String uid) {

        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REF)
                .child(uid)
                .setValue(new TokenModel(Common.currentUser.getPhone(), newToken, "off",false))
                .addOnCompleteListener(task -> Log.d("TOKEN", newToken))
                .addOnFailureListener(e -> Log.d("TOKEN_ERROR",""+e.getMessage()));

    }


}

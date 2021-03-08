package mohamed.parko.hosam.deliveryshop.Services;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.MainActivity;



public class FCMServices extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Map<String,String> dataRecv = remoteMessage.getData();
        if(dataRecv != null) {

            if(dataRecv.get(Common.IS_SEND_MESSAGE) != null && dataRecv.get(Common.IS_SEND_MESSAGE).equals("true")) {

                Bitmap bitmap = getBitmapFromURL(dataRecv.get(Common.ImageUrL));

                Intent intent = new Intent(FCMServices.this, MainActivity.class);
                Common.showNotificationWithImage(FCMServices.this, new Random().nextInt(),
                        dataRecv.get(Common.NOTI_TITLE),
                        dataRecv.get(Common.NOTI_CONTENT),
                        bitmap,
                        intent);
                /*Glide.with(this)
                        .asBitmap()
                        .load(dataRecv.get(Common.ImageUrL))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Intent intent = new Intent(FCMServices.this, MainActivity.class);
                                Common.showNotificationWithImage(FCMServices.this, new Random().nextInt(),
                                        dataRecv.get(Common.NOTI_TITLE),
                                        dataRecv.get(Common.NOTI_CONTENT),
                                        resource,
                                        intent);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {}});*/

            } else {

                if (dataRecv.get(Common.NOTI_CONTENT).equals("جاري فحص طلبك الآن..") ||
                        dataRecv.get(Common.NOTI_CONTENT).equals("تم قبول طلبك ، سيتم التواصل معك بعد قليل..") ||
                        dataRecv.get(Common.NOTI_CONTENT).equals("تم شحن طلبك ، مندوبنا في الطريق إليك الآن.") ||
                        dataRecv.get(Common.NOTI_CONTENT).equals("قام مندوبنا بتوصيل الطلب لك بنجاح")) {

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(Common.IS_OPEN_ORDER_ACTIVITY, true);
                    Common.showNotification(this, new Random().nextInt(),
                            dataRecv.get(Common.NOTI_TITLE),
                            dataRecv.get(Common.NOTI_CONTENT),
                            intent);

                }
                else if (dataRecv.get(Common.NOTI_TITLE).equals("رسالة جديدة")) {

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(Common.IS_OPEN_MessageActivity, true);
                    Common.showNotification(this, new Random().nextInt(),
                            dataRecv.get(Common.NOTI_TITLE),
                            dataRecv.get(Common.NOTI_CONTENT),
                            intent);

                }
                else {
                    Common.showNotification(this, new Random().nextInt(),
                            dataRecv.get(Common.NOTI_TITLE),
                            dataRecv.get(Common.NOTI_CONTENT),
                            null);
                }

            }


        }

    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if(Common.currentUser != null)
            Common.updateToken(s, Common.currentUser.getUid());
    }


}

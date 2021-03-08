package mohamed.parko.hosam.deliveryshop.remote;

import io.reactivex.Observable;
import mohamed.parko.hosam.deliveryshop.Model.FCMResponse;
import mohamed.parko.hosam.deliveryshop.Model.FCMSendData;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAAPBpYGvA:APA91bFLulXUUqELXuYC3l5_eKrGf7Ae7kjixT_Er_4rGKU1INfYQt3KoYCGrKcoCnGlfen2OclaN-qZ2MRbPq5JRsReuJv5M72XlZ1BRaGjwdGhxm0183uyVPRNIAqHZB-FLiH5AJnQ"

    })

    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);

}

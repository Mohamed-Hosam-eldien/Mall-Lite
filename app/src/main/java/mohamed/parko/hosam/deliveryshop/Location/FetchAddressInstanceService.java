package mohamed.parko.hosam.deliveryshop.Location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FetchAddressInstanceService extends IntentService {

    private ResultReceiver resultReceiver;


    public FetchAddressInstanceService() {
        super("FetchAddressInstanceService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent != null) {

            String errorMessage = "";
            resultReceiver = intent.getParcelableExtra(Contstants.RECEIVER);
            Location location = intent.getParcelableExtra(Contstants.LOCATION_DATA_EXTRA);

            if (location == null) {
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                errorMessage = e.getMessage();
            }

            if(addresses == null || addresses.isEmpty()) {
                deliverResultToReceiver(Contstants.FAILURE_RESULT, errorMessage);
            } else {

                Address address = addresses.get(0);
                ArrayList<String> addressFragment = new ArrayList<>();
                addressFragment.add(address.getAdminArea());

                /*for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragment.add(address.getAddressLine(i));
                }*/

                deliverResultToReceiver(Contstants.SUCCESS_RESULT, TextUtils.join(
                        Objects.requireNonNull(System.getProperty("line.separator")),
                        addressFragment
                ));

            }

        }

    }

    private void deliverResultToReceiver (int resultCode , String addressMessage) {

        Bundle bundle = new Bundle();
        bundle.putString(Contstants.RESULT_DATA_KEY , addressMessage);
        resultReceiver.send(resultCode, bundle);

    }

}

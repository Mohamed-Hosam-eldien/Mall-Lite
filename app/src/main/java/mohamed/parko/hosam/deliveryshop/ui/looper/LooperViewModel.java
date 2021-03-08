package mohamed.parko.hosam.deliveryshop.ui.looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mohamed.parko.hosam.deliveryshop.Callback.LooperCallbackListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.LooperModel;

public class LooperViewModel extends ViewModel implements LooperCallbackListener {

    public MutableLiveData<List<LooperModel>> looperList;
    private final LooperCallbackListener looperCallbackListener;
    private final MutableLiveData<String> errorMessage;


    public LooperViewModel() {
        looperCallbackListener = this;
        errorMessage = new MutableLiveData<>();
    }

    public MutableLiveData<List<LooperModel>> getLooperList() {

        if (looperList == null)
            looperList = new MutableLiveData<>();

        loadLooperItems();


        return looperList;
    }

    private void loadLooperItems() {

        List<LooperModel> loopList = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference(Common.banner_ref)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot post : snapshot.getChildren()) {
                            LooperModel model = post.getValue(LooperModel.class);
                            loopList.add(model);
                        }
                        looperCallbackListener.onLoadLooperSuccess(loopList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        looperCallbackListener.onLoadLooperFailer(error.getMessage());
                    }
                });

    }


    @Override
    public void onLoadLooperSuccess(List<LooperModel> model) {
        looperList.setValue(model);
    }

    @Override
    public void onLoadLooperFailer(String message) {
        errorMessage.setValue(message);
    }

}

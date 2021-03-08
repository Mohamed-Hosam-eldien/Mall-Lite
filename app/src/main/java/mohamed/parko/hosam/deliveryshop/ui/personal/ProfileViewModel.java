package mohamed.parko.hosam.deliveryshop.ui.personal;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<UserModel> mutableLiveData;


    public ProfileViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<UserModel> getMutableLiveData() {
        getCurrentUserDetails();
        return mutableLiveData;
    }

    public void getCurrentUserDetails() {

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(Common.currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        mutableLiveData.setValue(userModel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}});

    }

}

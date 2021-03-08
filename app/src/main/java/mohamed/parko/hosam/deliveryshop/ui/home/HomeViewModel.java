package mohamed.parko.hosam.deliveryshop.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import mohamed.parko.hosam.deliveryshop.Callback.ICategoryCallbackListener;
import mohamed.parko.hosam.deliveryshop.Callback.IPopularCallbackListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Model.CategoryModel;
import mohamed.parko.hosam.deliveryshop.Model.PopularModel;

public class HomeViewModel extends ViewModel implements IPopularCallbackListener, ICategoryCallbackListener {

    private MutableLiveData<List<PopularModel>> popularList;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private final IPopularCallbackListener popularCallbackListener;

    private MutableLiveData<List<CategoryModel>> categoryList;
    private final ICategoryCallbackListener categoryCallbackListener;

    public HomeViewModel() {
        popularCallbackListener = this;
        categoryCallbackListener = this;
    }

    public MutableLiveData<List<PopularModel>> getPopularList() {
        if (popularList == null) {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopular();
        }
        return popularList;
    }

    private void loadPopular() {

        List<PopularModel> list = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference(Common.POPULAR_CATEGORY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot post : snapshot.getChildren()) {
                            PopularModel popularModel = post.getValue(PopularModel.class);
                            list.add(popularModel);
                        }
                        popularCallbackListener.onPopularLoadSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        popularCallbackListener.onPopularLoadFailed(error.getMessage());
                    }
                });

    }

    public MutableLiveData<List<CategoryModel>> getCategoryList() {
        if (categoryList == null) {

            categoryList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadCategory();

        }
        return categoryList;
    }

    private void loadCategory() {

        List<CategoryModel> list = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot post : snapshot.getChildren()) {
                            CategoryModel categoryModel = post.getValue(CategoryModel.class);
                            list.add(categoryModel);
                        }
                        categoryCallbackListener.onCategoryLoadSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        categoryCallbackListener.onCategoryLoadFailed(error.getMessage());
                    }
                });

    }


    @Override
    public void onPopularLoadSuccess(List<PopularModel> popularModelList) {
        popularList.setValue(popularModelList);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        messageError.setValue(message);
    }

    @Override
    public void onCategoryLoadSuccess(List<CategoryModel> categoryModelList) {
        categoryList.setValue(categoryModelList);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        messageError.setValue(message);
    }


}

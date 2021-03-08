package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.NotNull;


import java.util.List;

import io.reactivex.annotations.NonNull;
import mohamed.parko.hosam.deliveryshop.Model.LooperModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.LooperLayoutBinding;

public class LooperAdapter extends LoopingPagerAdapter<LooperModel> {

    private final Context context;
    private final List<LooperModel> itemList;

    public LooperAdapter(Context context, List<LooperModel> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);

        this.context = context;
        this.itemList = itemList;
    }

    @Override
    protected void bindView(@NonNull View view, int position, int i1) {
        LooperLayoutBinding binding = LooperLayoutBinding.bind(view);

        Glide.with(view).load(itemList.get(position).getImage()).into(binding.looperImg);
        binding.looperTxt.setText(itemList.get(position).getName());

    }

    @NonNull
    @NotNull
    @Override
    protected View inflateView(int i, @NonNull ViewGroup viewGroup, int i1) {
        return LayoutInflater.from(context).inflate(R.layout.looper_layout, viewGroup, false);
    }


}

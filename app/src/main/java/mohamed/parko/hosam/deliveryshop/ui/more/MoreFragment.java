package mohamed.parko.hosam.deliveryshop.ui.more;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;


import java.util.ArrayList;

import mohamed.parko.hosam.deliveryshop.Adapter.MoreAdapter;
import mohamed.parko.hosam.deliveryshop.Model.MoreModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.FragmentMoreBinding;


public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;


    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.bind(inflater.inflate(R.layout.fragment_more, container, false));

        init();

        return binding.getRoot();
    }

    private void init() {

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_from_left);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerMore.setLayoutManager(layoutManager);
        binding.recyclerMore.setHasFixedSize(true);
        binding.recyclerMore.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        ArrayList<MoreModel> list = new ArrayList<>();
        list.add(new MoreModel("حسابي", R.drawable.ic_baseline_person_24,R.drawable.triangle_back1));
        list.add(new MoreModel("طلباتي", R.drawable.ic_baseline_schedule_48,R.drawable.triangle_back4));
        list.add(new MoreModel("عناويني", R.drawable.ic_baseline_location_on_24,R.drawable.triangle_back2));
        list.add(new MoreModel("الخصومات", R.drawable.ic_baseline_local_offer_24,R.drawable.triangle_back3));
        list.add(new MoreModel("تواصل معنا", R.drawable.ic_baseline_contact_use_alt_24,R.drawable.background_text2));
        list.add(new MoreModel("الأسئلة الشائعة", R.drawable.ic_baseline_help_24,R.drawable.background_text3));

        MoreAdapter adapter = new MoreAdapter(getContext(), list);
        binding.recyclerMore.setAdapter(adapter);
        binding.recyclerMore.setLayoutAnimation(animationController);


    }




}
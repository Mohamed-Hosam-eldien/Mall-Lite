package mohamed.parko.hosam.deliveryshop.ui.addresses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Adapter.AddressAdapter;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDataSource;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDatabase;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressItem;
import mohamed.parko.hosam.deliveryshop.Database.address.LocalAddressDatabase;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityAddressesBinding;
import mohamed.parko.hosam.deliveryshop.databinding.DialogAddAddressBinding;
import mohamed.parko.hosam.deliveryshop.databinding.EditTextBinding;


public class Addresses extends AppCompatActivity {

    private ActivityAddressesBinding binding;
    private DialogAddAddressBinding bindingDialog;
    private CompositeDisposable compositeDisposable;
    private AddressDataSource addressDataSource;
    private EditTextBinding bindingEdit;
    private AddressAdapter addressAdapter;
    private AddressesViewModel viewModel;
    private UserModel userModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Paper.init(this);
        userModel = Paper.book().read("currentUser");

        if(Common.currentUser == null)
            Common.currentUser =userModel;

        viewModel = new ViewModelProvider(this).get(AddressesViewModel.class);

        viewModel.getMutableLiveData().observe(this, this::init);

    }

    private void init(List<String> zoneModels) {

        addressDataSource = new LocalAddressDatabase(AddressDatabase.getInstance(this).addressDao());
        compositeDisposable = new CompositeDisposable();

        viewModel.initAddressDataSource(this);

        viewModel.getAddressItemMutableLiveData().observe(this, addressItems -> {
            addressAdapter = new AddressAdapter(this, addressItems, zoneModels);
            if(addressItems.size() > 0) {
                binding.recyclerViewAddress.setHasFixedSize(true);
                binding.recyclerViewAddress.setNestedScrollingEnabled(false);
                binding.recyclerViewAddress.addItemDecoration(new SpacesItemDecoration(8));
                binding.recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                binding.recyclerViewAddress.setAdapter(addressAdapter);
                binding.progress.setVisibility(View.GONE);
                binding.recyclerViewAddress.setVisibility(View.VISIBLE);
                binding.view.setVisibility(View.VISIBLE);
                binding.emptyLayout.setVisibility(View.GONE);
            } else {
                binding.progress.setVisibility(View.GONE);
                binding.recyclerViewAddress.setVisibility(View.GONE);
                binding.view.setVisibility(View.GONE);
                binding.emptyLayout.setVisibility(View.VISIBLE);
            }

        });

        binding.btnAddNewAddress.setOnClickListener(view -> {
            // init dialog
            Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_address, null);
            bindingDialog = DialogAddAddressBinding.bind(dialogView);
            dialog.setContentView(dialogView);

            // fill spinner with zone
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,zoneModels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.sort(String::compareTo);
            bindingDialog.area.setAdapter(adapter);


            bindingDialog.btnOk.setOnClickListener(view1 -> {
                if (!String.valueOf(bindingDialog.numBuild.getText()).isEmpty() && !String.valueOf(bindingDialog.flatNumber.getText()).isEmpty()
                        && !String.valueOf(bindingDialog.floor.getText()).isEmpty() && !String.valueOf(bindingDialog.nameStreet.getText()).isEmpty()
                        && bindingDialog.area.getSelectedView() != null) {

                    final AlertDialog builder = new AlertDialog.Builder(this).create();

                    LayoutInflater inflater1 = this.getLayoutInflater();
                    final View dialogView1 = inflater1.inflate(R.layout.edit_text, null);
                    builder.setView(dialogView1);
                    bindingEdit = EditTextBinding.bind(dialogView1);

                    bindingEdit.btnAddAddress.setOnClickListener(view2 -> {
                        if(bindingEdit.nameAddressFinal.getText().toString().isEmpty())
                            Toast.makeText(this, "برجاء وضع اسم للعنوان", Toast.LENGTH_SHORT).show();
                        else
                            getDetailsFromUser(dialog, builder);
                    });

                    builder.show();

                } else {
                    Toast.makeText(this, "برجاء استكمال بيانات العنوان", Toast.LENGTH_SHORT).show();
                }
            });

            /*compositeDisposable.add(addressDataSource.getHeaderForAllAddresses(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(strings -> Log.d("DEBUG",""+strings)));*/

            dialog.show();
        });

        binding.imgBack.setOnClickListener(view -> finish());


    }

    private void getDetailsFromUser(Dialog dialog, AlertDialog builder) {

        AddressItem addressItem = new AddressItem();
        addressItem.setUid(Common.currentUser.getUid());
        addressItem.setBuildName(String.valueOf(bindingDialog.nameBuild.getText()));
        addressItem.setBuildNumber(String.valueOf(bindingDialog.numBuild.getText()));
        addressItem.setDetails(String.valueOf(bindingDialog.addressInformation.getText()));
        addressItem.setFlatNumber(String.valueOf(bindingDialog.flatNumber.getText()));
        addressItem.setFloorNumber(String.valueOf(bindingDialog.floor.getText()));
        addressItem.setStreetName(String.valueOf(bindingDialog.nameStreet.getText()));
        addressItem.setZone(String.valueOf(bindingDialog.area.getSelectedItem()));
        addressItem.setAddressName(String.valueOf(bindingEdit.nameAddressFinal.getText()));

        addressDataSource.getItemWithAllOptionsInAddress(addressItem.getUid()
                , addressItem.getAddressName()
                , addressItem.getStreetName()
                , addressItem.getZone())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AddressItem>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull AddressItem addressItemFromDB) {

                        if (addressItemFromDB.equals(addressItem)) {

                            // update address
                            addressItemFromDB.setStreetName(addressItem.getStreetName());
                            addressItemFromDB.setZone(addressItem.getZone());
                            addressItemFromDB.setAddressName(addressItem.getAddressName());
                            addressItemFromDB.setBuildNumber(addressItem.getBuildNumber());
                            addressItemFromDB.setBuildName(addressItem.getBuildName());


                            addressDataSource.updateAddressItem(addressItemFromDB)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {
                                        }

                                        @Override
                                        public void onSuccess(@NonNull Integer integer) {
                                            // refresh adapter
                                            addressAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                            builder.dismiss();
                                            Toast.makeText(Addresses.this, "تمت إضافة عنوان", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                        }
                                    });


                        } else {

                            // add new address
                            compositeDisposable.add(addressDataSource.insertOrReplaceAll(addressItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        // refresh adapter
                                        addressAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        builder.dismiss();
                                        Toast.makeText(Addresses.this, "تمت إضافة عنوان", Toast.LENGTH_SHORT).show();
                                    }, throwable -> Log.d("DEBUG", "" + throwable.getMessage()))

                            );

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage().contains("empty")) {
                            compositeDisposable.add(addressDataSource.insertOrReplaceAll(addressItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        // refresh adapter
                                        addressAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        builder.dismiss();
                                        Toast.makeText(Addresses.this, "تمت إضافة عنوان", Toast.LENGTH_SHORT).show();
                                    }, throwable -> Log.d("DEBUG", "" + throwable.getMessage()))
                            );
                        } else
                            Log.d("DEBUG", "" + e.getMessage());
                    }
                });

    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        viewModel.onStop();
        binding = null;
        bindingEdit = null;
        bindingDialog = null;
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.currentUser != null)
            Common.currentUser = userModel;
    }
}
package mohamed.parko.hosam.deliveryshop.ui.checkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Callback.ILoadTimeFromServer;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDataSource;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDatabase;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressItem;
import mohamed.parko.hosam.deliveryshop.Database.address.LocalAddressDatabase;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDataSource;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDatabase;
import mohamed.parko.hosam.deliveryshop.Database.cart.LocalCartDatabase;
import mohamed.parko.hosam.deliveryshop.EventBus.CounterCartEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.ReturnFromCheckoutEvent;
import mohamed.parko.hosam.deliveryshop.Location.Contstants;
import mohamed.parko.hosam.deliveryshop.Location.FetchAddressInstanceService;
import mohamed.parko.hosam.deliveryshop.Model.FCMSendData;
import mohamed.parko.hosam.deliveryshop.Model.OrderModel;
import mohamed.parko.hosam.deliveryshop.Model.TokenModel;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityCheckoutBinding;
import mohamed.parko.hosam.deliveryshop.databinding.DialogAddAddressBinding;
import mohamed.parko.hosam.deliveryshop.databinding.EditTextBinding;
import mohamed.parko.hosam.deliveryshop.databinding.LayoutNotiesBinding;
import mohamed.parko.hosam.deliveryshop.remote.IFCMService;
import mohamed.parko.hosam.deliveryshop.remote.RetrofitFCMClient;
import mohamed.parko.hosam.deliveryshop.ui.addresses.AddressesViewModel;
import mohamed.parko.hosam.deliveryshop.ui.cart.CartViewModel;


public class Checkout extends AppCompatActivity implements ILoadTimeFromServer {

    private ActivityCheckoutBinding binding;
    private CartDataSource cartDataSource;
    private ViewModelProvider cartViewModel;
    private DialogAddAddressBinding bindingAddress;
    private EditTextBinding bindingEdit;
    private Dialog dialogAddress;
    private OrderModel orderModel;
    private ResultReceiver resultReceiver;


    private boolean gps_enabled;
    private boolean network_enabled;

    private CompositeDisposable compositeDisposable;
    private AddressDataSource addressDataSource;

    private ILoadTimeFromServer listener;
    private LayoutNotiesBinding notiBinding;

    // location
    private android.app.AlertDialog locationDialog;
    private IFCMService ifcmService;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        android.app.AlertDialog dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("برجاء الإنتظار..").setCancelable(false).build();
        dialog.show();

        init();
        new Handler().postDelayed(dialog::dismiss,2000);

    }

    // dialog for send order
    private void init() {

        //initLocation();
        Paper.init(this);
        userModel = Paper.book().read("currentUser");
        if (Common.currentUser == null)
            Common.currentUser = userModel;

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        orderModel = new OrderModel();
        cartDataSource = new LocalCartDatabase(CartDatabase.getInstance(this).cartDao());
        compositeDisposable = new CompositeDisposable();
        addressDataSource = new LocalAddressDatabase(AddressDatabase.getInstance(this).addressDao());
        cartViewModel = new ViewModelProvider(this);
        listener = this;


        AddressesViewModel viewModel = new ViewModelProvider(this).get(AddressesViewModel.class);
        viewModel.getMutableLiveData().observe(this, this::initDialogAddress);

        locationDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        locationDialog.setMessage("جاري إرسال طلبك...");
        resultReceiver = new AddressResultReceiver(new Handler());

        initAddressData();

        binding.layoutCachOnTomorrow.setOnClickListener(view1 -> {
            binding.checkByCashTomorrow.setChecked(true, true);
            if (binding.cardDays.getVisibility() == View.GONE) {
                if (binding.checkByCashTomorrow.isChecked() &&
                        binding.progBarDeleveryTomorrow.getVisibility() == View.GONE &&
                        binding.progBarDeleveryDays.getVisibility() == View.GONE)
                    binding.btnSend.setEnabled(true);

            } else if (binding.progBarDeleveryTomorrow.getVisibility() == View.GONE
                    && binding.checkByCashVodafonDays.isChecked()
                    && binding.progBarDeleveryDays.getVisibility() == View.GONE)
                binding.btnSend.setEnabled(true);

        });

        binding.checkByCashTomorrow.setOnClickListener(view1 -> {
            binding.checkByCashTomorrow.setChecked(true, true);
            if (binding.cardDays.getVisibility() == View.GONE) {
                if (binding.checkByCashTomorrow.isChecked() &&
                        binding.progBarDeleveryTomorrow.getVisibility() == View.GONE &&
                        binding.progBarDeleveryDays.getVisibility() == View.GONE)
                    binding.btnSend.setEnabled(true);

            } else if (binding.progBarDeleveryTomorrow.getVisibility() == View.GONE
                    && binding.checkByCashVodafonDays.isChecked()
                    && binding.progBarDeleveryDays.getVisibility() == View.GONE)
                binding.btnSend.setEnabled(true);
        });

        binding.layoutCachVodafonDays.setOnClickListener(view1 -> {
            binding.checkByCashVodafonDays.setChecked(true, true);
            if (binding.cardTomorrow.getVisibility() == View.GONE) {
                if (binding.checkByCashVodafonDays.isChecked() &&
                        binding.progBarDeleveryTomorrow.getVisibility() == View.GONE &&
                        binding.progBarDeleveryDays.getVisibility() == View.GONE)
                    binding.btnSend.setEnabled(true);

            } else if (binding.progBarDeleveryTomorrow.getVisibility() == View.GONE
                    && binding.checkByCashTomorrow.isChecked()
                    && binding.progBarDeleveryDays.getVisibility() == View.GONE)
                binding.btnSend.setEnabled(true);
        });

        binding.checkByCashVodafonDays.setOnClickListener(view1 -> {
            binding.checkByCashVodafonDays.setChecked(true, true);
            if (binding.cardTomorrow.getVisibility() == View.GONE) {
                if (binding.checkByCashVodafonDays.isChecked() &&
                        binding.progBarDeleveryTomorrow.getVisibility() == View.GONE &&
                        binding.progBarDeleveryDays.getVisibility() == View.GONE)
                    binding.btnSend.setEnabled(true);

            } else if (binding.progBarDeleveryTomorrow.getVisibility() == View.GONE
                    && binding.checkByCashTomorrow.isChecked()
                    && binding.progBarDeleveryDays.getVisibility() == View.GONE)
                binding.btnSend.setEnabled(true);
        });

        binding.btnAddAddress.setOnClickListener(view1 -> dialogAddress.show());

        binding.imgBack.setOnClickListener(view -> finish());
        binding.btnSend.setOnClickListener(view12 -> sendOrderToServer());

    }

    // dialog to add new address
    private void initDialogAddress(List<String> zoneModels) {

        dialogAddress = new Dialog(this);
        dialogAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_address, null);
        bindingAddress = DialogAddAddressBinding.bind(dialogView);
        dialogAddress.setContentView(dialogView);

        // fill spinner with zone
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, zoneModels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.sort(String::compareTo);
        bindingAddress.area.setAdapter(adapter);


        bindingAddress.btnOk.setOnClickListener(view1 -> {
            if (!String.valueOf(bindingAddress.numBuild.getText()).isEmpty() && !String.valueOf(bindingAddress.flatNumber.getText()).isEmpty()
                    && !String.valueOf(bindingAddress.floor.getText()).isEmpty() && !String.valueOf(bindingAddress.nameStreet.getText()).isEmpty()
                    && bindingAddress.area.getSelectedView() != null) {

                final AlertDialog builder = new AlertDialog.Builder(this).create();

                LayoutInflater inflater1 = this.getLayoutInflater();
                final View dialogView1 = inflater1.inflate(R.layout.edit_text, null);
                builder.setView(dialogView1);
                bindingEdit = EditTextBinding.bind(dialogView1);

                bindingEdit.btnAddAddress.setOnClickListener(view2 -> {
                    if (bindingEdit.nameAddressFinal.getText().toString().isEmpty())
                        Toast.makeText(this, "برجاء وضع اسم للعنوان", Toast.LENGTH_SHORT).show();
                    else
                        getDetailsFromUser(dialogAddress, builder);
                });

                builder.show();

            } else {
                Toast.makeText(this, "برجاء استكمال بيانات العنوان", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // get details for new address from user inputs
    private void getDetailsFromUser(Dialog dialog, AlertDialog builder) {

        AddressItem addressItem = new AddressItem();
        addressItem.setUid(Common.currentUser.getUid());
        addressItem.setBuildName(String.valueOf(bindingAddress.nameBuild.getText()));
        addressItem.setBuildNumber(String.valueOf(bindingAddress.numBuild.getText()));
        addressItem.setDetails(String.valueOf(bindingAddress.addressInformation.getText()));
        addressItem.setFlatNumber(String.valueOf(bindingAddress.flatNumber.getText()));
        addressItem.setFloorNumber(String.valueOf(bindingAddress.floor.getText()));
        addressItem.setStreetName(String.valueOf(bindingAddress.nameStreet.getText()));
        addressItem.setZone(String.valueOf(bindingAddress.area.getSelectedItem()));
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
                                            //addressAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                            builder.dismiss();
                                            initAddressData();
                                            Toast.makeText(Checkout.this, "تمت إضافة عنوان", Toast.LENGTH_SHORT).show();
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
                                        //addressAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        builder.dismiss();
                                        initAddressData();
                                        Toast.makeText(Checkout.this, "تمت إضافة عنوان", Toast.LENGTH_SHORT).show();
                                    }, throwable -> Log.d("DEBUG", "" + throwable.getMessage()))

                            );

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        if (e.getMessage() != null && e.getMessage().contains("empty")) {
                            compositeDisposable.add(addressDataSource.insertOrReplaceAll(addressItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        // refresh adapter
                                        //addressAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        builder.dismiss();
                                        initAddressData();
                                        Toast.makeText(Checkout.this, "تمت إضافة عنوان", Toast.LENGTH_SHORT).show();
                                    }, throwable -> Log.d("DEBUG", "" + throwable.getMessage()))
                            );
                        } else
                            Log.d("DEBUG", "" + e.getMessage());
                    }
                });

    }

    // get data from address and other payment details to set to dialog
    private void initAddressData() {

        // get headers of addresses
        compositeDisposable.add(addressDataSource.getHeaderForAllAddresses(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {

                    if (strings.size() != 0) {
                        // found address
                        binding.textNotFoundAddress.setVisibility(View.GONE);
                        binding.textNotFoundAddress2.setVisibility(View.GONE);
                        binding.spinnerHeaderAddress.setVisibility(View.VISIBLE);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter.sort(String::compareTo);

                        binding.spinnerHeaderAddress.setAdapter(adapter);
                        binding.txtAddress.setVisibility(View.VISIBLE);
                        binding.txtNotFound.setVisibility(View.GONE);

                        spinnerAddressState();

                    } else {
                        // not found address

                        binding.textNotFoundAddress.setVisibility(View.VISIBLE);
                        binding.textNotFoundAddress2.setVisibility(View.VISIBLE);
                        binding.spinnerHeaderAddress.setVisibility(View.GONE);


                        binding.txtAddress.setVisibility(View.GONE);
                        binding.txtDetails.setVisibility(View.GONE);
                        binding.txtNotFound.setVisibility(View.VISIBLE);

                    }

                }));


        // get price all order tomorrow
        cartDataSource.sumPriceTomorrowInCart(Common.currentUser.getUid(), "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull Double valueTomorrow) {
                        binding.totalPaymentTomorrow.setText(
                                new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                                        .format(valueTomorrow));
                        binding.cardTomorrow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("DEBUG", "" + e.getMessage());
                    }
                });


        // get price all order after 10 days
        cartDataSource.sumPriceAfterDaysInCart(Common.currentUser.getUid(), "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull Double valueAfterDays) {
                        binding.totalPaymentDays.setText(
                                new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                                        .format(valueAfterDays));
                        binding.cardDays.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("DEBUG2", "" + e.getMessage());
                    }
                });

    }

    // fill spinner with headers address name and set state for change it
    private void spinnerAddressState() {

        binding.spinnerHeaderAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String addressHeader = binding.spinnerHeaderAddress.getSelectedItem().toString();
                addressDataSource.getItemInAddress(addressHeader, Common.currentUser.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<AddressItem>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                            }

                            @Override
                            public void onSuccess(@NonNull AddressItem addressItem) {

                                if(addressItem.getBuildName().isEmpty()) {
                                    binding.txtAddress.setText(new StringBuilder(addressItem.getBuildNumber())
                                            .append(" ")
                                            .append("شارع ")
                                            .append(addressItem.getStreetName())
                                            .append(" - ")
                                            .append(addressItem.getZone())
                                            .append(" شقة رقم : ")
                                            .append(addressItem.getFlatNumber())
                                            .append(" -")
                                            .append(" الدور : ")
                                            .append(addressItem.getFloorNumber()));
                                    binding.txtDetails.setVisibility(View.GONE);

                                } else {

                                    binding.txtAddress.setText(new StringBuilder(addressItem.getBuildNumber())
                                            .append(" مبنى ")
                                            .append(addressItem.getBuildName())
                                            .append(" شارع ")
                                            .append(addressItem.getStreetName())
                                            .append(" - ")
                                            .append(addressItem.getZone())
                                            .append(" شقة رقم : ")
                                            .append(addressItem.getFlatNumber())
                                            .append(" -")
                                            .append(" الدور : ")
                                            .append(addressItem.getFloorNumber()));
                                    binding.txtDetails.setVisibility(View.VISIBLE);

                                }

                                binding.txtDetails.setText(addressItem.getDetails());

                                /*if (!addressItem.getBuildName().isEmpty())
                                    binding.nameBuild.setVisibility(View.VISIBLE);
                                else
                                    binding.nameBuild.setVisibility(View.GONE);

                                binding.nameBuild.setText(addressItem.getBuildName());


                                if (!addressItem.getDetails().isEmpty())
                                    binding.description.setVisibility(View.VISIBLE);
                                else
                                    binding.description.setVisibility(View.GONE);

                                binding.description.setText(addressItem.getDetails());

                                binding.numBuild.setText(addressItem.getBuildNumber());

                                binding.nameStreet.setText(addressItem.getStreetName());
                                binding.floor.setText(addressItem.getFloorNumber());
                                binding.numberFlat.setText(addressItem.getFlatNumber());
                                binding.zone.setText(addressItem.getZone());*/

                                orderModel.setAddress(addressItem);

                                cartViewModel.get(CartViewModel.class)
                                        .getZoneMutableLifeData(addressItem.getZone())
                                        .observe(Checkout.this, s -> {
                                            binding.deleverySalaryDays.setText(s);
                                            binding.deleverySalaryDays.setVisibility(View.VISIBLE);
                                            binding.progBarDeleveryDays.setVisibility(View.GONE);
                                            binding.deleverySalaryTomorrow.setText(s);
                                            binding.deleverySalaryTomorrow.setVisibility(View.VISIBLE);
                                            binding.progBarDeleveryTomorrow.setVisibility(View.GONE);
                                            binding.txt2.setVisibility(View.VISIBLE);
                                            binding.txt5.setVisibility(View.VISIBLE);

                                            binding.totalAllPaymentAfter10Days.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                                                    .format(Double.parseDouble(s) + Double.parseDouble(binding.totalPaymentDays.getText().toString())));

                                            binding.totalAllTomorrow.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                                                    .format(Double.parseDouble(s) + Double.parseDouble(binding.totalPaymentTomorrow.getText().toString())));

                                            if (binding.cardDays.getVisibility() == View.GONE) {

                                                if (binding.progBarDeleveryTomorrow.getVisibility() == View.GONE
                                                        && binding.checkByCashTomorrow.isChecked())
                                                    binding.btnSend.setEnabled(true);


                                            } else if (binding.cardTomorrow.getVisibility() == View.GONE) {

                                                if (binding.progBarDeleveryDays.getVisibility() == View.GONE
                                                        && binding.checkByCashVodafonDays.isChecked())
                                                    binding.btnSend.setEnabled(true);


                                            } else {
                                                if (binding.progBarDeleveryDays.getVisibility() == View.GONE
                                                        && binding.progBarDeleveryTomorrow.getVisibility() == View.GONE
                                                        && binding.checkByCashVodafonDays.isChecked()
                                                        && binding.checkByCashTomorrow.isChecked())
                                                    binding.btnSend.setEnabled(true);

                                            }

                                        });

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d("DEBUG_ADDRESS_ITEM", "" + e.getMessage());
                            }
                        });


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    // when click to send button first time
    private void sendOrderToServer() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocation();
        } else {
            underAPI();
        }

    }

    // get location after permission
    private void getLocation() {

        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            getCurrentLocation();
                            /*new Handler().postDelayed(() -> {

                                initLocation();
                                fusedLocationProviderClient.getLastLocation()
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(thisContext, "خطأ في تحديد الموقع برجاء المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
                                            locationDialog.dismiss();
                                        })
                                        .addOnCompleteListener(task -> {

                                            if (task.getResult() != null) {
                                                Single<String> singleAddress = Single.just(getAddressGovernorate(task.getResult().getLongitude(),
                                                        task.getResult().getLatitude()));

                                                setDataAfterGetLocation(singleAddress);

                                            } else {
                                                locationDialog.dismiss();
                                                Toast.makeText(thisContext, "عذراً.. حاول مرة أخرى", Toast.LENGTH_SHORT).show();
                                            }

                                        });

                            },2000);*/
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        locationDialog.show();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(Checkout.this)
                                .removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {

                            int lastLocationIndex = locationResult.getLocations().size() - 1;

                            double latitude = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(lastLocationIndex).getLongitude();

                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            orderModel.setLat(String.valueOf(latitude));
                            orderModel.setLon(String.valueOf(longitude));

                            getAddressFromLocation(location);

                        } else {
                            locationDialog.dismiss();
                            Toast.makeText(Checkout.this, "خطأ في تحديد الموقع! حاول مرة أخرى", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, Looper.getMainLooper());

    }


    private void getAddressFromLocation( Location location) {
        Intent intent = new Intent(this, FetchAddressInstanceService.class);
        intent.putExtra(Contstants.RECEIVER, resultReceiver);
        intent.putExtra(Contstants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if(resultCode == Contstants.SUCCESS_RESULT) {

                String area = resultData.getString(Contstants.RESULT_DATA_KEY);

                if(area != null) {

                    if (!area.equals("Alexandria Governorate") && !area.equals("الإسكندرية")) {

                        // location is unavailable
                        Dialog dialog = new Dialog(Checkout.this);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        View view = LayoutInflater.from(Checkout.this).inflate(R.layout.layout_noties, null);
                        notiBinding = LayoutNotiesBinding.bind(view);
                        dialog.setContentView(view);

                        notiBinding.dialogTitle.setText("خارج نطاق خدمتنا");
                        notiBinding.dialogDescription.setText("عزيزنا العميل نأسف لإخبارك بأنك خارج نطاق خدمتنا المتمثلة داخل محافظة الإسكندرية");
                        notiBinding.dialogImage.setImageResource(R.drawable.ic_baseline_location_off_24);

                        notiBinding.dialogAccept.setOnClickListener(view1 -> dialog.dismiss());
                        locationDialog.dismiss();
                        dialog.show();


                    } else {

                        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(cartItems ->

                                        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new SingleObserver<Double>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {
                                                    }

                                                    @Override
                                                    public void onSuccess(@NonNull Double total) {
                                                        // personal info
                                                        orderModel.setUid(Common.currentUser.getUid());
                                                        orderModel.setName(Common.currentUser.getName());
                                                        orderModel.setPhone(Common.currentUser.getPhone());

                                                        // order info
                                                        orderModel.setComment(String.valueOf(binding.edtOrderComment.getText()));
                                                        orderModel.setTotal(String.valueOf(total));
                                                        orderModel.setCartList(cartItems);
                                                        orderModel.setDeliverySalary(binding.deleverySalaryTomorrow.getText().toString());

                                                        orderModel.setTotalTomorrow(binding.totalPaymentTomorrow.getText().toString());
                                                        orderModel.setTotalAfterSomeDays(binding.totalPaymentDays.getText().toString());

                                                        orderModel.setMoneyPaid("");
                                                        orderModel.setHistoryArrivedOrder("");
                                                        orderModel.setMoneyRemaining("");

                                                        orderModel.setDeposit(!binding.totalPaymentDays.getText().toString().equals("0.00"));

                                                        syncLocalTimeWithGlobalTime(orderModel);

                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        locationDialog.dismiss();
                                                        if(e.getMessage() != null && e.getMessage().contains("Query returned empty result set"))
                                                            Log.d("DEBUG_SEND_ORDER",""+e.getMessage());
                                                    }

                                                })));


                    }

                }

            } else {
                Toast.makeText(Checkout.this, resultData.getString(Contstants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }

            locationDialog.dismiss();

        }
    }

    // to set location in api < 24
    private void underAPI() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            if (lm != null)
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (lm != null)
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*if (!gps_enabled && !network_enabled) {
            turnOnLocation();
        } else if (gps_enabled) {
            getLocation();
        }*/

        if (!gps_enabled && !network_enabled) {
            final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
            dialog.setMessage("عزيزي العميل برجاء تفعيل موقعك");

            dialog.setPositiveButton("موافق", (paramDialogInterface, paramInt) -> {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            });

            dialog.setNegativeButton("إلغاء", (paramDialogInterface, paramInt) ->
                    paramDialogInterface.dismiss());

            dialog.show();
        } else if (gps_enabled) {
            getLocation();
        }

    }

    // final step to set order in firebase
    private void setOrderToServer(OrderModel orderModel) {

        String orderNum = Common.createOrderNumber();

        FirebaseDatabase.getInstance()
                .getReference(Common.order_ref)
                .child(orderNum)
                .setValue(orderModel)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "فشل في إرسال الطلب حاول مرة أخرى", Toast.LENGTH_SHORT).show();
                    locationDialog.dismiss();
                })
                .addOnCompleteListener(task ->
                        FirebaseDatabase.getInstance()
                                .getReference(Common.orderCustomerRef)
                                .child(Common.currentUser.getUid())
                                .child(orderNum)
                                .setValue(orderModel)
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "فشل في إرسال الطلب حاول مرة أخرى", Toast.LENGTH_SHORT).show();
                                    locationDialog.dismiss();
                                })
                                .addOnCompleteListener(task1 ->
                                        cartDataSource.cleanCart(Common.currentUser.getUid())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new SingleObserver<Integer>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {
                                                    }

                                                    @Override
                                                    public void onSuccess(@NonNull Integer integer) {

                                                        cartDataSource.cleanCart(Common.currentUser.getUid())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new SingleObserver<Integer>() {
                                                                    @Override
                                                                    public void onSubscribe(@NonNull Disposable d) {
                                                                    }

                                                                    @Override
                                                                    public void onSuccess(@NonNull Integer integer) {
                                                                        sendNotification();
                                                                    }

                                                                    @Override
                                                                    public void onError(@NonNull Throwable e) {
                                                                        Log.d("DEBUG_SEND_ORDER", "" + e.getMessage());
                                                                        locationDialog.dismiss();
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        Log.d("DEBUG_SEND_ORDER", "" + e.getMessage());
                                                        locationDialog.dismiss();
                                                    }
                                                }))
                );


    }

    private void sendNotification() {

        Map<String, String> notiData = new HashMap<>();
        notiData.put(Common.NOTI_TITLE,"طلب جديد");
        notiData.put(Common.NOTI_CONTENT, "لديك طلب جديد من " + Common.currentUser.getName());

        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot post : snapshot.getChildren()) {

                            TokenModel model = post.getValue(TokenModel.class);
                            if(model != null && model.isAdmin()) {

                                FCMSendData sendData = new FCMSendData(model.getToken(), notiData);

                                compositeDisposable.add(ifcmService.sendNotification(sendData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {

                                            //calculateTotalPrice();
                                            EventBus.getDefault().postSticky(new ReturnFromCheckoutEvent(true));
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            finish();

                                        },throwable -> {
                                            //calculateTotalPrice();
                                            EventBus.getDefault().postSticky(new ReturnFromCheckoutEvent(true));
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                            finish();
                                        }));

                            } else {
                                //calculateTotalPrice();
                                EventBus.getDefault().postSticky(new ReturnFromCheckoutEvent(true));
                                EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                finish();
                            }

                        }

                        locationDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        locationDialog.dismiss();
                    }
                });

    }

    // calculate total price after change item quantity
    /*private void calculateTotalPrice() {

        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull Double price) {

                        binding.txtCartTotalPayment.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                                .format(price));

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (e.getMessage() != null && e.getMessage().contains("empty")) {
                            binding.txtCartTotalPayment.setText(R.string._0_00);
                            binding.btnSend.setEnabled(false);
                        } else
                            Log.d("CART_UPDATE2", "" + e.getMessage());
                    }
                });

    }*/

    // local time if time is changed by user
    private void syncLocalTimeWithGlobalTime(OrderModel orderModel) {

        final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long time = System.currentTimeMillis() + offset;

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy - HH:mm", Locale.ENGLISH);
                Date date = new Date(time);
                Log.d("LOCAL TIME", "" + sdf.format(date));

                listener.onLoadTimeSuccess(orderModel, time);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                locationDialog.dismiss();
            }
        });

    }

    @Override
    public void onLoadTimeSuccess(OrderModel order, long estimateTime) {
        order.setTime(estimateTime);
        order.setState(1);
        setOrderToServer(orderModel);
    }

    @Override
    public void onLoadFailed(String message) {
        Log.d("TIME_ERROR", "" + message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        notiBinding = null;
        bindingAddress = null;
        bindingEdit = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.currentUser == null)
            Common.currentUser =userModel;
    }

    @Override
    public void onStop() {
        super.onStop();
        cartViewModel.get(CartViewModel.class).onStop();
        compositeDisposable.clear();
    }


}
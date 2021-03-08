package mohamed.parko.hosam.deliveryshop.ui.foodDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
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
import mohamed.parko.hosam.deliveryshop.Adapter.AddonImageListAdapter;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Common.SpacesItemDecoration;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDataSource;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDatabase;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;
import mohamed.parko.hosam.deliveryshop.Database.cart.LocalCartDatabase;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDataSource;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.Database.favorite.FavoriteItem;
import mohamed.parko.hosam.deliveryshop.Database.favorite.LocalFavoriteDatabase;
import mohamed.parko.hosam.deliveryshop.EventBus.CartFromFoodDetails;
import mohamed.parko.hosam.deliveryshop.EventBus.CounterCartEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.RefreshFoodListEvent;
import mohamed.parko.hosam.deliveryshop.Home;
import mohamed.parko.hosam.deliveryshop.MainActivity;
import mohamed.parko.hosam.deliveryshop.Model.AddonModel;
import mohamed.parko.hosam.deliveryshop.Model.AddonWithPrice;
import mohamed.parko.hosam.deliveryshop.Model.CommentModel;
import mohamed.parko.hosam.deliveryshop.Model.FoodModel;
import mohamed.parko.hosam.deliveryshop.Model.UserModel;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.ActivityFoodDetailsBinding;
import mohamed.parko.hosam.deliveryshop.databinding.BottomSheetOrderNowBinding;
import mohamed.parko.hosam.deliveryshop.databinding.LayoutNotiesBinding;
import mohamed.parko.hosam.deliveryshop.databinding.RatingLayoutBinding;
import mohamed.parko.hosam.deliveryshop.ui.comments.CommentFragment;

public class FoodDetails extends AppCompatActivity {

    private ActivityFoodDetailsBinding binding;
    private ScaleAnimation scaleAnimation;
    private FoodDetailsViewModel viewModel;
    private AlertDialog waitingDialog;
    private BottomSheetDialog bottomSheetOrder;
    private BottomSheetOrderNowBinding orderNowBinding;
    private AddonWithPrice addonWithPriceUserChoose;
    private List<AddonModel> addonListUserChoose;
    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private FavoriteDataSource favoriteDataSource;
    private UserModel userModel;
    private String fromPop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Paper.init(this);
        userModel = Paper.book().read("currentUser");
        if(Common.currentUser == null)
            Common.currentUser = userModel;

        fromPop = getIntent().getStringExtra("fromPop");

        if(Common.Category_Selected != null) {
            init();
            initBottomView();
            displayAddonList();
            viewModel.getMutableLiveData().observe(this, this::setDetails);
            viewModel.getCommentModelMutableLiveData().observe(this, this::setRatingToFirebase);
        } else
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));


    }

    private void initBottomView() {

        orderNowBinding.txtFoodQuantity.setText(Common.SelectedFood.getMiniWeight());
        orderNowBinding.btnMinus.setEnabled(false);
        // for set quantity 0.25 or 1
        if (Common.SelectedFood.getAddonWithPrice() != null) {
            orderNowBinding.txtFoodQuantity.setText("0");
        } else {

            double val;
            if(!Common.SelectedFood.getDiscountPrice().isEmpty() &&
                    !Common.SelectedFood.getDiscountPrice().equals("0")) {
                val = Double.parseDouble(Common.SelectedFood.getDiscountPrice())
                        * Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());
            } else {
                val = Double.parseDouble(Common.SelectedFood.getPrice())
                        * Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());
            }

            orderNowBinding.txtFoodTotalPrice.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH)).format(val));

        }

        // for set details to menus
        if (!Common.SelectedFood.getDiposit().isEmpty()) {

            orderNowBinding.txtRedLine.setVisibility(View.VISIBLE);
            orderNowBinding.txtCash.setText("يتم دفع نسبة من المبلغ عند الطلب بالتنسيق مع خدمة العملاء وباقي المبلغ عند استلام الطلب");
            orderNowBinding.txtDeliveryTime.setText(R.string.delivery_time_for_hanmade);

        } else {

            orderNowBinding.txtRedLine.setVisibility(View.GONE);
            orderNowBinding.txtCash.setText(R.string.cash_on_delivery);
            orderNowBinding.txtDeliveryTime.setText(R.string.delivery_time_one_day);

        }

        // state for اختر الصنف
        if (Common.SelectedFood.getAddonWithPrice() == null)
            orderNowBinding.cardChooseCategory.setVisibility(View.GONE);

        // state for اختر النوع
        if (Common.SelectedFood.getAddon().get(0).getImage().equals("")) {
            orderNowBinding.cardChooseKind.setVisibility(View.GONE);
            orderNowBinding.addonRecyclerView.setVisibility(View.GONE);
        }

        // button plus and minus
        orderNowBinding.btnPlus.setOnClickListener(view -> plusMethoud());
        orderNowBinding.btnMinus.setOnClickListener(view -> minusMethoud());

        // btn add order to cart
        orderNowBinding.btnAddToCart.setEnabled(true);
        orderNowBinding.btnAddToCart.setOnClickListener(view -> transToCart());

    }

    private void transToCart() {

        if(Common.SelectedFood.getProductState().equals("متوفر")) {

            if (Common.SelectedFood.getAddonWithPrice() != null) {

                if (addonWithPriceUserChoose.getPrice() != null) {
                    if (!Common.SelectedFood.getAddon().get(0).getImage().isEmpty()) {
                        if (addonListUserChoose.size() > 0) {
                            // add to cart
                            addToCart(addonWithPriceUserChoose, addonListUserChoose);
                        } else {
                            if (Common.Category_Selected.getMenuId().equals("menu_05")) {
                                Toast.makeText(this, "من فضلك اختر لون", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "من فضلك اختر النوع", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // add to cart
                        addToCart(addonWithPriceUserChoose, null);
                    }
                } else {
                    Toast.makeText(this, "من فضلك اختر الصنف", Toast.LENGTH_SHORT).show();
                }


            } else if (!Common.SelectedFood.getAddon().get(0).getImage().isEmpty()) {

                if (addonListUserChoose.size() > 0) {

                    // add to cart
                    addToCart(null , addonListUserChoose);

                } else {
                    if (Common.Category_Selected.getMenuId().equals("menu_05")) {
                        Toast.makeText(this, "من فضلك اختر لون", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "من فضلك اختر النوع", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {

                // add to cart
                addToCart(null, null);

            }

        } else {
            Dialog dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View view = LayoutInflater.from(this).inflate(R.layout.layout_noties, null);
            LayoutNotiesBinding binding = LayoutNotiesBinding.bind(view);
            dialog.setContentView(view);

            binding.dialogTitle.setText("صنف غير متوفر!");
            binding.dialogDescription.setText("عزيزنا العميل، نعمل على توفير هذا الصنف قريباً..");
            binding.dialogImage.setImageResource(R.drawable.ic_not_available_cancel_24);
            binding.dialogAccept.setOnClickListener(view1 -> dialog.dismiss());

            dialog.show();
        }

    }

    private void addToCart(AddonWithPrice addonWithPrice, List<AddonModel> listAddonModel) {

        CartItem cartItem = new CartItem();
        cartItem.setUid(Common.currentUser.getUid());
        cartItem.setUserPhone(Common.currentUser.getPhone());

        cartItem.setFoodId(Common.SelectedFood.getId());

        if(Common.SelectedFood.getDiposit().equals("true")) {

            double totalPrice = Double.parseDouble(orderNowBinding.txtFoodTotalPrice.getText().toString());
            double deposit = totalPrice * 75 /100;
            cartItem.setFoodDeposit(String.valueOf(deposit));

        } else
            cartItem.setFoodDeposit("");


        cartItem.setFoodMiniWeight(Common.SelectedFood.getMiniWeight());
        cartItem.setFoodName(Common.SelectedFood.getName());

        if(!Common.SelectedFood.getPrice().equals("0")) {

            if(Common.SelectedFood.getDiscountPrice().equals("0")
                    || Common.SelectedFood.getDiscountPrice().isEmpty()) {
                cartItem.setFoodPrice(Common.SelectedFood.getPrice());
            } else {
                cartItem.setFoodPrice(Common.SelectedFood.getDiscountPrice());
            }

        } else
            cartItem.setFoodPrice(addonWithPrice.getPrice());

        cartItem.setFoodImage(Common.SelectedFood.getImage());
        cartItem.setFoodQuantity(orderNowBinding.txtFoodQuantity.getText().toString());

        if(listAddonModel != null)
            cartItem.setFoodAddon(new Gson().toJson(listAddonModel));
        else
            cartItem.setFoodAddon("empty");

        if(addonWithPrice != null)
            cartItem.setFoodAddonWithPrice(new Gson().toJson(addonWithPrice));
        else
            cartItem.setFoodAddonWithPrice("empty");


        cartDataSource.getItemWithAllOptionsInCart(cartItem.getFoodId(),
                cartItem.getUid(),
                cartItem.getFoodAddon(), cartItem.getFoodAddonWithPrice())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CartItem>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }
                    @Override
                    public void onSuccess(@NonNull CartItem cartItemFromDB) {
                        if(cartItemFromDB.equals(cartItem)) {

                            // already in database we will just update it
                            cartItemFromDB.setFoodAddonWithPrice(cartItem.getFoodAddonWithPrice());
                            cartItemFromDB.setFoodAddon(cartItem.getFoodAddon());
                            cartItemFromDB.setFoodQuantity(cartItem.getFoodQuantity());

                            cartDataSource.updateCartItem(cartItemFromDB)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {

                                        }
                                        @Override
                                        public void onSuccess(@NonNull Integer integer) {
                                            bottomSheetOrder.dismiss();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        }
                                        @Override
                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                            Log.d("DEBUG_FIRST_TIME", ""+ e.getMessage());
                                        }
                                    });

                        } else {

                            // add new item
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        bottomSheetOrder.dismiss();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    },throwable -> Log.d("CART_DEBUG",throwable.getMessage()+""))
                            );

                        }
                    }
                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if(e.getMessage().contains("empty")) {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        bottomSheetOrder.dismiss();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    },throwable -> Log.d("CART_DEBUG",throwable.getMessage()+""))
                            );
                        } else
                            Log.d("DEBUG",""+e.getMessage());
                    }
                });

    }

    // for button plus
    private void plusMethoud() {

        if (!orderNowBinding.txtFoodTotalPrice.getText().toString().equals("0.00")) {
            if (!orderNowBinding.btnMinus.isEnabled())
                orderNowBinding.btnMinus.setEnabled(true);

            double valuePlus = Double.parseDouble(Common.SelectedFood.getMiniWeight());
            double currentValue = Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString()) + valuePlus;
            orderNowBinding.txtFoodQuantity.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH)).format(currentValue));

            double newTotal;
            if (Common.SelectedFood.getPrice().equals("0")) {
                newTotal = Double.parseDouble(addonWithPriceUserChoose.getPrice()) *
                        Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());

            } else {
                if(!Common.SelectedFood.getDiscountPrice().isEmpty() &&
                        !Common.SelectedFood.getDiscountPrice().equals("0")) {
                    newTotal = Double.parseDouble(Common.SelectedFood.getDiscountPrice()) *
                            Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());
                } else {
                    newTotal = Double.parseDouble(Common.SelectedFood.getPrice()) *
                            Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());
                }

            }
            orderNowBinding.txtFoodTotalPrice.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH)).format(newTotal));
        } else {
            Toast.makeText(this, "من فضلك اختر الصنف", Toast.LENGTH_SHORT).show();
        }

    }

    // for button minus
    private void minusMethoud() {

        if (orderNowBinding.txtFoodQuantity.getText().equals(Common.SelectedFood.getMiniWeight())) {
            orderNowBinding.btnMinus.setEnabled(false);
        } else {
            double valueMinus = Double.parseDouble(Common.SelectedFood.getMiniWeight());
            double currentValue = Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString()) - valueMinus;
            orderNowBinding.txtFoodQuantity.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH)).format(currentValue));

            double newTotal;
            if (Common.SelectedFood.getPrice().equals("0")) {
                newTotal = Double.parseDouble(addonWithPriceUserChoose.getPrice()) *
                        Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());

            } else {

                if(!Common.SelectedFood.getDiscountPrice().isEmpty() &&
                        !Common.SelectedFood.getDiscountPrice().equals("0")) {
                    newTotal = Double.parseDouble(Common.SelectedFood.getDiscountPrice()) *
                            Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());
                } else {
                    newTotal = Double.parseDouble(Common.SelectedFood.getPrice()) *
                            Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());
                }


            }
            orderNowBinding.txtFoodTotalPrice.setText((new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH)).format(newTotal)));
        }

    }

    private void setRatingToFirebase(CommentModel commentModel) {
        waitingDialog.show();

        FirebaseDatabase.getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.SelectedFood.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addRatingToFood(commentModel.getRatingValue());
                    }
                    waitingDialog.dismiss();
                }).addOnFailureListener(e -> {
            waitingDialog.dismiss();
            Toast.makeText(FoodDetails.this, "حدث خطأ ما.. برجاء المحاولة مرة أخرى", Toast.LENGTH_SHORT).show();
        });


    }

    private void addRatingToFood(float ratingValue) {

        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.Category_Selected.getMenuId())
                .child("foods")
                .child(Common.SelectedFood.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            FoodModel foodModel = snapshot.getValue(FoodModel.class);
                            foodModel.setKey(Common.SelectedFood.getKey());

                            if (foodModel.getRatingValue() == null)
                                foodModel.setRatingValue(0d);
                            if (foodModel.getRatingCount() == null)
                                foodModel.setRatingCount(0L);
                            double rating = foodModel.getRatingValue() + ratingValue;
                            long ratingCount = foodModel.getRatingCount() + 1;

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("ratingValue", rating);
                            updateData.put("ratingCount", ratingCount);

                            foodModel.setRatingCount(ratingCount);
                            foodModel.setRatingValue(rating);

                            snapshot.getRef()
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(task -> {
                                        waitingDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(FoodDetails.this, "تم إرسال تقييمك", Toast.LENGTH_SHORT).show();
                                            Common.SelectedFood = foodModel;
                                            viewModel.setFoodModel(foodModel); // to refresh data
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        waitingDialog.dismiss();
                    }
                });

    }

    private void init() {

        binding.imgBack.setOnClickListener(view -> {
            EventBus.getDefault().removeAllStickyEvents();
            finish();
        });

        binding.counterFab.setOnClickListener(view -> {
            EventBus.getDefault().removeAllStickyEvents();
            startActivity(new Intent(this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            EventBus.getDefault().postSticky(new CartFromFoodDetails(true));
        });


        compositeDisposable = new CompositeDisposable();
        favoriteDataSource = new LocalFavoriteDatabase(FavoriteDatabase.getInstance(this).favoriteDao());
        cartDataSource = new LocalCartDatabase(CartDatabase.getInstance(this).cartDao());


        favoriteDataSource.checkItemInFavorite(Common.SelectedFood.getId(),Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<FavoriteItem>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {}
                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull FavoriteItem favoriteItem) {
                        if(favoriteItem.getFoodId().equals(Common.SelectedFood.getId()))
                            binding.imgFav.setChecked(true);
                    }
                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {}});


        addonWithPriceUserChoose = new AddonWithPrice();
        addonListUserChoose = new ArrayList<>();

        viewModel = new ViewModelProvider(this).get(FoodDetailsViewModel.class);
        waitingDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        waitingDialog.setMessage("جاري إرسال التقييم..");

        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);


        bottomSheetOrder = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_order_now, null);
        orderNowBinding = BottomSheetOrderNowBinding.bind(view);
        orderNowBinding.getRoot();
        bottomSheetOrder.setContentView(view);


        countItemInCart();

    }

    private void countItemInCart() {

        cartDataSource.countItemInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer num) {

                        if (num != 0)
                            binding.counterFab.setCount(num);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("DEBUG_COUNT", "" + e.getMessage());
                    }
                });

    }

    private void setDetails(FoodModel model) {

        Glide.with(this).load(model.getImage()).into(binding.foodImage);

        if(!model.getDiscountPrice().isEmpty()
                && !model.getDiscountPrice().equals("0")) {
            binding.foodCurrentPrice.setText(new StringBuilder(model.getDiscountPrice()));
            binding.foodOldPrice.setText(new StringBuilder(model.getPrice()));
            binding.layoutOldPrice.setVisibility(View.VISIBLE);
        } else {
            binding.foodCurrentPrice.setText(new StringBuilder(model.getPrice()));
            binding.layoutOldPrice.setVisibility(View.GONE);
        }

        // to gone pound text
        if (model.getAddonWithPrice() != null) {
            binding.pound.setVisibility(View.GONE);
            binding.foodCurrentPrice.setText("السعر وفقاً لطلبك");
        }

        binding.txtFoodName.setText(model.getName());
        binding.txtDescription.setText(model.getDescription());

        if(model.getProductState().equals("متوفر"))
            binding.txtProductState.setBackgroundResource(R.drawable.background_product_state);
        else if(model.getProductState().equals("سيتم توفيره خلال 48 ساعة"))
            binding.txtProductState.setBackgroundResource(R.drawable.background_product_state2);
        else
            binding.txtProductState.setBackgroundResource(R.drawable.background_product_state3);

        binding.txtProductState.setText(model.getProductState());



        if (model.getRatingValue() != null)
            binding.txtRatingValue.setText(new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.ENGLISH)).format(model.getRatingValue() / model.getRatingCount()));

        binding.txtLikeValue.setText(String.valueOf(model.getLike()));

        binding.imgLike.setOnClickListener(view -> {
            if (binding.imgLike.getTag().equals("0")) {
                binding.imgLike.setImageResource(R.drawable.ic_like_color_24);
                binding.imgLike.setTag("1");
                setNumberLike();
            } else {
                binding.imgLike.setImageResource(R.drawable.ic_like_gray_24);
                binding.imgLike.setTag("0");
                removeLike();
            }
        });


        binding.imgFav.setOnClickListener(view -> {

            FavoriteItem favoriteItem = new FavoriteItem();
            favoriteItem.setFoodId(Common.SelectedFood.getId());
            favoriteItem.setMenuId(Common.Category_Selected.getMenuId());
            favoriteItem.setUid(Common.currentUser.getUid());
            favoriteItem.setFoodImage(Common.SelectedFood.getImage());
            favoriteItem.setFoodName(Common.SelectedFood.getName());

            if (binding.imgFav.isChecked()) {

                compositeDisposable.add(favoriteDataSource.insertOrReplaceAll(favoriteItem)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> binding.imgFav.setChecked(true)));

            } else {

                compositeDisposable.add(favoriteDataSource.deleteFavoriteItem(favoriteItem)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((integer, throwable) -> binding.imgFav.setChecked(false)));

            }

        });


        binding.layoutComment.setOnClickListener(view -> {
            CommentFragment commentFragment = CommentFragment.getInstance();
            if(Common.SelectedFood.getRatingCount() != null && Common.SelectedFood.getRatingCount() > 0)
                commentFragment.show(getSupportFragmentManager(), "CommentFragment");
            else
                Toast.makeText(this, "لا يوجد تعليقات على هذا المنتج", Toast.LENGTH_SHORT).show();
        });

        setLikeState(model);

        binding.imgFav.setOnCheckedChangeListener((compoundButton, b) ->
                compoundButton.startAnimation(scaleAnimation));

        binding.layoutRate.setOnClickListener(view -> showRatingDialog());

        binding.btnOrderNow.setOnClickListener(view -> {
            fillRecyclerFoodImages();
            //displayOrderLayout();
            new Handler().postDelayed(() ->
                    bottomSheetOrder.show(),200);

        });


    }

    private void fillRecyclerFoodImages() {

        AddonImageListAdapter adapter = new AddonImageListAdapter(this, Common.SelectedFood, orderNowBinding.chipGroupImageAddon, addonListUserChoose);
        orderNowBinding.addonRecyclerView.setHasFixedSize(true);
        orderNowBinding.addonRecyclerView.addItemDecoration(new SpacesItemDecoration(8));

        orderNowBinding.addonRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        orderNowBinding.addonRecyclerView.setAdapter(adapter);

    }

    private void displayAddonList() {

        if (Common.SelectedFood.getAddonWithPrice() != null && Common.SelectedFood.getAddonWithPrice().size() > 0) {

            orderNowBinding.chipGroupFoodAddon.setVisibility(View.VISIBLE);
            orderNowBinding.chipGroupFoodAddon.clearCheck();
            orderNowBinding.chipGroupFoodAddon.removeAllViews();

            for (AddonWithPrice addModel : Common.SelectedFood.getAddonWithPrice()) {

                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_chip_item, null);
                chip.setText(new StringBuilder(addModel.getName()).append(" ( ")
                        .append(addModel.getPrice()).append(" ج.م ").append(")"));

                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {

                        orderNowBinding.txtFoodQuantity.setText("1");

                        addonWithPriceUserChoose = addModel;

                        calculateToPrice(addModel);

                    }

                });

                orderNowBinding.chipGroupFoodAddon.addView(chip);

            }

        }


    }

    private void calculateToPrice(AddonWithPrice addModel) {

        double totalPrice = Double.parseDouble(Common.SelectedFood.getPrice());

        // for addon
        /*if (Common.SelectedFood.getAddonWithPrice() != null && Common.SelectedFood.getAddonWithPrice().size() > 0) {
            for (AddonWithPrice addonWithPrice : Common.SelectedFood.getAddonWithPrice())
                totalPrice += Double.parseDouble(addonWithPrice.getPrice());
        }*/

        totalPrice += Double.parseDouble(addModel.getPrice());

        //displayPrice = totalPrice * Double.parseDouble(orderNowBinding.txtFoodQuantity.getText().toString());
        //displayPrice = Math.round(displayPrice * 100.0 /100.0);
        orderNowBinding.txtFoodTotalPrice.setText((new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH)).format(totalPrice)));


    }

    private void setLikeState(FoodModel model) {

        FirebaseDatabase.getInstance()
                .getReference(Common.LIKE_REF)
                .child(model.getId())
                .child(Common.currentUser.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.imgLike.setImageResource(R.drawable.ic_like_color_24);
                            binding.imgLike.setTag("1");
                        } else {
                            binding.imgLike.setImageResource(R.drawable.ic_like_gray_24);
                            binding.imgLike.setTag("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }

    private void removeLike() {

        waitingDialog.setMessage("برجاء الإنتظار..");
        waitingDialog.show();
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.Category_Selected.getMenuId())
                .child("foods")
                .child(Common.SelectedFood.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            FoodModel foodModel = snapshot.getValue(FoodModel.class);
                            foodModel.setKey(Common.SelectedFood.getKey());

                            int numberLikes = foodModel.getLike() - 1;
                            foodModel.setLike(numberLikes);

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("like", numberLikes);

                            snapshot.getRef()
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(task -> {
                                        waitingDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Common.SelectedFood = foodModel;
                                            removeLikeFromFirebase(foodModel);
                                            viewModel.setFoodModel(foodModel); // to refresh data
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        waitingDialog.dismiss();
                    }
                });

    }

    private void setNumberLike() {
        waitingDialog.show();
        waitingDialog.setMessage("برجاء الإنتظار..");
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.Category_Selected.getMenuId())
                .child("foods")
                .child(Common.SelectedFood.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            FoodModel foodModel = snapshot.getValue(FoodModel.class);
                            foodModel.setKey(Common.SelectedFood.getKey());

                            int numberLikes = foodModel.getLike() + 1;
                            foodModel.setLike(numberLikes);

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("like", numberLikes);

                            snapshot.getRef()
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(task -> {
                                        waitingDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Common.SelectedFood = foodModel;
                                            addLikeToFirebase();
                                            viewModel.setFoodModel(foodModel); // to refresh data
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        waitingDialog.dismiss();
                    }
                });

    }

    private void removeLikeFromFirebase(FoodModel foodModel) {

        FirebaseDatabase.getInstance()
                .getReference(Common.LIKE_REF)
                .child(foodModel.getId())
                .child(Common.currentUser.getPhone())
                .removeValue();

    }

    private void addLikeToFirebase() {

        Map<String,Object> data = new HashMap<>();
        data.put("name",Common.currentUser.getName());
        data.put("phone",Common.currentUser.getPhone());

        FirebaseDatabase.getInstance()
                .getReference(Common.LIKE_REF)
                .child(Common.SelectedFood.getId())
                .child(Common.currentUser.getPhone())
                .setValue(data);

    }

    private void showRatingDialog() {

        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View viewDialog = LayoutInflater.from(this).inflate(R.layout.rating_layout, null);
        dialog.setContentView(viewDialog);
        RatingLayoutBinding binding = RatingLayoutBinding.bind(viewDialog);
        binding.getRoot();

        binding.btnDismiss.setOnClickListener(view -> dialog.dismiss());

        binding.btnSend.setOnClickListener(view -> {

            if (binding.rating.getRating() == 0) {
                Toast.makeText(this, "برجاء تقييم المنتج", Toast.LENGTH_SHORT).show();
            } else if (binding.editComment.getText().toString().isEmpty()) {
                binding.editComment.setError("فارغ!");
            } else {

                CommentModel model = new CommentModel();
                model.setRatingValue(binding.rating.getRating());
                model.setComment(binding.editComment.getText().toString());
                model.setName(Common.currentUser.getName());
                model.setUid(Common.currentUser.getUid());

                Map<String, Object> serverTimeStamp = new HashMap<>();
                serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
                model.setCommentTimeStamp(serverTimeStamp);

                viewModel.setCommentModel(model);
                dialog.dismiss();

            }

        });


        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Common.currentUser == null)
            Common.currentUser =userModel;
        if(Common.Category_Selected == null)
            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(compositeDisposable != null)
            compositeDisposable.clear();
        EventBus.getDefault().removeAllStickyEvents();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        //if(fromPop != null && fromPop.equals("no"))
            //EventBus.getDefault().postSticky(new RefreshFoodListEvent(Common.SelectedFood, true));

        binding = null;
        orderNowBinding = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCartCounter(CounterCartEvent event) {
        if (event.isSuccess()) {
            countItemInCart();
        }
    }

}
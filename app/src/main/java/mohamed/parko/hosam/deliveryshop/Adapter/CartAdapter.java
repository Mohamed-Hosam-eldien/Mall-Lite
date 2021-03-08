package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDataSource;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartDatabase;
import mohamed.parko.hosam.deliveryshop.Database.cart.CartItem;
import mohamed.parko.hosam.deliveryshop.Database.cart.LocalCartDatabase;
import mohamed.parko.hosam.deliveryshop.EventBus.CounterCartEvent;
import mohamed.parko.hosam.deliveryshop.EventBus.UpdateCartItem;
import mohamed.parko.hosam.deliveryshop.Model.AddonModel;
import mohamed.parko.hosam.deliveryshop.Model.AddonWithPrice;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.LayoutItemCartBinding;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private final List<CartItem> cartItems;
    private final CartDataSource cartDataSource;
    private final TextView txtTotalPayment;
    private final Gson gson;


    public CartAdapter(Context context, List<CartItem> cartItems, TextView txtTotalPayment) {
        this.context = context;
        this.cartItems = cartItems;
        cartDataSource = new LocalCartDatabase(CartDatabase.getInstance(context).cartDao());
        this.txtTotalPayment = txtTotalPayment;
        gson = new Gson();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.foodNameCart.setText(ellipsize(cartItems.get(position).getFoodName()));
        AddonWithPrice addonWithPrice = null;

        //deposit is true
        if(!cartItems.get(position).getFoodDeposit().isEmpty()) {

            holder.binding.layoutDeposit.setVisibility(View.VISIBLE);
            holder.binding.txtDeposit.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                    .format(Double.parseDouble(cartItems.get(position).getFoodDeposit())));
            holder.binding.txtDeliverTime.setText(new StringBuilder("سيتم التوصيل خلال 10 أيام"));

        } else {

            holder.binding.layoutDeposit.setVisibility(View.GONE);
            holder.binding.txtDeliverTime.setText(new StringBuilder("سيتم التوصيل غداً"));

        }


        if (!cartItems.get(position).getFoodAddonWithPrice().equals("empty")) {
            addonWithPrice = gson.fromJson(cartItems.get(position).getFoodAddonWithPrice()
                    , new TypeToken<AddonWithPrice>() {
                    }.getType());
            holder.binding.txtTotalCart.setText((new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                    .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) *
                            Double.parseDouble(addonWithPrice.getPrice()))));
            holder.binding.txtFoodAddonWithPriceCart.setText(addonWithPrice.getName());
            holder.binding.layoutAddon.setVisibility(View.VISIBLE);
        } else {
            holder.binding.txtTotalCart.setText((new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                    .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) *
                            Double.parseDouble(cartItems.get(position).getFoodPrice()))));
        }


        if (!cartItems.get(position).getFoodAddon().equals("empty")) {

            List<AddonModel> addonList = gson.fromJson(cartItems.get(position).getFoodAddon()
                    , new TypeToken<List<AddonModel>>() {
                    }.getType());

            CartAddonImageAdapter adapter = new CartAddonImageAdapter(context, addonList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            layoutManager.setReverseLayout(true);

            holder.binding.recyclerAddonImage.setHasFixedSize(true);
            holder.binding.recyclerAddonImage.setLayoutManager(layoutManager);
            holder.binding.recyclerAddonImage.setAdapter(adapter);

            holder.binding.recyclerAddonImage.setVisibility(View.VISIBLE);

        }


        holder.binding.txtQuantityCart.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                .format(Double.parseDouble(cartItems.get(position).getFoodQuantity())));


        AddonWithPrice finalAddonWithPrice = addonWithPrice;
        holder.binding.txtPlus.setOnClickListener(view -> {

            double stepValue = Double.parseDouble(cartItems.get(position).getFoodMiniWeight());

            if (Double.parseDouble(holder.binding.txtQuantityCart.getText().toString()) >= stepValue) {


                double oldValue = Double.parseDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                        .format(Double.parseDouble(holder.binding.txtQuantityCart.getText().toString())));

                holder.binding.txtQuantityCart.setText(formatArabicNumber(oldValue + stepValue));
                cartItems.get(position).setFoodQuantity(formatArabicNumber(oldValue + stepValue));

                // for deposit
                if (!cartItems.get(position).getFoodDeposit().isEmpty() && finalAddonWithPrice != null) {
                    String deposit = new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                            .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) *
                                    Double.parseDouble(finalAddonWithPrice.getPrice()) * 70 / 100); // null
                    holder.binding.txtDeposit.setText(deposit);
                    cartItems.get(position).setFoodDeposit(deposit);
                }

                if(!cartItems.get(position).getFoodAddonWithPrice().equals("empty")) {

                    holder.binding.txtTotalCart.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                            .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) * Double.parseDouble(finalAddonWithPrice.getPrice())));
                } else {
                    holder.binding.txtTotalCart.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                            .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) * Double.parseDouble(cartItems.get(position).getFoodPrice())));
                }

                EventBus.getDefault().postSticky(new UpdateCartItem(cartItems.get(position)));

            }

        });

        holder.binding.txtMinus.setOnClickListener(view -> {

            double stepValue = Double.parseDouble(cartItems.get(position).getFoodMiniWeight());

            if (Double.parseDouble(holder.binding.txtQuantityCart.getText().toString()) > stepValue) {


                double oldValue = Double.parseDouble(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                        .format(Double.parseDouble(holder.binding.txtQuantityCart.getText().toString())));

                holder.binding.txtQuantityCart.setText(formatArabicNumber(oldValue - stepValue));
                cartItems.get(position).setFoodQuantity(formatArabicNumber(oldValue - stepValue));

                // for deposit
                if (!cartItems.get(position).getFoodDeposit().isEmpty() && finalAddonWithPrice != null) {
                    String deposit = new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                            .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) *
                                    Double.parseDouble(finalAddonWithPrice.getPrice()) * 70 / 100); // null
                    holder.binding.txtDeposit.setText(deposit);
                    cartItems.get(position).setFoodDeposit(deposit);
                }

                if(!cartItems.get(position).getFoodAddonWithPrice().equals("empty")) {

                    holder.binding.txtTotalCart.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                            .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) * Double.parseDouble(finalAddonWithPrice.getPrice())));
                } else {
                    holder.binding.txtTotalCart.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                            .format(Double.parseDouble(cartItems.get(position).getFoodQuantity()) * Double.parseDouble(cartItems.get(position).getFoodPrice())));
                }

                EventBus.getDefault().postSticky(new UpdateCartItem(cartItems.get(position)));

            }
        });

        holder.binding.imageDeleteItem.setOnClickListener(view ->
                cartDataSource.deleteCartItem(cartItems.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {}
                            @Override
                            public void onSuccess(@NonNull Integer position) {
                                notifyItemRemoved(position);
                                EventBus.getDefault().postSticky(new CounterCartEvent(true));

                                //EventBus.getDefault().postSticky(new UpdateCartItem(cartItems.get(position)));
                                calculateTotalPrice();
                                //countItemInCart();
                            }
                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d("DEBUG_DELETE_ITEM", "" + e.getMessage());
                            }
                        }));


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutItemCartBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = LayoutItemCartBinding.bind(itemView);

        }

    }

    private void calculateTotalPrice() {

        cartDataSource.sumPriceInCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Double price) {

                        txtTotalPayment.setText(new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.ENGLISH))
                                .format(price));

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if(e.getMessage().contains("empty")) {
                            txtTotalPayment.setText(R.string._0_00);
                        } else
                            Log.d("CART_UPDATE2", "" + e.getMessage());
                    }
                });

    }

    private String ellipsize(String input) {
        if (input == null || input.length() < 23) {
            return input;
        }
        return input.substring(0, 23) + "...";
    }

    private String formatArabicNumber(double number) {

        if ((number == Math.floor(number)) && !Double.isInfinite(number)) {
            DecimalFormat decimalFormat = new DecimalFormat("0.#####", new DecimalFormatSymbols(Locale.ENGLISH));
            String num = decimalFormat.format((number));
            return String.valueOf(Integer.parseInt((num)));
        } else {
            return String.valueOf(((number)));
        }
    }

}

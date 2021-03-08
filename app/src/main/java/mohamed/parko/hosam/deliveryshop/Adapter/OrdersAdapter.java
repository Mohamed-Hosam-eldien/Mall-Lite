package mohamed.parko.hosam.deliveryshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mohamed.parko.hosam.deliveryshop.Callback.IRecyclerClickListener;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.EventBus.ShowOrderFoodsEvent;
import mohamed.parko.hosam.deliveryshop.Model.OrderModel;
import mohamed.parko.hosam.deliveryshop.OrderFoods;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.OrderLayoutBinding;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.viewHolder> {

    private final List<OrderModel> orderModelList;
    private final Context context;
    private final Calendar calendar;
    private final SimpleDateFormat dateFormat;

    public OrdersAdapter(List<OrderModel> orderModelList, Context context) {
        this.orderModelList = orderModelList;
        this.context = context;
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy  -  HH:mm", Locale.ENGLISH);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context)
                .inflate(R.layout.order_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        calendar.setTimeInMillis(orderModelList.get(position).getTime());
        Date date = new Date(orderModelList.get(position).getTime());

        holder.binding.orderDate.setText(new StringBuilder(Common.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
                .append("  ")
                .append(dateFormat.format(date)));

        holder.binding.orderNumber.setText(orderModelList.get(position).getOrderNumber().substring(orderModelList.get(position).getOrderNumber().length() - 4));
        holder.binding.orderState.setText(Common.convertNumberToState(orderModelList.get(position).getState()));

        switch (orderModelList.get(position).getState()) {

            case 1 :
                holder.binding.orderLogo.setImageResource(R.drawable.ic_order_waiting_info_24);
                holder.binding.orderState.setText("قيد الإنتظار...");
                holder.binding.orderState.setTextColor(context.getResources().getColor(R.color.orange));
                break;

            case 2 :
                holder.binding.orderLogo.setImageResource(R.drawable.ic_order_review_24);
                holder.binding.orderState.setText("جاري فحص طلبك..");
                holder.binding.orderState.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;

            case 3 :
                holder.binding.orderLogo.setImageResource(R.drawable.ic_order_accept_24);
                holder.binding.orderState.setText("تمت الموافقة على طلبك");
                holder.binding.orderState.setTextColor(context.getResources().getColor(R.color.lightMoov));

                break;

            case 4 :
                holder.binding.orderLogo.setImageResource(R.drawable.ic_order_shipping_24);
                holder.binding.orderState.setText("مندوبنا في الطريق إليك الآن..");
                holder.binding.orderState.setTextColor(context.getResources().getColor(R.color.blueDark));
                break;

            case 5 :
                holder.binding.orderLogo.setImageResource(R.drawable.ic_order_complete_circle_24);
                holder.binding.orderState.setText("تم توصيل الطلب بنجاح");
                holder.binding.orderState.setTextColor(context.getResources().getColor(R.color.green));
                break;

            case 6 :
                holder.binding.orderLogo.setImageResource(R.drawable.ic_order_refuse_24);
                holder.binding.orderState.setText("تم إلغاء الطلب");
                holder.binding.orderState.setTextColor(context.getResources().getColor(R.color.red));
                break;


        }

        holder.setClickListener((view, position1) -> {

            EventBus.getDefault().postSticky(new ShowOrderFoodsEvent(orderModelList.get(position)));
            context.startActivity(new Intent(context, OrderFoods.class));

        });


    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }


    public static class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OrderLayoutBinding binding;

        IRecyclerClickListener clickListener;

        public void setClickListener(IRecyclerClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = OrderLayoutBinding.bind(itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClickListener(view,getAdapterPosition());
        }
    }


}

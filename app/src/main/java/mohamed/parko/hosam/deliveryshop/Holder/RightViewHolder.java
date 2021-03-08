package mohamed.parko.hosam.deliveryshop.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import mohamed.parko.hosam.deliveryshop.R;

public class RightViewHolder extends RecyclerView.ViewHolder {

    public TextView txtMessage, txtTime;
    public ImageView imgMessageState;


    public RightViewHolder(@NonNull View itemView) {
        super(itemView);
        txtMessage = itemView.findViewById(R.id.message_content);
        txtTime = itemView.findViewById(R.id.message_time);
        imgMessageState = itemView.findViewById(R.id.imgMessageState);
    }

}

package mohamed.parko.hosam.deliveryshop.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import mohamed.parko.hosam.deliveryshop.Common.Common;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDataSource;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressDatabase;
import mohamed.parko.hosam.deliveryshop.Database.address.AddressItem;
import mohamed.parko.hosam.deliveryshop.Database.address.LocalAddressDatabase;
import mohamed.parko.hosam.deliveryshop.R;
import mohamed.parko.hosam.deliveryshop.databinding.DialogAddAddressBinding;
import mohamed.parko.hosam.deliveryshop.databinding.LayoutAddressBinding;
import mohamed.parko.hosam.deliveryshop.databinding.LayoutNotiesBinding;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final Context context;
    private final List<AddressItem> addressItemList;
    private final AddressDataSource addressDataSource;
    private final List<String> zoneModels;

    public AddressAdapter(Context context, List<AddressItem> addressItemList, List<String> zoneModels) {
        this.context = context;
        this.addressItemList = addressItemList;
        addressDataSource = new LocalAddressDatabase(AddressDatabase.getInstance(context).addressDao());
        this.zoneModels = zoneModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_address, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.nameFinalItem.setText(addressItemList.get(position).getAddressName());

        if (addressItemList.get(position).getBuildName().isEmpty()) {

            holder.binding.textAddress.setText(new StringBuilder(addressItemList.get(position).getBuildNumber())
                    .append(" ")
                    .append("شارع ")
                    .append(addressItemList.get(position).getStreetName())
                    .append(" - ")
                    .append(addressItemList.get(position).getZone())
                    .append(" شقة رقم : ")
                    .append(addressItemList.get(position).getFlatNumber())
                    .append(" -")
                    .append(" الدور : ")
                    .append(addressItemList.get(position).getFloorNumber()));

        } else {

            holder.binding.textAddress.setText(new StringBuilder(addressItemList.get(position).getBuildNumber())
                    .append(" مبنى ")
                    .append(addressItemList.get(position).getBuildName())
                    .append(" شارع ")
                    .append(addressItemList.get(position).getStreetName())
                    .append(" - ")
                    .append(addressItemList.get(position).getZone())
                    .append(" شقة رقم : ")
                    .append(addressItemList.get(position).getFlatNumber())
                    .append(" -")
                    .append(" الدور : ")
                    .append(addressItemList.get(position).getFloorNumber()));

        }

        if (!addressItemList.get(position).getDetails().isEmpty()) {
            holder.binding.textDetails.setText(addressItemList.get(position).getDetails());
            holder.binding.textDetails.setVisibility(View.VISIBLE);
        }

        /*holder.binding.nameBuildItem.setText(addressItemList.get(position).getBuildName());
        holder.binding.numBuildItem.setText(addressItemList.get(position).getBuildNumber());

        holder.binding.numberFlat.setText(addressItemList.get(position).getFlatNumber());
        holder.binding.numberFloor.setText(addressItemList.get(position).getFloorNumber());

        holder.binding.areaItem.setText(addressItemList.get(position).getZone());
        holder.binding.descriptionItem.setText(addressItemList.get(position).getDetails());

        holder.binding.nameFinalItem.setText(addressItemList.get(position).getAddressName());
        holder.binding.nameStreetItem.setText(addressItemList.get(position).getStreetName());

        if (!addressItemList.get(position).getDetails().isEmpty()) {
            holder.binding.descriptionItem.setVisibility(View.VISIBLE);
        }

        if (!addressItemList.get(position).getBuildName().isEmpty()) {
            holder.binding.nameBuildItem.setVisibility(View.VISIBLE);
        }*/

        holder.binding.imgAddressDelete.setOnClickListener(view -> deleteItem(position));

        holder.binding.imgAddressEdit.setOnClickListener(view -> editItem(position));

    }

    private void editItem(int position) {

        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_address, null);
        DialogAddAddressBinding bindingDialog = DialogAddAddressBinding.bind(dialogView);
        dialog.setContentView(dialogView);

        bindingDialog.nameStreet.setText(addressItemList.get(position).getStreetName());
        bindingDialog.flatNumber.setText(addressItemList.get(position).getFlatNumber());
        bindingDialog.floor.setText(addressItemList.get(position).getFloorNumber());
        bindingDialog.nameBuild.setText(addressItemList.get(position).getBuildName());
        bindingDialog.numBuild.setText(addressItemList.get(position).getBuildNumber());

        bindingDialog.addressInformation.setText(addressItemList.get(position).getDetails());

        bindingDialog.addressName.setText(addressItemList.get(position).getAddressName());
        bindingDialog.addressName.setVisibility(View.VISIBLE);

        ArrayAdapter<String> zoneAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, zoneModels);
        zoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        zoneAdapter.sort(String::compareTo);
        bindingDialog.area.setAdapter(zoneAdapter);

        // set selected zone to spinner
        int spinnerPosition = zoneAdapter.getPosition(addressItemList.get(position).getZone());
        bindingDialog.area.setSelection(spinnerPosition);

        bindingDialog.btnOk.setOnClickListener(view -> {

            AddressItem updateAddress = new AddressItem();
            updateAddress.setZone(String.valueOf(bindingDialog.area.getSelectedItem()));
            updateAddress.setDetails(String.valueOf(bindingDialog.addressInformation.getText()));
            updateAddress.setFlatNumber(String.valueOf(bindingDialog.flatNumber.getText()));
            updateAddress.setFloorNumber(String.valueOf(bindingDialog.floor.getText()));
            updateAddress.setStreetName(String.valueOf(bindingDialog.nameStreet.getText()));
            updateAddress.setBuildNumber(String.valueOf(bindingDialog.numBuild.getText()));
            updateAddress.setBuildName(String.valueOf(bindingDialog.nameBuild.getText()));
            updateAddress.setAddressName(String.valueOf(bindingDialog.addressName.getText()));
            updateAddress.setUid(Common.currentUser.getUid());

            addressDataSource.updateAddressItem(updateAddress)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onSuccess(@NonNull Integer integer) {
                            notifyItemChanged(integer);
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                        }
                    });

        });

        dialog.show();

    }

    private void deleteItem(int position) {

        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View viewDialog = LayoutInflater.from(context).inflate(R.layout.layout_noties, null);
        dialog.setContentView(viewDialog);
        LayoutNotiesBinding binding = LayoutNotiesBinding.bind(viewDialog);

        binding.dialogImage.setImageResource(R.drawable.ic_baseline_delete_24);
        binding.dialogTitle.setText("حذف عنوان !");
        binding.dialogDescription.setText("هل تريد حذف هذا العنوان من قائمة عناوينك؟");

        binding.dialogAccept.setOnClickListener(view1 -> {

            addressDataSource.deleteAddressItem(addressItemList.get(position))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onSuccess(@NonNull Integer pos) {
                            notifyItemRemoved(pos);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                        }
                    });

            dialog.dismiss();

        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return addressItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutAddressBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = LayoutAddressBinding.bind(itemView);

        }
    }

}

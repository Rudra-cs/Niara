package com.example.niara.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.niara.Model.CustomerInfo;
import com.example.niara.Model.Food;
import com.example.niara.R;
import com.example.niara.ui.activities.PaymentActivity;
import com.example.niara.ui.fragments.SettingsFragment;
import com.example.niara.utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomerInfoAdapter extends RecyclerView.Adapter<CustomerInfoAdapter.CustomerInfoViewHodler> {
    private Context context;
    private List<CustomerInfo> customerinfolist;
    private ItemClickListener clickListener;

    public CustomerInfoAdapter(Context context, List<CustomerInfo> customerinfolist,ItemClickListener clickListener) {
        this.context = context;
        this.customerinfolist = customerinfolist;
        this.clickListener=clickListener;

    }

    @NonNull
    @NotNull
    @Override
    public CustomerInfoViewHodler onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new CustomerInfoViewHodler(LayoutInflater.from(context).inflate(R.layout.address_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CustomerInfoAdapter.CustomerInfoViewHodler holder, int position) {
        CustomerInfo address=customerinfolist.get(position);
        holder.name.setText(address.getName());
        holder.locality.setText(address.getLocality());
        holder.city.setText(address.getCity());
        holder.zipcode.setText(address.getZipcode());
        holder.mobile.setText(address.getMobile());
        holder.state.setText(address.getState());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(customerinfolist.get(position));
                Toast.makeText(v.getContext(), "Address selected :"+customerinfolist.get(position).getLocality(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerinfolist.size();
    }

    public static class CustomerInfoViewHodler extends RecyclerView.ViewHolder{
        private TextView name,locality,city,zipcode,mobile,state,nametag,localitytag,citytag,zipcodetag,mobiletag,statetag;
        private LinearLayout mrladdress;
        public CustomerInfoViewHodler(@NonNull @NotNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name);
            locality=itemView.findViewById(R.id.locality);
            city=itemView.findViewById(R.id.city);
            zipcode=itemView.findViewById(R.id.zipcode);
            mobile=itemView.findViewById(R.id.mobile);
            state=itemView.findViewById(R.id.state);

            nametag=itemView.findViewById(R.id.nameTag);
            localitytag=itemView.findViewById(R.id.localityTag);
            citytag=itemView.findViewById(R.id.cityTag);
            zipcodetag=itemView.findViewById(R.id.zipcodeTag);
            mobiletag=itemView.findViewById(R.id.mobileTag);
            statetag=itemView.findViewById(R.id.stateTag);

            mrladdress=itemView.findViewById(R.id.addressCategory);



        }
    }
    public interface ItemClickListener {
        void onItemClick(CustomerInfo address);
    }

}

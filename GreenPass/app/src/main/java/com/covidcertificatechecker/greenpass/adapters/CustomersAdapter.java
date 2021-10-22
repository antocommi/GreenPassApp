package com.covidcertificatechecker.greenpass.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.covidcertificatechecker.greenpass.R;
import com.covidcertificatechecker.greenpass.models.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomerViewHolder> {

    ArrayList<Customer> mlist;

    public CustomersAdapter(ArrayList<Customer> list){
        this.mlist = list;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_layout, parent, false);
        return new CustomerViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        holder.name.setText(mlist.get(position).getName());
        holder.vacDate.setText("Vaccination Date: " + mlist.get(position).getVacDate());
        holder.vacCountry.setText("Vaccinated in: " + mlist.get(position).getVacCountry());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Customers").child(mlist.get(holder.getAdapterPosition()).getId());
                dbRef.child("outTime").setValue(String.valueOf(System.currentTimeMillis()));
                dbRef.child("available").setValue("false");

                Toast.makeText(v.getContext(), "Customer Deleted Successfully", Toast.LENGTH_SHORT).show();
                mlist.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }
    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder{

        TextView name, vacDate, vacCountry;
        ImageView delete;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameText);
            vacDate = itemView.findViewById(R.id.vacDate);
            vacCountry = itemView.findViewById(R.id.vacCountry);
            delete = itemView.findViewById(R.id.deleteOption);
        }
    }

}

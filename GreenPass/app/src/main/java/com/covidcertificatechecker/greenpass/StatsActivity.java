package com.covidcertificatechecker.greenpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.covidcertificatechecker.greenpass.models.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;

public class StatsActivity extends AppCompatActivity {

    TextView totalCustomers, busyHours, averageStay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        totalCustomers = findViewById(R.id.totalCustomersShow);
        busyHours = findViewById(R.id.busyHoursShow);
        averageStay = findViewById(R.id.averageStayShow);

        loadTotalCustomers();
        loadBusyHours();
        StayinApp();

    }
    private void loadTotalCustomers(){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Customers");
        Query query = dbRef.orderByChild("storeId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalCustomers.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadBusyHours(){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Customers");
        Query query = dbRef.orderByChild("storeId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int arr[] = new int[(int) snapshot.getChildrenCount()];
                int i = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    Customer customer = ds.getValue(Customer.class);

                    arr[i] = getHourfromMillis(Long.parseLong(customer.getInTime()));
                    i++;

                }
                if (i > 0){
                    int n = mostFrequent(arr, i);
                    busyHours.setText(n +" - " + (n+1));
                }else{
                    busyHours.setText("No Data Available");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void StayinApp(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Customers");
        Query query = dbRef.orderByChild("storeId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int arr[] = new int[(int) snapshot.getChildrenCount()];
                int i = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    Customer customer = ds.getValue(Customer.class);

                    try{
                        arr[i] = getHourfromMillis(Long.parseLong(customer.getInTime())) - getHourfromMillis( Long.parseLong(customer.getOutTime()));
                        i++;
                    }catch (Exception e){
                        //do nothing
                    }
                }
                if (i>0){
                    double n = average(arr, i);
                    averageStay.setText("" + n + " Mins");
                }else{
                    averageStay.setText("No Data Available");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public int mostFrequent(int arr[], int n)
    {

        // Sort the array
        Arrays.sort(arr);

        // find the max frequency using linear
        // traversal
        int max_count = 1, res = arr[0];
        int curr_count = 1;

        for (int i = 1; i < n; i++)
        {
            if (arr[i] == arr[i - 1])
                curr_count++;
            else
            {
                if (curr_count > max_count)
                {
                    max_count = curr_count;
                    res = arr[i - 1];
                }
                curr_count = 1;
            }
        }

        // If last element is most frequent
        if (curr_count > max_count)
        {
            max_count = curr_count;
            res = arr[n - 1];
        }

        return res;
    }

    static double average(int a[], int n)
    {

        // Find sum of array element
        int sum = 0;

        for (int i = 0; i < n; i++)
            sum += a[i];

        return (double)sum / n;
    }

    private int getYearfromMillis(Long l){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        int Year = calendar.get(Calendar.YEAR);
        return Year;
    }

    private int getMonthfromMillis(Long l){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        int Month = calendar.get(Calendar.MONTH);
        return Month;
    }

    private int getDayfromMillis(Long l){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);;
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        return cDay;
    }

    private int getHourfromMillis(Long l){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        int cHour =  calendar.get(Calendar.HOUR_OF_DAY);
        return cHour;
    }
}
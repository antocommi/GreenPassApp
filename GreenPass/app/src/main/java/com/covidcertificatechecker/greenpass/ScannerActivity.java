package com.covidcertificatechecker.greenpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.chaquo.python.Python;
import com.covidcertificatechecker.greenpass.models.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pub.devrel.easypermissions.EasyPermissions;

public class ScannerActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    CodeScanner codeScanner;
    CodeScannerView codeScannerView;
    String text;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        codeScannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, codeScannerView);

       // CheckCamPermissions();

        pd = new ProgressDialog(this);
        pd.setMessage("Validating....");
        codeScanner.setDecodeCallback( new DecodeCallback(){


            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text = result.getText();
                        String json = getJsonString(text.replace("HC1:", ""));
                        if (json.equals("error")){
                            new AlertDialog.Builder(ScannerActivity.this)
                                    .setTitle("Error")
                                    .setMessage("There was an error in scanning the QR code or the scanned QR code is not a covid certificate.")

                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    })
                                    .show();
                        }else{
                            try {
                                pd.show();
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Customers");
                                Customer customer = new Customer();
                                JSONObject jsonObject = new JSONObject(json);
                                customer.setId(dbRef.push().getKey());
                                customer.setName(jsonObject.getJSONObject("-260").getJSONObject("1").getJSONObject("nam").getString("fnt")
                                    + " " + jsonObject.getJSONObject("-260").getJSONObject("1").getJSONObject("nam").getString("gnt"));
                                customer.setVacDate(epochtoDate(jsonObject.getLong("6") * 1000));
                                customer.setVacCountry(getCountryName(jsonObject.getString("1")));
                                customer.setStoreId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                customer.setInTime(String.valueOf(System.currentTimeMillis()));
                                customer.setAvailable("true");
                                dbRef.child(customer.getId()).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ScannerActivity.this, "Customer Added Successfully.", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                            finish();
                                        }else{
                                            pd.dismiss();
                                            Toast.makeText(ScannerActivity.this, "Firebase Exception: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ScannerActivity.this, "An error occurred while parsing json.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckCamPermissions();
            }
        });

    }

    public String getJsonString(String str){
        Python python = Python.getInstance();
        return python.getModule("decode").callAttr("main", ""+str).toString();
    }


    @Override
    protected void onResume() {
        super.onResume();
        CheckCamPermissions();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }


    private void CheckCamPermissions() {
        String perm = Manifest.permission.CAMERA;
        if (EasyPermissions.hasPermissions(this, perm)) {
            codeScanner.startPreview();
        } else {
            EasyPermissions.requestPermissions(this, "We need camera permission to scan QR.",
                    124, perm);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        codeScanner.startPreview();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //new AppSettingsDialog.Builder(this).build().show();
            CheckCamPermissions();
        }
    }

    private String epochtoDate(long l){
        Date date = new Date(l);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        return formatted;
    }

    private String getCountryName(String s){
        Locale l = new Locale("", s);
        String country = l.getDisplayCountry();
        return country;
    }
}
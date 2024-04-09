package com.moonpo.activity;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.os.Bundle;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.moonpo.R;
import com.moonpo.model.Employee;
import com.moonpo.receivers.BarcodeDataReceiver;
import com.moonpo.retrofit.EmployeeApi;
import com.moonpo.retrofit.RetrofitService;
import com.moonpo.service.EmployeeService;
import com.moonpo.utils.ImageUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQrActivity extends AppCompatActivity {

    EmployeeService employeeService = new EmployeeService();
    EditText edtId, edtNo, edtName, edtDateOfBirth, edtSex, edtIssueDate, edtAddress;
    Button btnSave, btnReset, btnTakePhoto;
    ImageView imagePhoto;
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BarcodeDataReceiver.ACTION_DECODE_DATA)) {
                String scannedData = intent.getStringExtra("scanned_data");
                // FillData Scan Qr
                //Log.d(TAG, "Received scanned:" + scannedData);
                fillDataFrom(scannedData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        // Mapping data from
        getActivityForm();
        // BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(BarcodeDataReceiver.ACTION_DECODE_DATA);
        registerReceiver(dataReceiver, intentFilter);
        resetForm();
        takePhoto();
        saveData();
    }

    private void saveData() {


        RetrofitService retrofitService = new RetrofitService();
        EmployeeApi employeeApi = retrofitService.getRetrofit().create(EmployeeApi.class);

        btnSave.setOnClickListener(view -> {
            Employee employee = getEmployeeInfo();
            String check = employeeService.checkEmployeeFormView(employee);
            if (check.equals("Success")) {
                employeeApi.save(employee).enqueue(new Callback<Employee>() {
                    @Override
                    public void onResponse(Call<Employee> call, Response<Employee> response) {
                        if (response.isSuccessful()) {
                            Employee savedEmployee = response.body();
                            Toast.makeText(ScanQrActivity.this, "Save Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ScanQrActivity.this, "Save Failed Id Existed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Employee> call, Throwable t) {
                        Toast.makeText(ScanQrActivity.this, "Save failed!!!", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(ScanQrActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
                    }
                });
            } else {
                Toast.makeText(ScanQrActivity.this, check, Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void takePhoto() {
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khai Báo Intent Ẩn gọi đến ACTION_IMAGE_CAPTURE
                Intent cameraIntent = new Intent(ACTION_IMAGE_CAPTURE);
                // Yêu Cầu Quyền Truy Cập
                if (ActivityCompat.checkSelfPermission(ScanQrActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanQrActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    return;
                }
                // Start and wait results
                startActivityForResult(cameraIntent, 99);
            }

        });
    }

    private void getActivityForm() {
        edtId = findViewById(R.id.edt_id);
        edtNo = findViewById(R.id.edt_no);
        edtName = findViewById(R.id.edt_name);
        edtDateOfBirth = findViewById(R.id.edt_dateofbirth);
        edtSex = findViewById(R.id.edt_sex);
        edtIssueDate = findViewById(R.id.edt_issuedate);
        edtAddress = findViewById(R.id.edt_address);
        btnSave = findViewById(R.id.btn_save);
        btnReset = findViewById(R.id.btn_reset);
        btnTakePhoto = findViewById(R.id.btn_takephoto);
        imagePhoto = findViewById(R.id.img_photo);
        edtId.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imagePhoto.setImageBitmap(photo);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dataReceiver);
    }

    private void fillDataFrom(String scannedData) {
        // Get data
        String[] parts = employeeService.splitString(scannedData, '|');
        resetForm();
        // Set data to form
        edtNo.setText(parts[0]);
        // parts[1] is null for cards
        edtName.setText(parts[2]);
        edtDateOfBirth.setText(employeeService.formatDateToForm(parts[3]));
        edtSex.setText(parts[4]);
        edtAddress.setText(parts[5]);
        edtIssueDate.setText(employeeService.formatDateToForm(parts[6]));
    }

    private void resetForm() {
        btnReset.setOnClickListener(v -> {
            edtId.setText("");
            edtNo.setText("");
            edtName.setText("");
            edtDateOfBirth.setText("");
            edtSex.setText("");
            edtIssueDate.setText("");
            edtAddress.setText("");
            imagePhoto.setImageResource(R.drawable.avatar);
        });
    }


    private Employee getEmployeeInfo() {
        Employee employee = new Employee();
        employee.setId(edtId.getText().toString());
        employee.setNo(edtNo.getText().toString());
        employee.setName(edtName.getText().toString());
        employee.setDateOfBirth(edtDateOfBirth.getText().toString());
        employee.setGender(edtSex.getText().toString());
        employee.setIssueDate(edtIssueDate.getText().toString());
        employee.setAddress(edtAddress.getText().toString());
        employee.setPhoto(ImageUtils.imageToBase64(imagePhoto));
        return employee;
    }
}
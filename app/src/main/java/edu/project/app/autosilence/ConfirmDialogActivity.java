package edu.project.app.autosilence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfirmDialogActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String GEO_LATITUDE = "geo_latitude";
    public static final String GEO_LONGITUDE = "geo_longitude";
    public static final String GEO_RADIUS = "geo_radius";
    public static final String GEO_NAME = "geo_name";
    public static final String GEO_ADDRESS = "geo_address";

    public static final String FROM_CONFIRM_ACTIVITY = "fromConfirmActivity";

    EditText etName, etLatitude, etLongitude,etRadius, etAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_dialog);

        Button button = findViewById(R.id.buttonCancel);
        button.setOnClickListener(this);
        button = findViewById(R.id.buttonConfirm);
        button.setOnClickListener(this);


        etName = findViewById(R.id.editTextName);
        etLatitude = findViewById(R.id.editTextLatitude);
        etLongitude = findViewById(R.id.editTextLongitude);
        etRadius = findViewById(R.id.editTextRadius);
        etAddress = findViewById(R.id.editTextAddress);

        Intent intent = getIntent();
        etName.setText(intent.getStringExtra(GEO_NAME));
        etLatitude.setText(String.valueOf(intent.getDoubleExtra(GEO_LATITUDE, 0.0)));
        etLongitude.setText(String.valueOf(intent.getDoubleExtra(GEO_LONGITUDE, 0.0)));
        etRadius.setText(String.valueOf(intent.getFloatExtra(GEO_RADIUS, 100.0f)));
        etAddress.setText(intent.getStringExtra(GEO_ADDRESS));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonCancel) {
            setResult(RESULT_CANCELED);
            finish();
        } else if(v.getId() == R.id.buttonConfirm){
            String lat = etLatitude.getText().toString().trim();
            if(lat.equals("")){
                Toast.makeText(this, "Invalid Latitude value!", Toast.LENGTH_SHORT).show();
                return;
            }
            String lng = etLongitude.getText().toString().trim();
            if (lng.equals("")) {
                Toast.makeText(this, "Invalid Longitude value!", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = etName.getText().toString().trim();
            if (name.equals("")) {
                Toast.makeText(this, "Enter a name for the geofence!", Toast.LENGTH_SHORT).show();
                return;
            }
            String rad = etRadius.getText().toString().trim();
            if (rad.equals("")|| rad.startsWith("-")||Float.parseFloat(rad)>2000) {
                Toast.makeText(this, "Enter radius between 0 m to 2000 m", Toast.LENGTH_SHORT).show();
                return;
            }
            String address = etAddress.getText().toString().trim();
            if (address.equals("")) {
                Toast.makeText(this, "Adding address is better for understanding later", Toast.LENGTH_SHORT).show();
            }
            Intent resultData = new Intent();
            resultData.putExtra(GEO_NAME, name)
                    .putExtra(GEO_LATITUDE,Double.parseDouble(lat))
                    .putExtra(GEO_LONGITUDE,Double.parseDouble(lng))
                    .putExtra(GEO_RADIUS,Float.parseFloat(rad))
                    .putExtra(GEO_ADDRESS,address)
                    .putExtra(MainActivity.ACTIVITY_FROM,FROM_CONFIRM_ACTIVITY);
            setResult(RESULT_OK,resultData);
            finish();
        }
    }
}

package trap1.bhaleraoomkar.chatroomfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference loginRef;
    private DatabaseReference messageRef;
    private Button login;
    private Button signup;
    private EditText username;
    private EditText password;
    private Geocoder geocoder;
    private SignIn success = new SignIn();
    private FusedLocationProviderClient fusedLocationClient;
    private String username2 = "";
    int counter = 0;


    ValueEventListener updateListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Toast toast = Toast.makeText(getApplicationContext(), "Value was updated to "+dataSnapshot.getValue(), Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("e", "loadValue:onCancelled", databaseError.toException());
            // ...
        }
    };

    ValueEventListener checkLast = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            long lastMessage = (Long)dataSnapshot.child("lastMessage/id").getValue();
            Intent send = new Intent(getApplicationContext(), NotificationService.class);
            send.putExtra("id", lastMessage);
            startService(send);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    View.OnClickListener sign = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String usn = username.getText().toString();
            final String pw = password.getText().toString();
            loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.child(usn).exists() || usn.equals("")){
                        username.setText("");
                        password.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(), "Username already exists or is invalid", Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Map<String, Object> updates = new HashMap<String, Object>();
                        DatabaseReference newRef = database.getReferenceFromUrl(snapshot.child(usn).getRef().toString());
                        updates.put("password", pw);
                        updates.put("online","false");
                        newRef.updateChildren(updates);
                        success.setBoo(true);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("e", "loadValue:onCancelled", databaseError.toException());
                    // ...
                }
            });

        }
    };

    View.OnClickListener log = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String usn = username.getText().toString();
            username2 = usn;
            final String pw = password.getText().toString();
            loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (usn.equals("") || !snapshot.child(usn).exists() || !((String)snapshot.child(usn).child("password").getValue()).equals(pw)) {
                        username.setText("");
                        password.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_LONG);
                        toast.show();
                    }else if(snapshot.child(usn).child("online").getValue().toString().equals("true")){
                        username.setText("");
                        password.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(), "User is already online", Toast.LENGTH_LONG);
                        toast.show();
                    }else {
                        username.setText("");
                        password.setText("");
                        Map<String, Object> updates = new HashMap<String, Object>();
                        DatabaseReference newRef = database.getReferenceFromUrl(snapshot.child(usn).getRef().toString());
                        updates.put("online","true");
                        newRef.updateChildren(updates);
                        updateLastLocation();
                        Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
                        intent.putExtra(getString(R.string.username_key), usn);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("e", "loadValue:onCancelled", databaseError.toException());
                }
            });
        }
    };

    private void updateLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Map<String, Object> updates = new HashMap<String, Object>();
                            double lat = location.getLatitude();
                            double longi = location.getLongitude();
                            updates.put("latitude", lat);
                            updates.put("longitude", longi);
                            try {
                                Address address = geocoder.getFromLocation(lat, longi, 1).get(0);
                                String city = address.getLocality();
                                String country = address.getCountryName();
                                updates.put("city", city);
                                updates.put("country", country);
                                loginRef.child(username2).updateChildren(updates);
                            }catch(java.io.IOException e){
                                updates.put("city", "null");
                                updates.put("country", "null");
                                loginRef.child(username2).updateChildren(updates);
                            }
                        }
                    }
                });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        login = (Button)findViewById(R.id.login);
        signup = (Button)findViewById(R.id.signup);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginRef = database.getReference("users");
        messageRef = database.getReference("messages");
        signup.setOnClickListener(sign);
        login.setOnClickListener(log);
        messageRef.addListenerForSingleValueEvent(checkLast);
        success.setBoo(false);
        success.setListener(new SignIn.ChangeListener() {
            @Override
            public void onChange() {
                success.setBoo(false);
                log.onClick(findViewById(R.id.parent));
            }
        });
        geocoder = new Geocoder(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




    }
}

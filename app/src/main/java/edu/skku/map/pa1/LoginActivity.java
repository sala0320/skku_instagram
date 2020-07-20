package edu.skku.map.pa1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;
    String username = "", password = "";
    Button loginBtn;
    Intent intent, loginIntent, signupIntent;
    TextView usernameET, passwordET;
    TextView signup;
    Integer check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        loginBtn = (Button)findViewById(R.id.login);
        usernameET = (TextView) findViewById(R.id.username);
        passwordET = (TextView) findViewById(R.id.password);
        signup = (TextView)findViewById(R.id.signup);
        check = 0;
        signupIntent = new Intent(this, SignupActivity.class);
        loginIntent = new Intent(this, MainActivity.class);

        intent = getIntent();
        usernameET.setText(intent.getStringExtra("username_Intent"));


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signupIntent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();

                if(username.length() == 0){
                    Toast.makeText(LoginActivity.this, "Wrong Username.", Toast.LENGTH_LONG).show();
                }
                else if(check == 0){
                    mPostReference.addListenerForSingleValueEvent(checkPassword);
                }
            }
        });
    }
    public ValueEventListener checkPassword = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot postSnapshot : dataSnapshot.child("user_list").getChildren()) {
                String key = postSnapshot.getKey();
                if(username.equals(key)) {
                    UserInfo get = postSnapshot.getValue(UserInfo.class);
                    String pw = get.password;
                    String fn = get.fullname;
                    String bd = get.birthday.substring(0,4) + "/" + get.birthday.substring(4,6) + "/" + get.birthday.substring(6,8);
                    String em = get.email;

                    if(password.equals(pw)) {
                        check = 1;
                        loginIntent = new Intent(getApplication(), MainActivity.class);
                        loginIntent.putExtra("username_Intent", key);
                        loginIntent.putExtra("fullname_Intent", fn);
                        loginIntent.putExtra("birthday_Intent", bd);
                        loginIntent.putExtra("email_Intent", em);
                        startActivity(loginIntent);
                        return;
                    }
                }
            }
            Toast.makeText(LoginActivity.this, "Wrong Password.", Toast.LENGTH_LONG).show();
            check = 0;
            passwordET.setText("");
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
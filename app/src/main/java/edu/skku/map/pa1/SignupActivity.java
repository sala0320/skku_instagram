package edu.skku.map.pa1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;
    Intent signupIntent;
    String username = "", password = "", fullname = "", birthday = "", email = "";
    EditText usernameET, passwordET, fullnameET, birthdayET, emailET;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        usernameET = (EditText)findViewById(R.id.username);
        passwordET = (EditText)findViewById(R.id.password);
        fullnameET = (EditText)findViewById(R.id.fullname);
        birthdayET = (EditText)findViewById(R.id.birthday);
        emailET = (EditText)findViewById(R.id.email);
        signup = (Button)findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();
                fullname = fullnameET.getText().toString();
                birthday = birthdayET.getText().toString();
                email = emailET.getText().toString();
                Log.d("Register_username",username);

                if(username.length() * password.length() * fullname.length() * birthday.length() * email.length() == 0){
                    Toast.makeText(SignupActivity.this, "Please fill all blanks.", Toast.LENGTH_SHORT).show();
                }
                else{
                    mPostReference.addListenerForSingleValueEvent(checkDuplicate);
                }
            }
        });
    }
    public ValueEventListener checkDuplicate = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.child("user_list").getChildren().iterator();
                while (child.hasNext()) {
                    if (username.equals(child.next().getKey())) {
                        Toast.makeText(getApplicationContext(), "Please use another username.", Toast.LENGTH_SHORT).show();
                        mPostReference.removeEventListener(this);
                        return;
                    }
                }

                signupIntent = new Intent(getApplication(), LoginActivity.class);
                signupIntent.putExtra("username_Intent", username);
                startActivity(signupIntent);
                Log.d("Register_username_Sent",username);
                postFirebaseDatabase(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
    };

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        if(add){
            UserInfo post = new UserInfo(username, password, fullname, birthday, email);
            postValues = post.toMap();
        }
        childUpdates.put("/user_list/" + username , postValues);//id_list/ID 에 동시에 postvalue넣기
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET() {
        usernameET.setText("");
        passwordET.setText("");
        fullnameET.setText("");
        birthdayET.setText("");
        emailET.setText("");
        username = "";
        password = "";
        fullname = "";
        birthday = "";
        email = "";
    }
}

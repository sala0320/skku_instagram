package edu.skku.map.pa1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddpostActivity extends AppCompatActivity{
    private static final int PICK_IMAGE = 777;
    private static final long ONE_MEGABYTE = 1024 * 1024;
    private StorageReference mStorageRefContent;
    private StorageReference mStorageRefUser;
    private DatabaseReference mPostReference;
    public static Integer num = 0;;
    Uri currentImageUri;
    Toolbar tb;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Button postbtn;
    Intent postIntent, backIntent, intent;
    TextView usernameNav;
    ImageView userimageBtn;
    MenuItem fullnameNav, birthdayNav, emailNav;
    String username = "", fullname = "", birthday = "", email = "";
    EditText contentET, tagsET;
    String content ="", tags = "", check = "",title = "", image = "";
    String time;
    long now;
    Date date;
    SimpleDateFormat dateFormat;
    ImageButton imageBtn;
    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mStorageRefContent = FirebaseStorage.getInstance().getReference("ContentImages");
        mStorageRefUser = FirebaseStorage.getInstance().getReference("UserImages");
        mPostReference = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();
        username = intent.getStringExtra("username_Intent");
        fullname = intent.getStringExtra("fullname_Intent");
        birthday = intent.getStringExtra("birthday_Intent");
        email = intent.getStringExtra("email_Intent");

        now = System.currentTimeMillis();
        date = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = dateFormat.format(date);

        contentET = (EditText)findViewById(R.id.content);
        tagsET = (EditText)findViewById(R.id.tags);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        imageBtn = (ImageButton)findViewById(R.id.imageButton);
        check = "private";
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = "public";
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               title = time;
               Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
               startActivityForResult(gallery, PICK_IMAGE);
           }
        });
        StorageReference islandRefContent = mStorageRefContent.child("/"+username+"/" + title);
        islandRefContent.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageBtn.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        postbtn = (Button) findViewById(R.id.addpost);
        postIntent = new Intent(this, MainActivity.class);
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contentET.length() == 0){
                    Toast.makeText(AddpostActivity.this, "Please inputs contents.", Toast.LENGTH_SHORT).show();
                }
                else {
                    content = contentET.getText().toString();
                    tags = tagsET.getText().toString();
                    postFirebaseDatabase(true);
                    postIntent.putExtra("email_Intent", email);
                    postIntent.putExtra("birthday_Intent", birthday);
                    postIntent.putExtra("fullname_Intent", fullname);
                    postIntent.putExtra("username_Intent", username);
                    startActivity(postIntent);
                }
            }
        });

        /*-------------Toolbar---------------*/
        tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*------------Drawer 옆에 나오는 메뉴 ------------*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.drawer);
        View headerView = navigationView.getHeaderView(0);
        usernameNav = (TextView)headerView.findViewById(R.id.header_username);
        usernameNav.setText(username);
        userimageBtn = (ImageView)headerView.findViewById(R.id.header_userimage);

        userimageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddpostActivity.this, "Change image in mainpage", Toast.LENGTH_SHORT);
            }
        });
        StorageReference islandRefUser = mStorageRefUser.child(username);
        islandRefUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                userimageBtn.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        Menu menu = navigationView.getMenu();
        fullnameNav = menu.findItem(R.id.menu_fullname);

        fullnameNav.setTitle(fullname);
        birthdayNav = menu.findItem(R.id.menu_birthday);
        birthdayNav.setTitle(birthday);
        emailNav = menu.findItem(R.id.menu_email);
        emailNav.setTitle(email);


    }
    /*-----------파이어베이스에 저장-------------*/
    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        if(add){
            ContentInfo post = new ContentInfo(username, title, content, tags, check);
            postValues = post.toMap();
        }
        childUpdates.put("/content_list/" + time, postValues);//id_list/ID 에 동시에 postvalue넣기
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET() {
        contentET.setText("");
        tagsET.setText("");
        checkBox.setChecked(false);
        title = "";
        content = "";
        tags = "";
        check = "";

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backIntent = new Intent(this,MainActivity.class);
            backIntent.putExtra("username_Intent", username);
            backIntent.putExtra("fullname_Intent", fullname);
            backIntent.putExtra("birthday_Intent", birthday);
            backIntent.putExtra("email_Intent", email);
            startActivity(backIntent);
        }
    }
    @Override
    public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            currentImageUri = data.getData();
            imageBtn.setImageURI(currentImageUri);
            StorageReference ref = mStorageRefContent.child("/"+username+"/" + title);
            UploadTask uploadTask = ref.putFile(currentImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddpostActivity.this, "Upload Success!", Toast.LENGTH_LONG);
                }
            });
        }
    }
}

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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    Uri currentImageUri;
    Toolbar tb;
    DrawerLayout drawerLayout;
    TabLayout tabLayout;
    NavigationView navigationView;
    ViewPager viewPager;
    TabAdapter tabAdapter;
    ImageButton postbtn;
    String username ="",fullname ="", birthday = "", email = "";
    TextView usernameNav;
    ImageView userimageBtn;
    MenuItem fullnameNav, birthdayNav, emailNav;
    Intent postIntent, backIntent, intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        mStorageRef = FirebaseStorage.getInstance().getReference("UserImages/");
        intent = getIntent();

        username = intent.getStringExtra("username_Intent");
        fullname = intent.getStringExtra("fullname_Intent");
        birthday = intent.getStringExtra("birthday_Intent");
        email = intent.getStringExtra("email_Intent");

        postbtn = (ImageButton)findViewById(R.id.addpost);
        postIntent = new Intent(this, AddpostActivity.class);
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postIntent.putExtra("username_Intent", username);
                postIntent.putExtra("fullname_Intent", fullname);
                postIntent.putExtra("email_Intent", email);
                postIntent.putExtra("birthday_Intent", birthday);
                startActivity(postIntent);
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

        /*Header*/
        usernameNav = (TextView)headerView.findViewById(R.id.header_username);
        usernameNav.setText(username);
        userimageBtn = (ImageView)headerView.findViewById(R.id.header_userimage);
        userimageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
        Log.d("Mainuser", username);
        StorageReference islandRef = mStorageRef.child(username);
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
        /*Menu*/
        Menu menu = navigationView.getMenu();
        fullnameNav = menu.findItem(R.id.menu_fullname);
        fullnameNav.setTitle(fullname);
        birthdayNav = menu.findItem(R.id.menu_birthday);
        birthdayNav.setTitle(birthday);
        emailNav = menu.findItem(R.id.menu_email);
        emailNav.setTitle(email);


        /*----------personal private 프래그먼트 나누는 탭--------------*/
        tabLayout = (TabLayout)findViewById(R.id.main_tab);
        tabLayout.addTab(tabLayout.newTab().setText("PERSONAL"));
        tabLayout.addTab(tabLayout.newTab().setText("PUBLIC"));

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        tabAdapter = new TabAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            backIntent = new Intent(MainActivity.this, LoginActivity.class);
            backIntent.putExtra("username_Intent", username);
            startActivity(backIntent);
        }
    }
    @Override
    public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            currentImageUri = data.getData();
            userimageBtn.setImageURI(currentImageUri);
            StorageReference ref = mStorageRef.child(username);
            UploadTask uploadTask = ref.putFile(currentImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Upload Success!", Toast.LENGTH_LONG);
                }
            });
        }
    }
}

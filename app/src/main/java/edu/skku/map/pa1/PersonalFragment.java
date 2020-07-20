package edu.skku.map.pa1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.EventListener;


public class PersonalFragment extends Fragment {

    private StorageReference mStorageRefContent;
    private StorageReference mStorageRefUser;
    private static final long ONE_MEGABYTE = 1024 * 1024;
    private DatabaseReference mPostReference;
    private View personalFragmentView;
    ArrayList<ContentItem> data;
    ContentAdapter adapter;
    ListView listview;
    String username;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("username_Intent");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mStorageRefContent = FirebaseStorage.getInstance().getReference("ContentImages");
        mStorageRefUser = FirebaseStorage.getInstance().getReference("UserImages");

        personalFragmentView = inflater.inflate(R.layout.fragment_personal, container, false);
        data = new ArrayList<ContentItem>();
        listview = (ListView) personalFragmentView.findViewById(R.id.listView_personal);
        getFirebaseDatabase();
        adapter = new ContentAdapter(getActivity(), data);
        listview.setAdapter(adapter);

        return personalFragmentView;
    }

    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String username_firebase = postSnapshot.getKey();
                    Log.d("firebase_username", username_firebase);
                    ContentInfo get = postSnapshot.getValue(ContentInfo.class);
                    ContentItem content = new ContentItem(get.username, get.contentimage, get.content, get.tags, get.check);
                    if(get.check.equals("private") && get.username.equals(username)) {
                        Log.d("Private", "info" + username + get.username + get.contentimage + get.content + get.tags + get.check);
                        data.add(content);
                        }
                    }

                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("content_list").addValueEventListener(postListener);
    }

    public void onFragmentInteraction(Uri uri) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

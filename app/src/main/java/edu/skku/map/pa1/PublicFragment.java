package edu.skku.map.pa1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PublicFragment extends Fragment {
    private DatabaseReference mPostReference;
    private View publicFragmentView;
    ArrayList<ContentItem> data;
    ContentAdapter adapter;
    ListView listview;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPostReference = FirebaseDatabase.getInstance().getReference();
        publicFragmentView = inflater.inflate(R.layout.fragment_personal, container, false);
        data = new ArrayList<ContentItem>();
        listview = (ListView) publicFragmentView.findViewById(R.id.listView_personal);

        adapter = new ContentAdapter(this.getContext(), data);
        listview.setAdapter(adapter);
        getFirebaseDatabase();
        return publicFragmentView;
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
                    if(get.check.equals("public")) {
                        Log.d("public", "info" + get.username + get.contentimage + get.content + get.tags + get.check);
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
}

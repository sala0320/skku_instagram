package edu.skku.map.pa1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.view.View.GONE;

public class ContentAdapter extends BaseAdapter {
    private StorageReference mStorageRefContent;
    private StorageReference mStorageRefUser;
    private StorageReference islandRefUser;
    private static final long ONE_MEGABYTE = 1024 * 1024;
    LayoutInflater inflater;
    private ArrayList<ContentItem> items;
    ImageView content_image, user_image;
    TextView content_content, content_tags, username;;
    public ContentAdapter (Context context, ArrayList<ContentItem> content) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = content;
    }


    @Override
    public int getCount() {
        Log.d("content_num",Integer.toString(items.size()));
        return items.size();
    }

    @Override
    public ContentItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.content_layout, viewGroup, false);
        }
        ContentItem item = items.get(i);

        final View contentview = view;
        //user_image = (ImageView) view.findViewById(R.id.content_userimage);
        username = (TextView) view.findViewById(R.id.content_username);
        //content_image = (ImageView) view.findViewById(R.id.content_image);
        content_content = (TextView) view.findViewById(R.id.content_content);
        content_tags = (TextView) view.findViewById(R.id.content_tags);

        username.setText(item.getUsername());

        mStorageRefUser = FirebaseStorage.getInstance().getReference("UserImages");
        islandRefUser = mStorageRefUser.child("/" + item.getUsername());
        islandRefUser.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                user_image = (ImageView)contentview.findViewById(R.id.content_userimage);
                user_image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        if(!item.getContentimage().equals("") ) {
            mStorageRefContent = FirebaseStorage.getInstance().getReference("ContentImages");
            StorageReference islandRefContent = mStorageRefContent.child("/" + item.getUsername() + "/" + item.getContentimage());
            islandRefContent.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    content_image = (ImageView) contentview.findViewById(R.id.content_image);
                    content_image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else{
            content_image = (ImageView) contentview.findViewById(R.id.content_image);
            content_image.setVisibility(GONE);
        }
        if(item.getTags().equals("")){
            content_tags.setText("");
        }
        else{
            String[] splitText = item.getTags().split(" ");
            String tags = "";
            for (int j = 0; j < splitText.length; j++) {
                tags += ("#" + splitText[j] + " ");
            }
            content_tags.setText(tags);
        }
        content_content.setText(item.getContent());

        return view;
    }
}

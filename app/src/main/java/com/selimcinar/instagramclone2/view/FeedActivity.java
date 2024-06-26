package com.selimcinar.instagramclone2.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.selimcinar.instagramclone2.R;
import com.selimcinar.instagramclone2.adapter.PostAdapter;
import com.selimcinar.instagramclone2.databinding.ActivityFeedBinding;
import com.selimcinar.instagramclone2.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private ActivityFeedBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    ArrayList<Post> postArrayList;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        postArrayList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);
    }
    private  void  getData(){
//DocumentReference documentReference = firebaseFirestore.collection("Posts").document("fd");
        //CollectionReference documentReference = firebaseFirestore.collection("Posts").document("fd");

        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if (value !=null){
                    for (DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();
                        //casting
                        String userEmail =(String) data.get("useremail");
                        String comment = (String) data.get("comment");
                        String downloadUrl = (String) data.get("downloadurl");

                        Post post = new Post(userEmail,comment,downloadUrl);
                        postArrayList.add(post);

                    }
                    postAdapter.notifyDataSetChanged();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menüyü bağlama
        MenuInflater menuInflater = getMenuInflater(); //xmlle menüyü bağlamaya yarayan method
        menuInflater.inflate(R.menu.options_menu,menu); //menu parametresine var olan xml menüyü bağlar.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Menüde tıklanma durumunda ne olacak

        if (item.getItemId()==R.id.add_post){
            //Upload activity güncelleme aktivitesine geçiş yap
            Intent intentToUpload = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpload);
        }
        else if (item.getItemId()==R.id.signout){
            //Signout çıkış yap
            mAuth.signOut();//hesaptan çıkış yap
            Intent intentToMain = new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intentToMain);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
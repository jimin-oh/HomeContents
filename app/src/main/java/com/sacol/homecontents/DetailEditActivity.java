package com.sacol.homecontents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailEditActivity extends AppCompatActivity {
    private ImageView cancel;
    private TextView completion;
    private TextView detail_edit_contents;
    private TextView detail_edit_title;
    private DatabaseReference databaseReference;
    private  String date;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_edit);
        init();
        setUp();
    }

    private void init() {
        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
        cancel = findViewById(R.id.detail_edit_cancel);
        completion = findViewById(R.id.detail_edit_completion);
        detail_edit_contents = findViewById(R.id.detail_edit_contents);
        detail_edit_title = findViewById(R.id.detail_edit_title);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    private void setUp() {
        cancel.setOnClickListener(goBackPage);
        completion.setOnClickListener(completionEdit);
        databaseReference.child("contents").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                detail_edit_contents.setText(snapshot.child("content").getValue().toString());
                detail_edit_title.setText(snapshot.child("title").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    View.OnClickListener goBackPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            Toast.makeText(getApplicationContext(), "수정되지 않았습니다", Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener completionEdit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDatabase.child("contents").child(date).child("content").setValue(detail_edit_contents.getText().toString());
            mDatabase.child("contents").child(date).child("title").setValue(detail_edit_title.getText().toString());
            finish();
            Toast.makeText(getApplicationContext(), "수정이 완료되었습니다", Toast.LENGTH_SHORT).show();
        }
    };
}
package kr.hs.emirim.homecontents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.DetailAdapter;

public class DetailActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private DetailAdapter detailAdapter;
    private List<Model> models;

    private ImageView back;
    private ImageView save;
    private boolean flag;
    private String date;

    private TextView detail_title;
    private TextView detail_contents;
    private DatabaseReference databaseReference;
    private TextView detail_nickname;
    private String uid;
    private DatabaseReference mDatabase;
    private String key;
    private ImageView detail_delete;
    private ImageView detail_edit;
    private ImageView detail_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
        setUp();
        initDatabase();

    }

    public void init() {
        Intent intent = getIntent();
        date = intent.getExtras().getString("date");
        back = findViewById(R.id.detail_back);
        save = findViewById(R.id.detail_storage);
        detail_user = findViewById(R.id.detail_user);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        detail_contents = findViewById(R.id.detail_contents);
        detail_title = findViewById(R.id.detail_title);
        detail_nickname = findViewById(R.id.detail_nickname);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getUid();
        detail_delete = findViewById(R.id.detail_delete);
        detail_edit = findViewById(R.id.detail_edit);

        viewPager = findViewById(R.id.viewPager);
        models = new ArrayList<>();
        detailAdapter = new DetailAdapter(models,this);

    }

    public void setUp() {
        viewPager.setAdapter(detailAdapter);
        viewPager.setPadding(0, 0, 0, 0);
        back.setOnClickListener(goBackPage);
        save.setOnClickListener(saveContent);
        detail_delete.setOnClickListener(delete);
        detail_edit.setOnClickListener(goEditPage);
    }

    private void showToast(String str){
        Toast.makeText(getApplicationContext(),str, Toast.LENGTH_LONG).show();
    }

    private void initDatabase() {
        databaseReference.child("contents").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                models.clear();
                for (DataSnapshot homecontent : snapshot.child("ImgLink").getChildren()) {
                   models.add(new Model(homecontent.getValue().toString(),homecontent.getKey()));
                }
                detailAdapter.notifyDataSetChanged();
                detail_contents.setText(snapshot.child("content").getValue().toString());
                detail_title.setText(snapshot.child("title").getValue().toString());

                if (!uid.equals(snapshot.child("uid").getValue().toString())) {
                    detail_delete.setVisibility(View.GONE);
                    detail_edit.setVisibility(View.GONE);
                }
                databaseReference.child("users").child(snapshot.child("uid").getValue().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        detail_nickname.setText(snapshot.child("name").getValue().toString());
                        if (snapshot.child("profileImg").getValue()!=null){
                            Glide
                                    .with(getApplicationContext())
                                    .load(snapshot.child("profileImg").getValue())
                                    .into(detail_user);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child("save").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot homecontent : snapshot.getChildren()) {
                    if (date.equals(homecontent.getValue().toString())) {
                        save.setImageResource(R.drawable.storage_filled_icon);
                        flag = true;
                        key = homecontent.getKey();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    View.OnClickListener goBackPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DetailActivity.this.finish();
        }
    };

    View.OnClickListener saveContent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!flag) {
                save.setImageResource(R.drawable.storage_filled_icon);
                mDatabase.child("save").child(uid).push().setValue(date);
                flag = true;

            } else {
                save.setImageResource(R.drawable.storage_icon);
                mDatabase.child("save").child(uid).child(key).removeValue();
                flag = false;
            }
        }
    };

    View.OnClickListener delete = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String message = "삭제 하시겠습니까?";
            new AlertDialog.Builder(DetailActivity.this)
                    .setMessage(message)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDatabase.child("contents").child(date).removeValue();
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "삭제하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    };

    View.OnClickListener goEditPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(DetailActivity.this, DetailEditActivity.class);
            intent.putExtra("date",date);
            startActivity(intent);
        }
    };
}
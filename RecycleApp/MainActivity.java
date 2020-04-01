package com.example.recycleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;
public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    TextView fullName,items;
    FirebaseFirestore fstore;
    String userId;
    TextView itemUnrecognize;
    TextView label;
    ImageView cardboard;
    ImageView can;
    ImageView plastic;
    ImageView glass;
    TextView cancel;
    TextView state;
    TextView textView5;
    FirebaseDatabase mDatabase;
    DatabaseReference mRefernceLabel;
    DatabaseReference mRefernceState;
    DatabaseReference mRefernceItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        mRefernceLabel = mDatabase.getReference("predict");
        mRefernceState = mDatabase.getReference("state");
        mRefernceItem = mDatabase.getReference("item");
        setContentView(R.layout.activity_main);
        fullName = findViewById(R.id.FullName);
        items = findViewById(R.id.Items);
        label = findViewById(R.id.label);
        itemUnrecognize = findViewById(R.id.textView4);
        fAuth = FirebaseAuth.getInstance();
        cardboard = findViewById(R.id.cardboard);
        fstore = FirebaseFirestore.getInstance();
        glass = findViewById(R.id.glass);
        can = findViewById(R.id.can);
        cancel = findViewById(R.id.cancel);
        textView5= findViewById(R.id.textView5);
        plastic = findViewById(R.id.plastic);
        state = findViewById(R.id.state);
        userId = fAuth.getCurrentUser().getUid();
        readLabel();
        readState();
        readItem();

        DocumentReference documentReference = fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                fullName.setText(documentSnapshot.getString("fName"));
                items.setText(String.valueOf(documentSnapshot.getLong("items")));
            }
        });
        itemUnrecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                can.setVisibility(View.VISIBLE);
                cardboard.setVisibility(View.VISIBLE);
                glass.setVisibility(View.VISIBLE);
                plastic.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                can.setVisibility(View.INVISIBLE);
                cardboard.setVisibility(View.INVISIBLE);
                glass.setVisibility(View.INVISIBLE);
                plastic.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);

            }
        });
        cardboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fstore.collection("users").document(userId);
                documentReference.update("items", FieldValue.increment(1));
                documentReference.update("cardboard", FieldValue.increment(1));
                mRefernceLabel.child("label").setValue("cardboard");
                mRefernceState.child("state").setValue("before");
                itemUnrecognize.setVisibility(View.INVISIBLE);
                can.setVisibility(View.INVISIBLE);
                cardboard.setVisibility(View.INVISIBLE);
                glass.setVisibility(View.INVISIBLE);
                plastic.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);

            }
        });
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fstore.collection("users").document(userId);
                documentReference.update("items", FieldValue.increment(1));
                documentReference.update("can", FieldValue.increment(1));
                mRefernceLabel.child("label").setValue("can");
                mRefernceState.child("state").setValue("before");
                itemUnrecognize.setVisibility(View.INVISIBLE);
                can.setVisibility(View.INVISIBLE);
                cardboard.setVisibility(View.INVISIBLE);
                glass.setVisibility(View.INVISIBLE);
                plastic.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
            }
        });
        glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fstore.collection("users").document(userId);
                documentReference.update("items", FieldValue.increment(1));
                documentReference.update("glass", FieldValue.increment(1));
                mRefernceLabel.child("label").setValue("glass");
                mRefernceState.child("state").setValue("before");
                itemUnrecognize.setVisibility(View.INVISIBLE);
                can.setVisibility(View.INVISIBLE);
                cardboard.setVisibility(View.INVISIBLE);
                glass.setVisibility(View.INVISIBLE);
                plastic.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
            }
        });
        plastic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fstore.collection("users").document(userId);
                documentReference.update("items", FieldValue.increment(1));
                documentReference.update("plastic", FieldValue.increment(1));
                mRefernceLabel.child("label").setValue("plastic");
                mRefernceState.child("state").setValue("before");
                itemUnrecognize.setVisibility(View.INVISIBLE);
                can.setVisibility(View.INVISIBLE);
                cardboard.setVisibility(View.INVISIBLE);
                glass.setVisibility(View.INVISIBLE);
                plastic.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
            }
        });
    }
    public void readLabel(){
        mRefernceLabel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("label").getValue(String.class);
                label.setText(value);
                if(state.getText().equals("after")&& label.equals("unknown"))
                {
                    itemUnrecognize.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void readState(){
        mRefernceState.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("state").getValue(String.class);
                state.setText(value);
                if(value.equals("after")&& label.getText().equals("unknown"))
                {
                    itemUnrecognize.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void readItem(){
        mRefernceItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DocumentReference documentReference = fstore.collection("users").document(userId);
                String value = dataSnapshot.child("item").getValue(String.class);
//                state.setText(value);
                if(value.equals("add"))
                {
                    documentReference.update("items", FieldValue.increment(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}

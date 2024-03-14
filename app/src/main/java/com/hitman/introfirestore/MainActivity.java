package com.hitman.introfirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText titleText;
    private EditText thoughtText;
    private Button saveBtn;
    private TextView titleTextView;
    private TextView thoughtTextView;
    private Button showBtn;
    private Button updateBtn;
    private Button deleteBtn;
    // Firebase Instance
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    //Firebase Document Reference
    private final DocumentReference  journalRef = db.collection("Journals").document("First Thought");
    private final CollectionReference collectionReference = db.collection("Journals");
    // KEY Name as variables
    public static final String KEY_TITLE="title";
    public static final String KEY_THOUGHT="thought";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleText=findViewById(R.id.editTextTitle);
        thoughtText=findViewById(R.id.editTextThoughts);
        saveBtn=findViewById(R.id.saveButton);
        titleTextView=findViewById(R.id.rec_title);
        thoughtTextView=findViewById(R.id.rec_thought);
        showBtn=findViewById(R.id.show_button);
        updateBtn=findViewById(R.id.update_button);
        deleteBtn=findViewById(R.id.button_delete);
        // Saving data into the firebase
        saveBtn.setOnClickListener(v->{
            addThought();
//            String title= titleText.getText().toString().trim();
//            String thought= thoughtText.getText().toString().trim();
//
////            Map<String, Object> data= new HashMap<>();
////            data.put(KEY_TITLE,title);
////            data.put(KEY_THOUGHT,thought);
//            // Instead of this we will use pojo class
//            Journal data= new Journal();
//            data.setTitle(title);
//            data.setThought(thought);
//
//            db.collection("Journals")
//                    .document("First Thought")
//                    .set(data)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
//                            Log.d("MainActDB", "onSuccess: Successfully Saved");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("MainActDB",e.toString());
//                        }
//                    });
        });

        // Show Data
        showBtn.setOnClickListener(v->{
            showThought();
//            journalRef.get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            // Checking if the document is exist or not
//                            if(documentSnapshot.exists()){
//
////                                String title=documentSnapshot.getString(KEY_TITLE);
////                                String thought=documentSnapshot.getString(KEY_THOUGHT);
//                                // Let's use the Journal class instead of this
//                                Journal journal = documentSnapshot.toObject(Journal.class);
//
//                                if (journal != null) {
//                                    titleTextView.setText(journal.getTitle().trim());
//                                    thoughtTextView.setText(journal.getThought().trim());
//                                }
//
//                            }
//                            else{
//                                Toast.makeText(MainActivity.this,"No Data is present",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("MainActDB", "onFailure: "+e.toString());
//                        }
//                    });
        });
        // Update
        updateBtn.setOnClickListener(v->{
            String title=titleText.getText().toString().trim();
            String thought=thoughtText.getText().toString().trim();
            Map<String,Object> data=new HashMap<>();

            // We can update data by invoking that update() method
            journalRef.update(KEY_THOUGHT,thought)
                    .addOnSuccessListener(unused -> Toast.makeText(MainActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.d("MainActDB", "onFailure: Successfully Updated data"));
        });
        // Delete data from the firestore
        deleteBtn.setOnClickListener(v->{
            // By this way we can delete just a field or key from DB
                //journalRef.update(KEY_THOUGHT, FieldValue.delete());
            // By this way we can delete all
            journalRef.delete();
        });
    }

    public void addThought()
    {
        String title= titleText.getText().toString().trim();
        String thought= thoughtText.getText().toString().trim();

        Journal journal = new Journal(title,thought);
        collectionReference.add(journal)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                     Toast.makeText(MainActivity.this,"Failure",Toast.LENGTH_SHORT).show();
                });
    }
    public void showThought()
    {
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                        {
                            Log.d("showThought", "onSuccess: "+documentSnapshot.getString(KEY_TITLE));
                        }
                    }
                })
                .addOnFailureListener(e -> {

                });
    }


    /*
        Inside on-start we will add the snapshot because it's fine to add it inside the on create but it's more convenient to add it inside the on start

     */
    @Override
    protected void onStart() {
        super.onStart();
        journalRef.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                // Checking is there any kind of error or not
                if(error!=null)
                {
                    Toast.makeText(MainActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                // checking if the snapshot is exist or not
                if(value!=null&&value.exists()){
//                    String title=value.getString(KEY_TITLE);
//                    String thought=value.getString(KEY_THOUGHT);
                    // Instead of this two variable lets use POJO
                    Journal journal = value.toObject(Journal.class);

                    if(journal!=null) {
                        titleTextView.setText(journal.getTitle().trim());
                        thoughtTextView.setText(journal.getThought().trim());
                    }
                }
                else {
                    thoughtTextView.setText("");
                    titleTextView.setText("");
                }
            }
        });
    }
}
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    // Firebase Instance
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();
    //Firebase Document Reference
    private DocumentReference  journalRef = db.collection("Journals").document("First Thought");
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

        // Saving data into the firebase
        saveBtn.setOnClickListener(v->{
            String title= titleText.getText().toString().trim();
            String thought= thoughtText.getText().toString().trim();

            Map<String, Object> data= new HashMap<>();
            data.put(KEY_TITLE,title);
            data.put(KEY_THOUGHT,thought);

            db.collection("Journals")
                    .document("First Thought")
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                            Log.d("MainActDB", "onSuccess: Successfully Saved");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MainActDB",e.toString());
                        }
                    });
        });

        // Show Data
        showBtn.setOnClickListener(v->{
            journalRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // Checking if the document is exist or not
                            if(documentSnapshot.exists()){
                                String title=documentSnapshot.getString(KEY_TITLE);
                                String thought=documentSnapshot.getString(KEY_THOUGHT);

                                titleTextView.setText(title);
                                thoughtTextView.setText(thought);
                            }
                            else{
                                Toast.makeText(MainActivity.this,"No Data is present",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MainActDB", "onFailure: "+e.toString());
                        }
                    });
        });
        updateBtn.setOnClickListener(v->{
            String title=titleText.getText().toString().trim();
            String thought=thoughtText.getText().toString().trim();
            Map<String,Object> data=new HashMap<>();

            // We can update data by invoking that update() method
            journalRef.update(KEY_THOUGHT,thought)
                    .addOnSuccessListener(unused -> Toast.makeText(MainActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.d("MainActDB", "onFailure: Successfully Updated data"));
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
                    String title=value.getString(KEY_TITLE);
                    String thought=value.getString(KEY_THOUGHT);

                    titleTextView.setText(title);
                    thoughtTextView.setText(thought);
                }
            }
        });
    }
}
package com.hitman.introfirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText titleText;
    private EditText thoughtText;
    private Button saveBtn;
    // Firebase Instance
    private final FirebaseFirestore db= FirebaseFirestore.getInstance();

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

    }
}
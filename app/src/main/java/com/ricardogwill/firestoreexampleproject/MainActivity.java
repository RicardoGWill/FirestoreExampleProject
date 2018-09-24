package com.ricardogwill.firestoreexampleproject;

import android.content.Context;
import android.media.audiofx.AudioEffect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

// This project is based off the tutorial here: https://www.youtube.com/watch?v=MILE4PVx1kE&index=2&list=PLrnPJCHvNZuDrSqu-dKdDi3Q6nM-VUyxD
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";

    private EditText titleEditText, descriptionEditText;
    private TextView dataTextView;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    // "Notebook" is the Firestore NoSQL "Collection", and "My First Note" is the "Document".
    // Note that "firebaseFirestore.collection("Notebook/My First Note");" is equal to the right half below.
    private DocumentReference documentReference = firebaseFirestore.collection("Notebook").document("My First Note");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.title_editText);
        descriptionEditText = findViewById(R.id.description_editText);
        dataTextView = findViewById(R.id.data_textView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Using "this" before the new EventListener DETATCHES the listener at the appropriate moment.
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Error while loading.", Toast.LENGTH_SHORT).show();
                    // Just type "logd" for the basic template for the line below.
                    Log.d(TAG, e.toString());
                    return;
                }
                // This "if" statement is nearly the same as in the "loadNote" method below.
                if (documentSnapshot.exists()) {
                    Note note = documentSnapshot.toObject(Note.class);

                    String title = note.getTitle();
                    String description = note.getDescription();

                    dataTextView.setText("Title: " + title + "\n" + "Description: " + description);
                } else {
                    dataTextView.setText("");
                }
            }
        });
    }

    // "OnClick" XML is used instead of an OnClickListener.
    public void saveNote(View view) {
        String titleString = titleEditText.getText().toString();
        String descriptionString = descriptionEditText.getText().toString();

        // Note: The following 3 lines were necessary UNTIL the "Note" Class was created.
        // Note cont'd: Instead, the line starting with "Note note = new Note()" replaces them.
        // Map<String, Object> note = new HashMap<>();
        // note.put(KEY_TITLE, titleString);
        // note.put(KEY_DESCRIPTION, descriptionString);

        Note note = new Note(titleString, descriptionString);

        documentReference.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note saved.", Toast.LENGTH_SHORT).show();
                        removeKeyboardAndMakeEditTextBlank();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void loadNote(View view) {
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Note note = documentSnapshot.toObject(Note.class);

                            String title = note.getTitle();
                            String description = note.getDescription();

                            dataTextView.setText("Title: " + title + "\n" + "Description: " + description);
                        } else {
                            Toast.makeText(MainActivity.this, "Document does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void deleteDescription(View view) {
        // Note: The following 3 lines of commented-out code can be replaced by the 1 actual line of code below it.
        // Map<String, Object> note = new HashMap<>();
        // note.put(KEY_DESCRIPTION, FieldValue.delete());
        // documentReference.update(note);

        documentReference.update(KEY_DESCRIPTION, FieldValue.delete());
        // It is possible to put an OnSuccessListener and OnFailureListener after the above line (before the semicolon).
    }

    public void deleteNote(View view) {
        documentReference.delete();
        // It is possible to put an OnSuccessListener and OnFailureListener after the above line (before the semicolon).
    }

    public void updateDescription(View view) {
        String description = descriptionEditText.getText().toString();

        // Note: The below notes show ONE way to do this, if we do "documentReference.set" instead of ".update".
        // Note: Adding "SetOptions.merge()" makes it so that updating the description does not make the title "null",
        // Note cont'd: but rather leaves the title as it is.

        // Map<String, Object> note = new HashMap<>();
        // note.put(KEY_DESCRIPTION, description);
        // documentReference.set(note, SetOptions.merge());

        // Note: "documentReference.update" just updates the description but doesn't delete the title.
        // Note: Unlike the commented out code above, ".update" does not do anything if no document exists already on Firestore.
        documentReference.update(KEY_DESCRIPTION, description);
        removeKeyboardAndMakeEditTextBlank();
    }

    public void removeKeyboardAndMakeEditTextBlank() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(descriptionEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        titleEditText.setText("");
        descriptionEditText.setText("");
    }



}

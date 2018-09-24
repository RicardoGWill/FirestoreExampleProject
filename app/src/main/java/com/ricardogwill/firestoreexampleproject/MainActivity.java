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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

// This project is based off the tutorial here: https://www.youtube.com/watch?v=MILE4PVx1kE&index=2&list=PLrnPJCHvNZuDrSqu-dKdDi3Q6nM-VUyxD
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";

    private EditText titleEditText, descriptionEditText, priorityEditText;
    private TextView dataTextView;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Notebook");
    // "Notebook" is the Firestore NoSQL "Collection", and "My First Note" is the "Document".
    // Note that "firebaseFirestore.collection("Notebook/My First Note");" is equal to the right half below.
    private DocumentReference documentReference = firebaseFirestore.collection("Notebook").document("My First Note");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.title_editText);
        descriptionEditText = findViewById(R.id.description_editText);
        priorityEditText = findViewById(R.id.priority_editText);
        dataTextView = findViewById(R.id.data_textView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Using "this" before "new EventListener" detatches the listener at the appropriate time to save memory.
        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    // This sets the "documentID" (see "Note" class).
                    note.setDocumentID(documentSnapshot.getId());

                    String documentID = note.getDocumentID();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int priority = note.getPriority();

                    data += "ID: " + documentID + "\nTitle: " + title
                            + "\nDescription: " + description + "\nPriority: " + priority + "\n\n";
                }

                dataTextView.setText(data);
            }
        });
    }

    // "OnClick" XML is used instead of an OnClickListener.
    public void addNote(View view) {
        String titleString = titleEditText.getText().toString();
        String descriptionString = descriptionEditText.getText().toString();

        if (priorityEditText.length() == 0) {
            priorityEditText.setText("0");
        }

        int priorityInt = Integer.parseInt(priorityEditText.getText().toString());

        Note note = new Note(titleString, descriptionString, priorityInt);

        collectionReference.add(note);  // It is possible to add an OnSuccessListener and OnFailureListener before the semicolon.

        removeKeyboardAndMakeEditTextBlank();
    }

    public void loadNotes(View view) {
        collectionReference.whereGreaterThanOrEqualTo("priority", 2)
                // ".orderBy" shows the notes (by "priority", then by "title").
                // The "Load Notes" button doesn't work with two different orders (e.g. "orderBy("priority").orderBy("title")", so after I press it, I must go to Logcat, see the message, and go to the link to set up a "Composite" Index:
                // 09-24 16:51:25.524 4162-4162/com.ricardogwill.firestoreexampleproject D/MainActivity: com.google.firebase.firestore.FirebaseFirestoreException: FAILED_PRECONDITION: The query requires an index. You can create it here:
                // https://console.firebase.google.com/project/firestoreexampleproject-c2f51/database/firestore/indexes?create_index=EghOb3RlYm9vaxoMCghwcmlvcml0eRACGgkKBXRpdGxlEAIaDAoIX19uYW1lX18QAg
                .orderBy("priority").orderBy("title")
//                .limit(3) // ".limit" limits the number of results to the number defined.
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // The following parts are similar to those in "OnStart".  With "OnStart", the notes show up immediately.
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            // This sets the "documentID" (see "Note" class).
                            note.setDocumentID(documentSnapshot.getId());

                            String documentID = note.getDocumentID();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();

                            data += "ID: " + documentID + "\nTitle: " + title
                                    + "\nDescription: " + description + "\nPriority: " + priority + "\n\n";
                        }

                        dataTextView.setText(data);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
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

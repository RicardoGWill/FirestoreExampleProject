package com.ricardogwill.firestoreexampleproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

// This project is based off the tutorial here: https://www.youtube.com/watch?v=MILE4PVx1kE&index=2&list=PLrnPJCHvNZuDrSqu-dKdDi3Q6nM-VUyxD
public class MainActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, priorityEditText, tagsEditText;
    private TextView dataTextView;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.title_editText);
        descriptionEditText = findViewById(R.id.description_editText);
        priorityEditText = findViewById(R.id.priority_editText);
        tagsEditText = findViewById(R.id.tags_editText);
        dataTextView = findViewById(R.id.data_textView);

        updateNestedValue();

    }

    // "OnClick" XML is used instead of an OnClickListener.
    public void addNote(View view) {
        String titleString = titleEditText.getText().toString();
        String descriptionString = descriptionEditText.getText().toString();

        if (priorityEditText.length() == 0) {
            priorityEditText.setText("0");
        }

        int priorityInt = Integer.parseInt(priorityEditText.getText().toString());

        String tagInput = tagsEditText.getText().toString();
        // "\\s*,\\s*" removes any spaces before or after the text.
        String[] tagArray = tagInput.split("\\s*,\\s*");
        Map<String, Boolean> tags = new HashMap<>();

        for (String tag : tagArray) {
            tags.put(tag, true);
        }

        Note note = new Note(titleString, descriptionString, priorityInt, tags);

        collectionReference.add(note);  // It is possible to add an OnSuccessListener and OnFailureListener before the semicolon.

        removeKeyboardAndMakeEditTextBlank();
    }

    public void loadNotes(View view) {
        collectionReference.whereEqualTo("tags.tag1", true).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentID(documentSnapshot.getId());

                            String documentID = note.getDocumentID();

                            data += "ID: " + documentID;

                            for (String tag : note.getTags().keySet()) {
                                data += "\n-" + tag;
                            }

                            data += "\n\n";
                            }

                            dataTextView.setText(data);
                    }
                });
    }

    // Note that you have to go to the "Firestore Database" (from the Firebase Console) to see how this app works.
    public void updateNestedValue() {
        collectionReference.document("TQmPTOtmSJpMYqUJGwYP")
                .update("tags.tag1.nested1.nested2", true);
    }


    public void removeKeyboardAndMakeEditTextBlank() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(descriptionEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        titleEditText.setText("");
        descriptionEditText.setText("");
        priorityEditText.setText("");
        tagsEditText.setText("");
    }

}

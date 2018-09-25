package com.ricardogwill.firestoreexampleproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

// This project is based off the tutorial here: https://www.youtube.com/watch?v=MILE4PVx1kE&index=2&list=PLrnPJCHvNZuDrSqu-dKdDi3Q6nM-VUyxD
public class MainActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, priorityEditText;
    private TextView dataTextView;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Notebook");

    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEditText = findViewById(R.id.title_editText);
        descriptionEditText = findViewById(R.id.description_editText);
        priorityEditText = findViewById(R.id.priority_editText);
        dataTextView = findViewById(R.id.data_textView);

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
        Query query;
        if (lastResult == null) {
            query = collectionReference.orderBy("priority")
                    .limit(3);
        } else {
            query = collectionReference.orderBy("priority")
                    .startAfter(lastResult)
                    .limit(3);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentID(documentSnapshot.getId());

                            String documentID = note.getDocumentID();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();

                            data += "ID: " + documentID + "\nTitle: " + title
                                    + "\nDescription: " + description + "\nPriority: " + priority + "\n\n";
                        }
                        if (queryDocumentSnapshots.size() > 0) {
                            data += "__________\n\n";
                            dataTextView.append(data);

                            lastResult = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                });
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
    }

}

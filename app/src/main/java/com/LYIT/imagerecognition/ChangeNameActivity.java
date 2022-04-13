package com.LYIT.imagerecognition;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class ChangeNameActivity extends AppCompatActivity {

    EditText mChangeName, mCurPassword;
    Button mBtnChangeName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_name);

        mChangeName = findViewById(R.id.changeName);
        mCurPassword = findViewById(R.id.curPassword);
        mBtnChangeName = findViewById(R.id.btnChangeName);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = fAuth.getCurrentUser().getUid();

        mBtnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String changeName = mChangeName.getText().toString().trim();
                String curPassword = mCurPassword.getText().toString().trim();

                if(TextUtils.isEmpty(changeName)){
                    mChangeName.setError("Please Enter NEW Name");
                    return;
                }

                if(TextUtils.isEmpty(curPassword)){
                    mCurPassword.setError("Please Enter Current Password");
                    return;
                }

                if(user != null){
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), curPassword);
                    user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fStore.collection("users").document(userId).update("name", changeName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChangeNameActivity.this, "Name Changed", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                                    }else{
                                        Toast.makeText(ChangeNameActivity.this, "Error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mCurPassword.setError("Password NOT Match");
                            return;
                        }
                    });
                }
            }
        });
    }
}

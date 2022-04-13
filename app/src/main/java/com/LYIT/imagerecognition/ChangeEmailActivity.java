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
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangeEmailActivity extends AppCompatActivity {

    EditText mNewEmail, mCurrentPassword;
    Button mBtnChangeEmail;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email);

        mNewEmail = findViewById(R.id.newEmail);
        mCurrentPassword = findViewById(R.id.currentPassword);
        mBtnChangeEmail = findViewById(R.id.btnChangeEmail);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mBtnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String newEmail = mNewEmail.getText().toString().trim();
                String currentPassword = mCurrentPassword.getText().toString().trim();

                if(TextUtils.isEmpty(newEmail)){
                    mNewEmail.setError("Please Enter NEW Email");
                    return;
                }

                if(TextUtils.isEmpty(currentPassword)){
                    mCurrentPassword.setError("Please Enter Current Password");
                    return;
                }

                if(user != null){
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                    user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fStore.collection("users").document(userId).update("email", newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.updateEmail(mNewEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ChangeEmailActivity.this, "Email Changed", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplication(), SettingActivity.class));
                                            }else{
                                                Toast.makeText(ChangeEmailActivity.this, "Error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mCurrentPassword.setError("Password not match");
                            return;
                        }
                    });
                }
            }
        });
    }
}

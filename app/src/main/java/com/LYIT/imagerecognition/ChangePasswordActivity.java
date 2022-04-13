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
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText mOldPassword, mNewPassword, mNewConPassword;
    Button mBtnChangePassword;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        mOldPassword = findViewById(R.id.oldPassword);
        mNewPassword = findViewById(R.id.newPassword);
        mNewConPassword = findViewById(R.id.conNewPassword);
        mBtnChangePassword = findViewById(R.id.btnChangePassword);
        fAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = mOldPassword.getText().toString().trim();
                String newPassword = mNewPassword.getText().toString().trim();
                String newConPassword = mNewConPassword.getText().toString().trim();

                if(TextUtils.isEmpty(oldPassword)){
                    mOldPassword.setError("Please Enter OLD Password");
                    return;
                }

                if(TextUtils.isEmpty(newPassword)){
                    mNewPassword.setError("Please Enter NEW Password");
                    return;
                }

                if(TextUtils.isEmpty(newConPassword)){
                    mNewConPassword.setError("Please Enter Confirm Password");
                    return;
                }

                if(!newConPassword.equals(newPassword)){
                    mNewConPassword.setError("New Password NOT Match");
                    return;
                }

                if(user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                    user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            user.updatePassword(mNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ChangePasswordActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                                    }else{
                                        Toast.makeText(ChangePasswordActivity.this, "Error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mOldPassword.setError("OLD Password NOT Match");
                            return;
                        }
                    });
                }
            }
        });
    }
}

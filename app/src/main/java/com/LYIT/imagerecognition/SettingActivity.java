package com.LYIT.imagerecognition;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingActivity extends AppCompatActivity {

    TextView mUserName, mUserEmail;
    Button mBtnSignOut, mBtnChangeName ,mBtnChangeEmail, mBtnChangePassword, mBtnDeleteAccount;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        mUserName = findViewById(R.id.userName);
        mUserEmail = findViewById(R.id.userEmail);
        mBtnSignOut = findViewById(R.id.btnSignOut);
        mBtnChangeName = findViewById(R.id.btnChangeName);
        mBtnChangeEmail = findViewById(R.id.btnChangeEmail);
        mBtnChangePassword = findViewById(R.id.btnChangePassword);
        mBtnDeleteAccount = findViewById(R.id.btnDeleteAcc);

        mBtnSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    mUserName.setText(value.getString("name"));
                }
            }
        });

        if(user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();

            mUserName.setText(name);
            mUserEmail.setText(email);
        }

        mBtnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("This account will be deleted and remove from system");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       fStore.collection("users").document(userID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           Toast.makeText(SettingActivity.this, "User Account Deleted", Toast.LENGTH_SHORT).show();
                                           startActivity(new Intent(getApplication(), SignInActivity.class));
                                       }else{
                                           Toast.makeText(SettingActivity.this, "Error!!!" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                           }
                       });
                   }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        mBtnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), ChangeNameActivity.class));
            }
        });

        mBtnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), ChangeEmailActivity.class));
            }
        });

        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
            }
        });
    }
}

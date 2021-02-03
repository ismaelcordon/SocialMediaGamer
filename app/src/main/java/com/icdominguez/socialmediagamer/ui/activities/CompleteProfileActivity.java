package com.icdominguez.socialmediagamer.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.models.User;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;

import java.util.Date;

import dmax.dialog.SpotsDialog;

public class CompleteProfileActivity extends AppCompatActivity {

    EditText mEditTextUsername, mEditTextPhone;
    Button btnRegister;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mEditTextUsername = findViewById(R.id.editTextUsername);
        btnRegister = findViewById(R.id.buttonRegister);

        mEditTextPhone = findViewById(R.id.editTextPhone);

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();
    }

    private void register() {
        String username = mEditTextUsername.getText().toString();
        String phone = mEditTextPhone.getText().toString();

        if(username.isEmpty()) {
            mEditTextUsername.setError("El username no puede ser vacío");
        } else {
            updateUser(username, phone);
        }
    }

    private void updateUser(final String username, final String phone) {
        String id = mAuthProvider.getUid();
        User currentUser = new User();
        currentUser.setUserId(id);
        currentUser.setUsername(username);
        currentUser.setTimestamp(new Date().getTime());
        currentUser.setPhone(phone);

        mDialog.show();

        mUserProvider.updateUser(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    mDialog.dismiss();
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    mDialog.dismiss();
                    Toast.makeText(CompleteProfileActivity.this, "Error al almacenar el usuario en BBDD", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
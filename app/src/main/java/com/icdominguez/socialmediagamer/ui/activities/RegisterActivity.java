package com.icdominguez.socialmediagamer.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.models.User;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;
    EditText mEditTextEmail, mEditTextUsername, mEditTextPassword, mEditTextPassword2, mEditTextPhone;
    Button btnRegister;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextUsername = findViewById(R.id.editTextUsername);
        mEditTextPassword = findViewById(R.id.editTextPassword);
        mEditTextPassword2 = findViewById(R.id.editTextPassword2);
        mEditTextPhone = findViewById(R.id.editTextPhone);
        btnRegister = findViewById(R.id.buttonRegister);


        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();
        String password2 = mEditTextPassword2.getText().toString();
        String phone = mEditTextPhone.getText().toString();

        if(username.isEmpty()) {
            mEditTextUsername.setError("El username no puede ser vacío");
        } else if(email.isEmpty()) {
            mEditTextEmail.setError("El email no puede ser vacío");
        } else if(password.isEmpty()) {
            mEditTextPassword.setError("La password no puede ser vacía");
        } else if(password2.isEmpty()) {
            mEditTextPassword2.setError("La password no puede ser vacía");
        } else if(!password.equals(password2)) {
            mEditTextPassword.setError("Las contraseñas no coinciden");
        } else if(phone.isEmpty()) {
            mEditTextPhone.setError("El numero de teléfono no puede ser vacío");
        } else if(!isEmailValid(email)) {
                mEditTextEmail.setError("Email no válido");
        } else if(password.length() >= 6) {
            createUser(username,email,password, phone);
        } else if(password.length() < 6) {
            Toast.makeText(this,"La contraseña debe de tener al menso 6 caracteres", Toast.LENGTH_SHORT).show();
        }


    }

    private void createUser(final String username, final String email, final String password, final String phone) {
        mDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    String id = mAuthProvider.getUid();
                    User currentUser = new User();
                    currentUser.setUserId(id);
                    currentUser.setUsername(username);
                    currentUser.setEmail(email);
                    currentUser.setPhone(phone);
                    currentUser.setTimestamp(new Date().getTime());

                    mUserProvider.createUser(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error al almacenar el usuario en BBDD", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
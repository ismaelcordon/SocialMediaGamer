package com.icdominguez.socialmediagamer.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.icdominguez.socialmediagamer.R;
import com.icdominguez.socialmediagamer.models.Post;
import com.icdominguez.socialmediagamer.models.User;
import com.icdominguez.socialmediagamer.providers.AuthProvider;
import com.icdominguez.socialmediagamer.providers.ImageProvider;
import com.icdominguez.socialmediagamer.providers.UserProvider;
import com.icdominguez.socialmediagamer.utils.FileUtil;
import com.icdominguez.socialmediagamer.utils.ViewedMessageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    Button mButtonEditProfile;

    CircleImageView mCircleImageViewGoBack, mCircleImageViewProfile;
    ImageView mImageViewCover;
    EditText mEditTextUsername, mEditTextPhone;

    AlertDialog.Builder mBuilderSelector;
    CharSequence options[];

    CircleImageView mCircleImageViewBack;

    // PHOTO 1
    String mAbsolutePhotoPath;
    String mPhotoPath;
    File mPhotoFile;

    // PHOTO 2
    String mAbsolutePhotoPath2;
    String mPhotoPath2;
    File mPhotoFile2;

    File mImageFile;
    File mImageFile2;

    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int GALLERY_REQUEST_CODE_COVER = 2;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;
    private final int PHOTO_REQUEST_CODE_COVER = 4;

    String username;
    String phone;
    String mImageProfile = null;
    String mImageCover = null;

    AlertDialog mDialog;

    ImageProvider mImageProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mCircleImageViewGoBack = findViewById(R.id.circleImageBack);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfilePhoto);
        mEditTextUsername = findViewById(R.id.editTextUsername);
        mEditTextPhone = findViewById(R.id.editTextPhone);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mButtonEditProfile = findViewById(R.id.btnEditProfile);
        mImageProvider = new ImageProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

        options = new CharSequence[] {"Imagen de galeria", "Tomar foto"};

        mCircleImageViewGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditProfileActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");

        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(1);
            }
        });

        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(2);
            }
        });

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        getUser();
    }

    private void updateInfo(User user) {
        mDialog.show();
        if(mDialog.isShowing()) {
            mUserProvider.updateUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDialog.dismiss();
                    if(task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "La información se actualizó correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateUserProfile() {
        username = mEditTextUsername.getText().toString();
        phone = mEditTextPhone.getText().toString();
        if(!username.isEmpty() && !phone.isEmpty()) {
            if(mImageFile != null && mImageFile2 != null) {
                saveImageCoverAndProfile(mImageFile, mImageFile2);
            }

            // Ambas imagenes de la camara
            else if(mPhotoFile != null && mPhotoFile2 != null) {
                saveImageCoverAndProfile(mPhotoFile, mPhotoFile2);
            }

            // Una imagen de galeria y otra de la camara
            else if(mImageFile != null && mPhotoFile2 != null) {
                saveImageCoverAndProfile(mImageFile, mPhotoFile2);
            }
            else if(mPhotoFile != null && mImageFile2 != null) {
                saveImageCoverAndProfile(mPhotoFile,mImageFile2);
            }
            else if(mPhotoFile != null) {
                saveImage(mPhotoFile, true);
            }
            else if(mPhotoFile2 != null){
                saveImage(mPhotoFile2, false);
            } else if(mImageFile != null) {
                saveImage(mImageFile, true);
            } else if(mImageFile2 != null) {
                saveImage(mImageFile2, false);
            }
            else {
                User user = new User();
                user.setUsername(username);
                user.setPhone(phone);
                user.setUserId(mAuthProvider.getUid());
                updateInfo(user);
            }
        } else {
            Toast.makeText(this,"Ingrese el username y el telefono", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUser() {
        mUserProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    if(documentSnapshot.contains("username")) {
                        username = documentSnapshot.getString("username");
                        mEditTextUsername.setText(username);
                    }
                    if(documentSnapshot.contains("phone")) {
                        phone = documentSnapshot.getString("phone");
                        mEditTextPhone.setText(phone);
                    }

                    if(documentSnapshot.contains("imageProfile")) {
                        mImageProfile = documentSnapshot.getString("imageProfile");
                        if(mImageProfile != null) {
                            Picasso.with(EditProfileActivity.this).load(mImageProfile).into(mCircleImageViewProfile);
                        }
                    }

                    if(documentSnapshot.contains("imageCover")) {
                        mImageCover = documentSnapshot.getString("imageCover");
                        if(mImageCover != null) {
                            Picasso.with(EditProfileActivity.this).load(mImageCover).into(mImageViewCover);
                        }
                    }
                }
            }
        });
    }

    private void saveImageCoverAndProfile(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();

                            mImageProvider.save(EditProfileActivity.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()) {
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlCover = uri2.toString();
                                                Post post = new Post();
                                                User currentUser = new User();
                                                currentUser.setPhone(phone);
                                                currentUser.setUsername(username);
                                                currentUser.setImageProfile(urlProfile);
                                                currentUser.setImageCover(urlCover);
                                                currentUser.setUserId(mAuthProvider.getUid());
                                                updateInfo(currentUser);
                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "La imagen 2 no se pudo guardar", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveImage(File image, final boolean isProfileImage) {
        mDialog.show();
        mImageProvider.save(EditProfileActivity.this, image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            User currentUser = new User();
                            currentUser.setPhone(phone);
                            currentUser.setUsername(username);
                            if(isProfileImage) {
                                currentUser.setImageProfile(url);
                                currentUser.setImageCover(mImageProfile);
                            } else {
                                currentUser.setImageCover(url);
                                currentUser.setImageProfile(mImageProfile);
                            }
                            currentUser.setUserId(mAuthProvider.getUid());
                            updateInfo(currentUser);

                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void selectOptionImage(final int numberImage) {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0) {
                    if(numberImage == 1) {
                        openGallery(GALLERY_REQUEST_CODE_PROFILE);
                    }
                    else if(numberImage == 2) {
                        openGallery(GALLERY_REQUEST_CODE_COVER);
                    }
                }
                else if (i == 1) {
                    if(numberImage == 1) {
                        takePhoto(PHOTO_REQUEST_CODE_PROFILE);
                    } else if(numberImage == 2) {
                        takePhoto(PHOTO_REQUEST_CODE_COVER);
                    }
                }
            }
        });

        mBuilderSelector.show();
    }

    private void takePhoto(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile(requestCode);
            } catch (Exception e){
                Toast.makeText(this, "CHubo un error al obtener un archivo" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.icdominguez.socialmediagamer", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE_PROFILE);
            }
        }
    }

    private File createPhotoFile(int requestCode) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(new Date() + "_photo", ".jpg", storageDir);

        if(requestCode == PHOTO_REQUEST_CODE_PROFILE) {
            mPhotoPath = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath = photoFile.getAbsolutePath();
        }
        else if(requestCode == PHOTO_REQUEST_CODE_COVER) {
            mPhotoPath2 = "file:" + photoFile.getAbsolutePath();
            mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
        }

        return photoFile;
    }

    private void openGallery(int requesCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requesCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Seleccion de imagen de galeria
         */
        if(requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            try {
                mPhotoFile = null;
                mImageFile = FileUtil.from(this, data.getData());
                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch(Exception e) {
                Log.d("ERROR", "Se produjo un error al abrir el intent de la galeria" + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK) {
            try {
                mPhotoFile2 = null;
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            } catch(Exception e) {
                Log.d("ERROR", "Se produjo un error al abrir el intent de la galeria" + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Seleccion de fotografia
         */

        if(requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            mImageFile = null;
            mPhotoFile = new File(mAbsolutePhotoPath);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(mCircleImageViewProfile);
        }

        if(requestCode == PHOTO_REQUEST_CODE_COVER && resultCode == RESULT_OK) {
            mImageFile2 = null;
            mPhotoFile2 = new File(mAbsolutePhotoPath2);
            Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(mImageViewCover);
        }

    }

    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, EditProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, EditProfileActivity.this);
    }
}
package com.icdominguez.socialmediagamer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class TranslationDemoActivity extends AppCompatActivity {

    EditText etTextToTranslate;
    Button translateButton;
    TextView tvTranslated;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_demo);

        translateButton = findViewById(R.id.translateButton);
        etTextToTranslate = findViewById(R.id.inputToTranslate);
        tvTranslated = findViewById(R.id.translatedTv);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = etTextToTranslate.getText().toString();
                FirebaseTranslatorOptions options =
                        new FirebaseTranslatorOptions.Builder()
                                .setSourceLanguage(FirebaseTranslateLanguage.ES)
                                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                                .build();
                final FirebaseTranslator englishGermanTranslator =
                        FirebaseNaturalLanguage.getInstance().getTranslator(options);

                FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                        .requireWifi()
                        .build();


                englishGermanTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void v) {
                                        Toast.makeText(TranslationDemoActivity.this, "Diccionario alemán descargado", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Model couldn’t be downloaded or other internal error.
                                        // ...
                                        Toast.makeText(TranslationDemoActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                                    }
                                });

                englishGermanTranslator.translate(text)
                        .addOnSuccessListener(
                                new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tvTranslated.setText(s);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TranslationDemoActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                                    }
                                });
            }
        });
    }
}
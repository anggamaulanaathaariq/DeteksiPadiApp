package id.ac.polinema.deteksipadiapp.testing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;

import id.ac.polinema.appmusic.R;
import id.ac.polinema.deteksipadiapp.kotlin.ClassificationHSV;
import id.ac.polinema.deteksipadiapp.kotlin.ClassificationThresholding;

public class TestingBentuk extends AppCompatActivity {

    ImageView selectedImage2;
    Button galleryBtn2, detectColorButton;
    private Button camera_button2;
    private int REQUEST_STORAGE=111;
    private int REQUEST_FILE=222;
    private Uri uri;
    private String stringPath;
    private Intent iData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_bentuk);
//        selectedImage2 = findViewById(R.id.displayImageView2);
//        galleryBtn2 = findViewById(R.id.galerybutton2);
        detectColorButton = findViewById(R.id.kotlin_btn2);
        camera_button2 = findViewById(R.id.camera_button2);
        OpenCVLoader.initDebug();
        camera_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestingBentuk.this, Thresholding.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(TestingBentuk.this, "Camera Btn is Clicked", Toast.LENGTH_SHORT).show();
            }
        });

//        galleryBtn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(TestingBentuk.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
//                } else {
//                    choooseImage();
//                }
//            }
//        });
        detectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestingBentuk.this, ClassificationThresholding.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    private void choooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE && resultCode == RESULT_OK) {

            if (data != null){
                uri = data.getData();
                iData = data;

                getStringPath(uri);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    selectedImage2.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getStringPath(Uri myUri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(myUri,filePathColumn,null,null,null);

        if (cursor == null){
            stringPath = myUri.getPath();
        } else{
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            stringPath = cursor.getString(columnIndex);
            cursor.close();
        }
        return stringPath;

    }
}
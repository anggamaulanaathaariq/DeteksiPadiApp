package id.ac.polinema.deteksipadiapp.testing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.schema.Model;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import id.ac.polinema.appmusic.R;

import id.ac.polinema.deteksipadiapp.kotlin.ClassificationHSV;
//import id.ac.polinema.deteksipadiapp.kotlin.KotlinClass;

public class TestingWarna extends AppCompatActivity {

    ImageView selectedImage;
    Button galleryBtn, detectColorButton,java_btn;
    private Button camera_button;
    private int PERMISSION_STORAGE=10;

    private int ACCESS_STORAGE=30;

    private int REQUEST_STORAGE=111;
    private int REQUEST_FILE=222;
    private Uri uri;
    private String stringPath;
    private Intent iData;
    private Bitmap img;
    private Classifier classifier;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_warna);

        context = this;
//        selectedImage = findViewById(R.id.displayImageView);
//        galleryBtn = findViewById(R.id.galerybutton);
//        rincianText = findViewById(R.id.rincianText);
        detectColorButton = findViewById(R.id.kotlin_btn);
//        TextJava = findViewById(R.id.TextJava);
//        java_btn = findViewById(R.id.java_btn);

        camera_button = findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestingWarna.this, ColorDetection.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(TestingWarna.this, "Camera Btn is Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            Log.e("Image Classifier Error", "ERROR: " + e);
        }

//        java_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                img = Bitmap.createScaledBitmap(img, 224, 224, true);
//
//                try {
//                    ModelUnquant model = ModelUnquant.newInstance(context);
//
//                    // Creates inputs for reference.
//                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//
//                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
//                    tensorImage.load(img);
//                    ByteBuffer byteBuffer = tensorImage.getBuffer();
//                    inputFeature0.loadBuffer(byteBuffer);
//
//                    // Runs model inference and gets result.
//                    ModelUnquant.Outputs outputs = model.process(inputFeature0);
//                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//                    // Releases model resources if no longer used.
//                    model.close();
//
//                    TextJava.setText(outputFeature0.getFloatArray()[0] + "\n"+outputFeature0.getFloatArray()[1]);
//                } catch (IOException e) {
//                    // TODO Handle the exception
//                }
//            }
//        });

//        galleryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_PICK);
//                    intent.setType("image/*");
//                    startActivityForResult(Intent.createChooser(intent,"Pilih Gambar"), ACCESS_STORAGE);
//                }
//            }
//        });

        detectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestingWarna.this, ClassificationHSV.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCESS_STORAGE && resultCode == Activity.RESULT_OK) {
            try {
                Uri uriStorage = data.getData();
                Bitmap bitmapStorage = MediaStore.Images.Media.getBitmap(getContentResolver(),uriStorage);
                selectedImage.setImageBitmap(bitmapStorage);

//                List<Classifier.Recognition> predicitons = classifier.recognizeImage(
//                        bitmapStorage, 0);
//
//                // creating a list of string to display in list view
//                final List<String> predicitonsList = new ArrayList<>();
//                for (Classifier.Recognition recog : predicitons) {
//                    predicitonsList.add(recog.getName() + "  ::::::::::  " + recog.getConfidence());
//                }
//
//                // creating an array adapter to display the classification result in list view
//                ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(
//                        this, R.layout.support_simple_spinner_dropdown_item, predicitonsList);
//                ListText.setAdapter(predictionsAdapter);

            } catch (IOException e) {
                e.printStackTrace();
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


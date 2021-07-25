package id.ac.polinema.deteksipadiapp.testing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import id.ac.polinema.deteksipadiapp.kotlin.ClassificationHSV;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.ac.polinema.appmusic.R;

public class Thresholding extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG="TestingBentuk";
    CameraBridgeViewBase cameraBridgeViewBase;
    private ImageView capture_image;
    private int take_image = 0;
    private Mat mRgba;
    Mat imgHSV, imgThresholded, imgCanny;


    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    cameraBridgeViewBase.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };
    public Thresholding(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(Thresholding.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Thresholding.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
        setContentView(R.layout.activity_thresholding);

        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        capture_image = findViewById(R.id.capture_image);
        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (take_image == 0) {
                    take_image = 1;
                } else {
                    take_image = 0;
                }
            }
        });

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
       imgHSV = inputFrame.rgba();
       Imgproc.cvtColor(imgHSV, imgThresholded, Imgproc.COLOR_RGB2GRAY);
       Imgproc.cvtColor(imgHSV,imgThresholded, Imgproc.COLOR_RGB2HSV_FULL);
       Imgproc.Canny(imgHSV, imgCanny, 50,100);
        take_image=take_picture_function(take_image,imgCanny);
        return imgCanny;
    }

    private int take_picture_function(int take_image, Mat imgCanny) {
       if (take_image==1){
           Mat save_mat=new Mat();
           Core.flip(imgCanny.t(),save_mat,1);
           File folder = new File(Environment.getExternalStorageDirectory().getPath()+"/ImageThresholding");
           boolean success=true;
           if (!folder.exists()){
               success=folder.mkdirs();
           }
           SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
           MediaActionSound sound = new MediaActionSound();
           sound.play(MediaActionSound.SHUTTER_CLICK);
           String currentDateandTime= sdf.format(new Date());
           String fileName=Environment.getExternalStorageDirectory().getPath()+"/ImageThresholding/"+currentDateandTime+" .jpg";
           Imgcodecs.imwrite(fileName,save_mat);
           take_image=0;
       }
        return take_image;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        imgHSV = new Mat(width,height, CvType.CV_8UC4);
        imgThresholded = new Mat(width, height, CvType.CV_8UC1);
        imgCanny = new Mat(height,width,CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        imgHSV.release();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            //if load success
            Log.d(TAG, "Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            //if not loaded
            Log.d(TAG, "Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}
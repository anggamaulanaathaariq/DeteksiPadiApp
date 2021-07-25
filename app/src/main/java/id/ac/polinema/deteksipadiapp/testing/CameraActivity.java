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
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.ac.polinema.appmusic.R;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG="TestingWarna";

    JavaCameraView javaCameraView;
    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private int mCameraId=0;
    private ImageView take_picture_button;
    private int take_image=0;

    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };
    public CameraActivity(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        take_picture_button=findViewById(R.id.take_picture_button);
        take_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(take_image==0){
                    take_image=1;
                }
                else{
                    take_image=0;
                }
            }
        });

    }
        @Override
        protected void onResume () {
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
        protected void onPause () {
            super.onPause();
            if (mOpenCvCameraView != null) {
                mOpenCvCameraView.disableView();
            }
        }

        public void onDestroy () {
            super.onDestroy();
            if (mOpenCvCameraView != null) {
                mOpenCvCameraView.disableView();
            }

        }

        public void onCameraViewStarted ( int width, int height){
            mRgba = new Mat(height, width, CvType.CV_8UC4);
            mGray = new Mat(height, width, CvType.CV_8UC1);
        }
        public void onCameraViewStopped () {
            mRgba.release();
        }
        public Mat onCameraFrame (CameraBridgeViewBase.CvCameraViewFrame inputFrame){
            mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();

            take_image=take_picture_function_rgb(take_image,mRgba);

            return mRgba;

        }

    private int take_picture_function_rgb(int take_image, Mat mRgba) {
        if (take_image==1){
            Mat save_mat=new Mat();
            Core.flip(mRgba.t(),save_mat,1);
            Imgproc.cvtColor(save_mat,save_mat,Imgproc.COLOR_RGBA2BGRA);
            File folder = new File(Environment.getExternalStorageDirectory().getPath()+"/ImagePro");
            boolean success=true;
            if (!folder.exists()){
                success=folder.mkdirs();
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            MediaActionSound sound = new MediaActionSound();
            sound.play(MediaActionSound.SHUTTER_CLICK);
            String currentDateandTime= sdf.format(new Date());
            String fileName=Environment.getExternalStorageDirectory().getPath()+"/ImagePro/"+currentDateandTime+" .jpg";
            Imgcodecs.imwrite(fileName,save_mat);
            take_image=0;
        }
        return take_image;
    }
}

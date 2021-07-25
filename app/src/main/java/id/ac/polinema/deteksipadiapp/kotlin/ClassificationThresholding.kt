package id.ac.polinema.deteksipadiapp.kotlin

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import id.ac.polinema.appmusic.R
import id.ac.polinema.appmusic.ml.MobilenetV110224Quant

import id.ac.polinema.appmusic.ml.ModelUnquant

import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ClassificationThresholding : AppCompatActivity() {

    lateinit var galerybuttonKt : Button
    lateinit var detectColorButtonKt : Button
    lateinit var displayImageViewKt : ImageView
    lateinit var textDetect : TextView
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classification_thresholding)


        galerybuttonKt = findViewById(R.id.galerybuttonKt)
        detectColorButtonKt = findViewById(R.id.detectColorButtonKt)
        displayImageViewKt = findViewById(R.id.displayImageViewKt)
        textDetect = findViewById(R.id.textDetect)

        val fileName = "labels.txt"
        val inputString = application.assets.open(fileName).bufferedReader().use { it.readText() }
        val padiList = inputString.split("\n")

//        val labels = application.assets.open("labels.txt").bufferedReader().use { it.readText() }.split("\n")

        galerybuttonKt.setOnClickListener(View.OnClickListener {
            Log.d("mssg", "button pressed")
            var intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 100)
        })

        detectColorButtonKt.setOnClickListener(View.OnClickListener {
            var resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val model = MobilenetV110224Quant.newInstance(this)

            var tbuffer = TensorImage.fromBitmap(resized)
            var byteBuffer = tbuffer.buffer

// Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            inputFeature0.loadBuffer(byteBuffer)
// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var max = getMax(outputFeature0.floatArray)

            textDetect.setText(padiList[max])

// Releases model resources if no longer used.
            model.close()
        })
    }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            displayImageViewKt.setImageURI(data?.data)

            var uri: Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }

    fun getMax(arr:FloatArray) : Int{
        var ind = 0;
        var min = 0.0f;

        for(i in 0..5) {
            if(arr[i] > min)
            {
                min = arr[i]
                ind = i;
            }
        }
        return ind
    }
}
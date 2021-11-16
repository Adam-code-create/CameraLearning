package uz.gita.cameralearning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import uz.gita.cameralearning.databinding.ActivityMainBinding
import uz.xsoft.learncamerax.utils.checkPermissions
import java.io.File
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var outputDirectory : File? = null
    private var imageCapture : ImageCapture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        outputDirectory = getOutputDirectory()
        checkPermissions(arrayOf(android.Manifest.permission.CAMERA)){
            startCamera()
            binding.cameraBtn.setOnClickListener {
                getPhoto()
            }
        }
    }

    private fun startCamera(){
        val cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraProviderFeature.addListener({
            val cameraProvider :ProcessCameraProvider = cameraProviderFeature.get()
            val preview =Preview.Builder().build()
            preview.setSurfaceProvider(binding.preview.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this,cameraSelector, preview, imageCapture)




            }catch (e :Exception){
                Log.d("TTT", e.message.toString())
            }

        },ContextCompat.getMainExecutor(this))
    }

    private fun getOutputDirectory() :File {
        val mediaDirectory = externalMediaDirs.firstOrNull()?.let {
            File(it,resources.getString(R.string.app_name)).apply {
                mkdir()
            }
        }
        return if (mediaDirectory != null && mediaDirectory.exists()) mediaDirectory
        else filesDir

    }

    private fun getPhoto(){
        imageCapture?.let {
            val photoFile = File(outputDirectory, "myPhoto${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            it.takePicture(outputOptions,ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(this@MainActivity, "Rasm saqlandi", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d("TTT", exception.imageCaptureError.toString())
                }

            })
        }
    }

}
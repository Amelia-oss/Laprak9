package com.rahman.laprak

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.rahman.laprak.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var capturedBitmap: Bitmap? = null

    // ================= CLOUDINARY =================
    private fun configCloudinary() {
        val config = HashMap<String, String>()
        config["cloud_name"] = "djobwqaqg"
        config["api_key"] = "928667467914918"
        config["api_secret"] = "N4R8dSgOmvO2tvhxzUifVg1TpF8"
        MediaManager.init(this, config)
    }

    // ================= PERMISSION =================
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                binding.tvStatus.text = "Izin kamera ditolak"
            }
        }

    // ================= CAMERA =================
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                capturedBitmap = it
                binding.imageView.setImageBitmap(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configCloudinary()

        binding.btPickImage.text = "Buka Kamera"
        binding.btPickImage.setOnClickListener {
            checkCameraPermission()
        }

        binding.btSave.text = "Upload"
        binding.btSave.setOnClickListener {
            uploadToCloudinary()
        }
    }

    // ================= CHECK PERMISSION =================
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }

    // ================= UPLOAD =================
    private fun uploadToCloudinary() {
        val bitmap = capturedBitmap ?: run {
            binding.tvStatus.text = "Ambil foto dulu"
            return
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()

        MediaManager.get()
            .upload(byteArray)
            .callback(object : UploadCallback {

                override fun onStart(requestId: String) {
                    binding.tvStatus.text = "Upload dimulai..."
                }

                override fun onProgress(
                    requestId: String,
                    bytes: Long,
                    totalBytes: Long
                ) {
                    binding.tvStatus.text = "Mengunggah..."
                    binding.imageView.load("https://i.pinimg.com/1200x/da/07/a8/da07a810a210e37988780cbfb691ab9c.jpg")
                }

                override fun onSuccess(
                    requestId: String,
                    resultData: Map<*, *>
                ) {
                    val url = resultData["secure_url"].toString()
                    binding.tvStatus.text = "Berhasil!\n$url"
                    binding.imageView.load(url)
                    Log.d("CLOUDINARY", url)
                    // In the source Activity (e.g., MainActivity)
                    val i = Intent(this@MainActivity, bukagambar::class.java)
                    i.putExtra("key1", url)
                    startActivity(intent)

                }

                override fun onError(
                    requestId: String,
                    error: ErrorInfo
                ) {
                    binding.tvStatus.text = "Gagal: ${error.description}"
                }

                override fun onReschedule(
                    requestId: String,
                    error: ErrorInfo
                ) {}
            })
            .dispatch()
    }
}


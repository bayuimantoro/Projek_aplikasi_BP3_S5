package com.example.porjek_aplikasi

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class AboutActivity : AppCompatActivity() {

    private lateinit var ivAboutPhoto: CircleImageView
    private var tempCameraUri: Uri? = null

    companion object {
        private const val PREFS_NAME = "profile_prefs"
        private const val KEY_PROFILE_IMAGE = "profile_image_base64"
    }

    // Camera permission launcher
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            startCrop(tempCameraUri!!)
        }
    }

    // Gallery launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { startCrop(it) }
    }

    // UCrop launcher
    private val cropLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                ivAboutPhoto.setImageURI(it)
                saveImageToPrefs(it)
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(result.data!!)
            Toast.makeText(this, "Crop error: ${error?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.about_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
            insets
        }

        ivAboutPhoto = findViewById(R.id.iv_about_photo)
        val btnEditPhoto = findViewById<ImageButton>(R.id.btn_edit_photo)

        findViewById<TextView>(R.id.tv_about_name).text = "Bayu Imantoro"
        findViewById<TextView>(R.id.tv_about_email).text = "bayuimantoro5@gmail.com"

        // Load saved profile image
        loadSavedImage()

        // Click on photo to preview
        ivAboutPhoto.setOnClickListener {
            showImagePreview()
        }

        // Click on edit button to change photo
        btnEditPhoto.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun loadSavedImage() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val base64Image = prefs.getString(KEY_PROFILE_IMAGE, null)
        if (base64Image != null) {
            try {
                val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivAboutPhoto.setImageBitmap(bitmap)
            } catch (e: Exception) {
                ivAboutPhoto.setImageResource(R.drawable.about_me)
            }
        } else {
            ivAboutPhoto.setImageResource(R.drawable.about_me)
        }
    }

    private fun saveImageToPrefs(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_PROFILE_IMAGE, base64Image).apply()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImagePreview() {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_image_preview)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val ivPreview = dialog.findViewById<ImageView>(R.id.iv_preview)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btn_close)

        // Copy the drawable from CircleImageView
        ivPreview.setImageDrawable(ivAboutPhoto.drawable)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showImageSourceDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.dialog_image_source)

        val llCamera = bottomSheetDialog.findViewById<LinearLayout>(R.id.ll_camera)
        val llGallery = bottomSheetDialog.findViewById<LinearLayout>(R.id.ll_gallery)

        llCamera?.setOnClickListener {
            bottomSheetDialog.dismiss()
            checkCameraPermissionAndOpen()
        }

        llGallery?.setOnClickListener {
            bottomSheetDialog.dismiss()
            galleryLauncher.launch("image/*")
        }

        bottomSheetDialog.show()
    }

    private fun checkCameraPermissionAndOpen() {
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
        val photoFile = File(cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
        tempCameraUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        tempCameraUri?.let { cameraLauncher.launch(it) }
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationFile = File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        val destinationUri = Uri.fromFile(destinationFile)

        val options = UCrop.Options().apply {
            setCircleDimmedLayer(true)
            setShowCropGrid(false)
            setShowCropFrame(false)
            setToolbarTitle("Crop Foto")
            setCompressionQuality(90)
        }

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)
            .withOptions(options)

        cropLauncher.launch(uCrop.getIntent(this))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
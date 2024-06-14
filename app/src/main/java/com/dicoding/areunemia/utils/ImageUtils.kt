package com.dicoding.areunemia.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.dicoding.areunemia.BuildConfig
import com.dicoding.areunemia.R
import com.yalantis.ucrop.UCrop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAXIMAL_SIZE = 2000000 //2 MB
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun showImageSourceDialog(
    fragment: Fragment,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    takePhotoLauncher: ActivityResultLauncher<Uri>,
    onImageSelected: (Uri) -> Unit
) {
    val optionsArray = fragment.resources.getStringArray(R.array.image_source_options)
    val builder = AlertDialog.Builder(fragment.requireContext())
    builder.setTitle(R.string.select_image_source)
        .setItems(optionsArray) { _, which ->
            when (which) {
                0 -> pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                1 -> {
                    if (allPermissionsGranted(fragment.requireContext())) {
                        val imageUri = getImageUri(fragment.requireContext())
                        takePhotoLauncher.launch(imageUri)
                        onImageSelected(imageUri)
                    } else {
                        requestPermissionLauncher.launch(REQUIRED_PERMISSION)
                    }
                }
            }
        }
    val dialog = builder.create()
    dialog.show()
}

fun showEyeExampleDialog(
    fragment: Fragment,
    onProceed: () -> Unit
) {
    val dialogView = LayoutInflater.from(fragment.requireContext()).inflate(R.layout.dialog_eye_example, null)
    val builder = AlertDialog.Builder(fragment.requireContext())
        .setView(dialogView)
        .setTitle(R.string.example_eye_picture)
        .setPositiveButton(R.string.ok) { _, _ ->
            onProceed()
        }
        .setNegativeButton(R.string.cancel, null)
    builder.create().show()
}

fun allPermissionsGranted(context: Context) =
    ContextCompat.checkSelfPermission(
        context,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

fun startCrop(fragment: Fragment, uri: Uri) {
    val destinationUri = Uri.fromFile(File(fragment.requireContext().cacheDir, "${System.nanoTime()}.jpg"))
    UCrop.of(uri, destinationUri)
        .withAspectRatio(1f, 1f)
        .withMaxResultSize(640, 640)
        .withOptions(getUCropOptions()) // Setting options for exact result size
        .start(fragment.requireContext(), fragment)
}

private fun getUCropOptions(): UCrop.Options {
    val options = UCrop.Options()
    options.setCompressionQuality(100) // Set the compression quality to highest
    options.withMaxResultSize(640, 640) // Ensuring the result size is exactly 640x640
    return options
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}


fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA

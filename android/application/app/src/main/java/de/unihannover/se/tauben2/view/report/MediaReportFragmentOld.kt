package de.unihannover.se.tauben2.view.report

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.SquareImageView
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.fragment_report_media.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_VIDEO_CAPTURE = 2

class MediaReportFragmentOld : ReportFragment() {

    private lateinit var v: View


    init {
        pagePos = PagePos.FIRST
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_report_media, container, false)

        setBtnListener(R.id.fragment_report_location, null)

        (activity as ReportActivity).prev_btn.setOnClickListener {
            (activity as ReportActivity).finish()
        }

        val alertBuilder = AlertDialog.Builder(context).apply {
            setTitle("What kind of media do you want to add?")
            setItems(arrayOf("Take photo", "Take video", "Get local media")){ dialogInterface, i ->
                if(mCreatedCase.media.size > 3) {
                    setSnackBar(v, "Maximum amount reached.")
                    return@setItems
                }
                if(i == 0)
                    dispatchTakePictureIntent()
                else if(i ==1)
                    dispatchTakeVideoIntent()
                dialogInterface.dismiss()
            }
        }

        v.report_media_add_button.setOnClickListener {
            alertBuilder.show()
        }

        createBlankImages(v)

        loadImages()

        return v
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            val packageManager = activity?.packageManager
            packageManager?.let {
                takePictureIntent.resolveActivity(it)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also { file ->
                        val photoURI: Uri? = context?.let { context ->
                            FileProvider.getUriForFile(
                                    context,
                                    "de.unihannover.se.tauben2.fileprovider",
                                    file
                            )
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, 1)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(Date())
        val storageDir: File? = context?.filesDir
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            mCreatedCase.media += absolutePath.getFileName()
        }
    }




//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            activity?.packageManager?.also {
//                takePictureIntent.resolveActivity(it)?.also {
//                    // Create the File where the photo should go
//                    val photoFile: File? = try {
//                        createImageFile()
//                    } catch (ex: IOException) { null }
//                    // Continue only if the File was successfully created
//                    photoFile?.also { file ->
//                        val photoURI: Uri? = context?.let { context ->
//                            FileProvider.getUriForFile(context, "de.unihannover.se.tauben2.fileprovider", file)
//                        }
////                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                        startActivityForResult(takePictureIntent, 1)
//                    }
//                }
//            }
//        }
//    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            activity?.packageManager?.let {
                takeVideoIntent.resolveActivity(it)?.also {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                }
            }
        }
    }

//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(Date())
//        val storageDir: File? = context?.filesDir
//        return File.createTempFile(
//                "JPEG_${timeStamp}_", /* prefix */
//                ".jpg", /* suffix */
//                storageDir /* directory */
//        ).apply {
//            mCreatedCase.media += absolutePath.getFileName()
//        }
//    }

    // triggered after capturing a photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        Log.i("MediaReport", "OnActivityResult")
        if(resultCode != RESULT_OK)
            return
        when(requestCode) {
            1 -> {
                // compress most recent image
                val intentResult = intent.extras?.get("data")
                if(intentResult is Bitmap) {
                    Log.i("MediaReport", "Bitmap was chosen")
                }
                compressImage(mCreatedCase.media.last(), 80, 1000, 1000)
                loadImages()
            }
            REQUEST_VIDEO_CAPTURE -> {
                intent.data?.apply {
                    mCreatedCase.media += this.toString()
                }
            }
        }
    }

    private fun loadImages() {

        for (i in 0 until v.image_layout.childCount) {

            val layout = (v.image_layout.getChildAt(i) as ConstraintLayout)
            val image = layout.getChildAt(0) as ImageView

            layout.visibility = View.INVISIBLE

            if (image is SquareImageView && i < mCreatedCase.media.size) {

                val imageLink = if (URLUtil.isValidUrl(mCreatedCase.media[i])) mCreatedCase.media[i]
                else context?.getFileStreamPath(mCreatedCase.media[i])?.absolutePath


                if (URLUtil.isValidUrl(imageLink)) Picasso.get().load(imageLink).into(image)
                else Picasso.get().load(File(imageLink)).into(image)

                layout.visibility = View.VISIBLE
            }
        }
    }

    private fun deleteImage(image: SquareImageView) {
        for (i in 0 until v.image_layout.childCount) {
            if ((v.image_layout.getChildAt(i) as ConstraintLayout).getChildAt(0) == image) {
                // TODO remove this and do it properly
                if (!URLUtil.isValidUrl(mCreatedCase.media[i]))
                    (mCreatedCase.media as MutableList<String>).removeAt(i)
            }
        }
        loadImages()
    }

    private fun createBlankImages(view: View) {

        //  LinearLayout [
        //      3x ConstraintLayout [
        //          Image
        //          Button
        //      ]
        //  ]

        for (i in 0..2) {

            val constraintLayout = ConstraintLayout(view.context)
            val layout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
            layout.setMargins(2, 2, 2, 2)
            constraintLayout.layoutParams = layout

            val image = SquareImageView(view.context).apply {
                id = View.generateViewId()
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            val button = ImageButton(view.context).apply {
                setImageResource(R.drawable.ic_close)
                setPadding(0, 0, 0, 0)
                background = ColorDrawable(Color.TRANSPARENT)
                id = View.generateViewId()

                setOnClickListener {
                    deleteImage(image)
                }
            }

            constraintLayout.apply {
                addView(image)
                addView(button)
                visibility = View.INVISIBLE
            }

            view.image_layout.addView(constraintLayout)

            ConstraintSet().apply {
                clone(constraintLayout)
                connect(button.id, ConstraintSet.TOP, image.id, ConstraintSet.TOP)
                connect(button.id, ConstraintSet.END, image.id, ConstraintSet.END)
                applyTo(constraintLayout)
            }
        }
    }

    /**
     * Helper function for extracting the filename to a given filepath
     **/
    private fun String.getFileName(): String {
        return this.substringAfterLast("/")
    }

    /**
     * Compress and resize a given image
     * @param fileName Name of the file for locating
     * @param quality 0-100 where 0 means maximum compression
     * @param width new width of the image
     * @param height new height of the image
     */
    private fun compressImage(fileName: String, quality: Int, width: Int, height: Int) {
        val rawImage = context?.getFileStreamPath(fileName)?.absolutePath

        val resized = BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(rawImage, this)

            inSampleSize = calculateInSampleSize(this, width, height)
            inJustDecodeBounds = false

            BitmapFactory.decodeFile(rawImage, this)
        }

        val fileOutStream = context?.openFileOutput(fileName, Context.MODE_PRIVATE)
        resized.compress(Bitmap.CompressFormat.JPEG, quality, fileOutStream)
    }

    /**
     * calculate in sample size parameter for loading a scaled bitmap into memory
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}

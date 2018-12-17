package de.unihannover.se.tauben2.view.report

import android.app.Activity.RESULT_OK
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
import de.unihannover.se.tauben2.AppExecutors
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.SquareImageView
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.fragment_report_media.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MediaReportFragment : ReportFragment() {

    private lateinit var v: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_report_media, container, false)

        pagePos = PagePos.FIRST
        if (mCreatedCase == null) mCreatedCase = Case.getCleanInstance()
        setBtnListener(R.id.fragment_report_location, null)

        // clear case media entry for adding filenames for request
        mCreatedCase?.media = listOf()

        Log.d("CURRENT CASE", mCreatedCase.toString())
        createBlankImages(v)
        loadImages()

        (activity as ReportActivity).prev_btn.setOnClickListener {
            (activity as ReportActivity).finish()
        }

        v.report_media_add_button.setOnClickListener {
            if (mCreatedCase!!.media.size < 3) dispatchTakePictureIntent()
            else setSnackBar(v, "maximum amount reached")
        }

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
            mCreatedCase!!.media += absolutePath.getFileName()
        }
    }

    // triggered after capturing a photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // compress most recent image
            val media = mCreatedCase?.media
            media?.let {
                val imageName = media[media.lastIndex]
                compressImage(imageName, 80, 1000, 1000)
            }

            loadImages()
        }
    }

    private fun loadImages() {

        for (i in 0 until v.image_layout!!.childCount) {

            val layout = (v.image_layout.getChildAt(i) as ConstraintLayout)
            val image = layout.getChildAt(0) as ImageView

            layout.visibility = View.INVISIBLE

            if (image is SquareImageView && i < mCreatedCase!!.media.size) {

                val imageLink = context?.getFileStreamPath(mCreatedCase!!.media[i])?.absolutePath


                if (URLUtil.isValidUrl(imageLink)) Picasso.get().load(imageLink).into(image)
                else Picasso.get().load(File(imageLink)).into(image)

                layout.visibility = View.VISIBLE
            }
        }
    }

    private fun deleteImage(image: SquareImageView) {
        for (i in 0 until v.image_layout!!.childCount) {
            if ((v.image_layout!!.getChildAt(i) as ConstraintLayout).getChildAt(0) == image) {
                (mCreatedCase!!.media as MutableList<String>).removeAt(i)
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

            val image = SquareImageView(view.context)
            image.id = View.generateViewId()
            image.scaleType = ImageView.ScaleType.CENTER_CROP

            val button = ImageButton(view.context)
            button.setImageResource(R.drawable.ic_close_black_24dp)
            button.setPadding(0, 0, 0, 0)
            button.background = ColorDrawable(Color.TRANSPARENT)
            button.id = View.generateViewId()

            constraintLayout.addView(image)
            constraintLayout.addView(button)
            constraintLayout.visibility = View.INVISIBLE

            view.image_layout.addView(constraintLayout)

            val set = ConstraintSet()
            set.clone(constraintLayout)
            set.connect(button.id, ConstraintSet.TOP, image.id, ConstraintSet.TOP)
            set.connect(button.id, ConstraintSet.END, image.id, ConstraintSet.END)
            set.applyTo(constraintLayout)

            button.setOnClickListener {
                deleteImage(image)
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
        AppExecutors.INSTANCE.diskIO().execute {
            val rawImage = context?.getFileStreamPath(fileName)?.absolutePath
            val bitmap = BitmapFactory.decodeFile(rawImage)
            val resized = Bitmap.createScaledBitmap(bitmap, width, height, true)
            val fileOutStream = context?.openFileOutput(fileName, Context.MODE_PRIVATE)
            resized.compress(Bitmap.CompressFormat.JPEG, quality, fileOutStream)
        }
    }

}

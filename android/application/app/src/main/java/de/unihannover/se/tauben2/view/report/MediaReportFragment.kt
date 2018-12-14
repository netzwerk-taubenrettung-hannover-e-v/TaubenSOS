package de.unihannover.se.tauben2.view.report

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.entity.Case
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.fragment_report_media.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MediaReportFragment : ReportFragment() {

    private val layoutId = R.layout.fragment_report_media

    private lateinit var mCurrentPhotoPath: String
    private lateinit var mImageView: ImageView
    private var addedImagesCount = 0

    private val TAKE_PICTURE_REQUEST = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(layoutId, container, false)

        pagePos = PagePos.FIRST
        mCreatedCase = Case.getCleanInstance()
        setBtnListener (R.id.fragment_report_location, null)

        (activity as ReportActivity).prev_btn.setOnClickListener {
            (activity as ReportActivity).finish()
        }

        view.report_media_add_button.setOnClickListener {
            when (addedImagesCount) {
                0 -> {
                    addedImagesCount++
                    mImageView = view.report_media_00
                    dispatchTakePictureIntent()
                    mCreatedCase!!.media += mCurrentPhotoPath.getFileName()
                }
                1 -> {
                    addedImagesCount++
                    mImageView = view.report_media_01
                    dispatchTakePictureIntent()
                    mCreatedCase!!.media += mCurrentPhotoPath.getFileName()
                }
                2 -> {
                    addedImagesCount++
                    mImageView = view.report_media_02
                    dispatchTakePictureIntent()
                    mCreatedCase!!.media += mCurrentPhotoPath.getFileName()
                }
            }
        }

        return view
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
                        //...
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
                        startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
            //val imageStream = data
            val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
            mImageView.setImageBitmap(bitmap) // TODO picasso?
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.filesDir
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    /**
     * Helper function for extracting the filename to a given filepath
     **/
    private fun String.getFileName(): String {
        return this.substringAfterLast("/")
    }
}

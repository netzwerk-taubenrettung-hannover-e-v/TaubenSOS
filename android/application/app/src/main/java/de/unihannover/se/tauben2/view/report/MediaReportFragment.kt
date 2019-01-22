package de.unihannover.se.tauben2.view.report

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.deleteFile
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.loadMedia
import de.unihannover.se.tauben2.model.PicassoVideoRequestHandler
import de.unihannover.se.tauben2.view.InfoImageView
import de.unihannover.se.tauben2.view.RecordVideoActivity
import de.unihannover.se.tauben2.view.SquareImageView
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.fragment_report_media.view.*
import kotlinx.android.synthetic.main.phone_alert_dialog.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MediaReportFragment : ReportFragment() {

    private lateinit var v: View

    private lateinit var picassoInstance: Picasso

    init {
        pagePos = PagePos.FIRST
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 0
        private const val REQUEST_VIDEO_CAPTURE = 1
        private val LOG_TAG = MediaReportFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_report_media, container, false)

        context?.let {
            picassoInstance = Picasso.Builder(it.applicationContext)
                    .addRequestHandler(PicassoVideoRequestHandler()).build()
        }

        mCreatedCase.apply {
            phone = getViewModel(UserViewModel::class.java)?.getGuestPhone() ?: ""

            if (phone.isEmpty())
                setupPhonePermissions()
        }

        setBtnListener(R.id.fragment_report_location, null)

        (activity as ReportActivity).prev_btn.setOnClickListener {
            (activity as ReportActivity).finish()
        }

        val alertBuilder = AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.what_kind_of_media))
            setItems(arrayOf(getString(R.string.take_photo), getString(R.string.record_video))) { _, i ->
                if (mLocalMediaUrls.size > 3) {
                    setSnackBar(getString(R.string.maximum_reached))
                    return@setItems
                }
                when (i) {
                    0 -> dispatchTakeMediaIntent()
                    1 -> dispatchTakeMediaIntent(true)
                }
            }
        }

        v.report_media_add_button.setOnClickListener {
            alertBuilder.show()
        }

        createBlankImages(v)

        loadServerMedia()
        loadLocalMedia()

        return v
    }

    private fun setupPhonePermissions() {
        context?.let { cxt ->
            if (ContextCompat.checkSelfPermission(cxt, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
            else
                requestPhone(cxt)
        }
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        context?.let { cxt ->
            if (requestCode == 1) {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    requestPhone(cxt)
                else {
                    mCreatedCase.phone = (cxt.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).line1Number ?: ""
                    if (mCreatedCase.phone.isEmpty())
                        requestPhone(cxt)
                    else
                        getViewModel(UserViewModel::class.java)?.setGuestPhone(mCreatedCase.phone)
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun requestPhone(cxt: Context) {
        val alert = layoutInflater.inflate(R.layout.phone_alert_dialog, null)
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(cxt).setView(alert).show().apply {
            setCanceledOnTouchOutside(false)
            setOnCancelListener {
                activity?.finish()
            }
        }

        alert.apply {
            btn_okay.setOnClickListener {
                mCreatedCase.phone = edit_text_phone.text.toString()
                if (Patterns.PHONE.matcher(mCreatedCase.phone).matches()) {
                    getViewModel(UserViewModel::class.java)?.setGuestPhone(mCreatedCase.phone)
                    alertDialog.dismiss()
                } else
                    layout_edit_text_phone.error = "Phone number is not valid."
            }
            btn_cancel.setOnClickListener {
                alertDialog.cancel()
                activity?.finish()
            }
        }
    }

    private fun dispatchTakeMediaIntent(isVideo: Boolean = false) {

        val intentAction = if (isVideo) MediaStore.ACTION_VIDEO_CAPTURE else MediaStore.ACTION_IMAGE_CAPTURE
        val filePrefix = if (isVideo) "VIDEO" else "JPEG"
        val fileSuffix = if (isVideo) ".mp4" else ".jpg"
        val requestCode = if (isVideo) REQUEST_VIDEO_CAPTURE else REQUEST_IMAGE_CAPTURE

        Intent(intentAction).also { takeMediaIntent ->
            // Ensure that there's a camera activity to handle the intent
            activity?.packageManager?.let {
                takeMediaIntent.resolveActivity(it)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createMediaFile(filePrefix, fileSuffix)
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
                        if (isVideo) {
                            val videoIntent = Intent(context, RecordVideoActivity::class.java)
                            videoIntent.putExtra("url", file.absolutePath)
                            startActivityForResult(videoIntent, requestCode)
                        } else {
                            takeMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takeMediaIntent, requestCode)
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createMediaFile(prefix: String, suffix: String): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(Date())
        val storageDir: File? = context?.filesDir
        return File.createTempFile(
                "${prefix}_${timeStamp}_", /* prefix */
                suffix, /* suffix */
                storageDir /* directory */
        ).apply {
            mLocalMediaUrls.add(absolutePath.getFileName())
        }
    }

    // triggered after capturing a photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    compressImage(mLocalMediaUrls.last(), 80, 1000, 1000)
                }
                REQUEST_VIDEO_CAPTURE -> {

                }
            }
            loadLocalMedia()
        }
    }

    private fun loadLocalMedia(indexShift: Int = mCreatedCase.media.filter { !it.toDelete }.size) {
        mLocalMediaUrls.forEachIndexed { i, url ->
            if (v.image_layout.childCount <= i + indexShift)
                return@forEachIndexed

            val layout = (v.image_layout.getChildAt(i + indexShift) as InfoImageView)

            val image = layout.getChildAt(0) as SquareImageView
            val mediaLink = context?.getFileStreamPath(url)?.absolutePath

            val suffix = mediaLink?.split(".")?.last()

            if (suffix != "jpg") {
//                    MediaMetadataRetriever().apply {
//                        setDataSource(mediaLink, hashMapOf<String, String>())
//                    }
                picassoInstance.load(PicassoVideoRequestHandler.SCHEME_VIDEO + ":" + mediaLink)?.into(image)
                layout.setPlayable(true)
            } else {
                loadMedia(File(mediaLink), null, image, false)
                layout.setPlayable(false)
            }

//            image.setImageResource(R.drawable.ic_logo_48dp)
            layout.visibility = View.VISIBLE

        }
    }


    private fun loadServerMedia(indexShift: Int = 0) {

        mCreatedCase.media.filter { !it.toDelete }.forEachIndexed { i, media ->
            if (v.image_layout.childCount <= i + indexShift)
                return@forEachIndexed

            val layout = (v.image_layout.getChildAt(i + indexShift) as InfoImageView)
//            layout.visibility = View.INVISIBLE

            val image = layout.getChildAt(0) as SquareImageView

            mCreatedCase.loadMediaFromServerInto(media, image, null, false)

            if (media.getType().isVideo()) {
                layout.setPlayable(true)
//                    MediaMetadataRetriever().apply {
//                        setDataSource(mediaLink, hashMapOf<String, String>())
//                    }
//                picassoInstance.load(PicassoVideoRequestHandler.SCHEME_VIDEO + ":" + mediaLink)?.into(image)
            } else
                layout.setPlayable(false)


            layout.visibility = View.VISIBLE
        }
    }

    private fun deleteImage(image: SquareImageView) {
        for (i in 0 until v.image_layout.childCount) {
            val imageView = (v.image_layout.getChildAt(i) as InfoImageView).getChildAt(0) as SquareImageView
            imageView.setImageDrawable(null)
            if (imageView == image) {

                val notDeleteServerMedia = mCreatedCase.media.filter { !it.toDelete }.size
                if (notDeleteServerMedia > i) {
                    // Delete from Server
                    mCreatedCase.media[i].toDelete = true
                } else {
                    // Delete from local urls

                    // delete local file
                    val fileName = mLocalMediaUrls[i + notDeleteServerMedia]
                    context?.apply {
                        if (fileName.deleteFile(this))
                            Log.d(LOG_TAG, "Deleted $fileName successfully")
                        else Log.d(LOG_TAG, "Error: Couldn't delete $fileName")
                    }

                    mLocalMediaUrls.removeAt(i + notDeleteServerMedia)
                }
            }
        }
        loadServerMedia()
        loadLocalMedia()
    }

    private fun createBlankImages(view: View) {

        //  LinearLayout [
        //      3x ConstraintLayout [
        //          Image
        //          Button
        //      ]
        //  ]


        (0..2).forEach {

            //            val image = SquareImageView(view.context).apply {
//                id = View.generateViewId()
//                scaleType = ImageView.ScaleType.CENTER_CROP
//            }
//
//            val button = ImageButton(view.context).apply {
//                id = View.generateViewId()
//                setImageResource(R.drawable.ic_close)
////                setPadding(0,0,0,0)
//                background = ColorDrawable(Color.TRANSPARENT)
//                setOnClickListener {
//                    deleteImage(image)
//                }
//            }
//
//            val constraintLayout = ConstraintLayout(view.context).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        1.0f).apply {
//
//                    setMargins(2, 2, 2, 2)
//                }
//                addView(image)
//                addView(button)
//                visibility = View.INVISIBLE
//            }

            val infoImage = InfoImageView(view.context).apply {
                setClosable(true)
                visibility = View.INVISIBLE
                layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f).apply {

                    setMargins(4, 4, 4, 4)
                }
            }
            view.image_layout.addView(infoImage)

//            view.image_layout.addView(constraintLayout)
//
//            ConstraintSet().apply {
//                clone(constraintLayout)
//                connect(button.id, ConstraintSet.TOP, image.id, ConstraintSet.TOP)
//                connect(button.id, ConstraintSet.END, image.id, ConstraintSet.END)
//                applyTo(constraintLayout)
//            }

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
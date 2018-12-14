package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.view.MapViewFragment
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_report00.*
import kotlinx.android.synthetic.main.fragment_report00.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Report00Fragment : Fragment(), Observer<Location?> {


    private var mLocation: LatLng? = null
    private var mCreatedCase = Case.getCleanInstance()
    private val mMediaFilePaths: MutableList<String> = mutableListOf()

    private lateinit var mCurrentPhotoPath: String
    private lateinit var mImageView: ImageView
    private var addedImagesCount = 0

    companion object : Singleton<Report00Fragment>() {
        override fun newInstance() = Report00Fragment()

        private val LOG_TAG = Report00Fragment::class.java.simpleName
        val CREATED_CASE_KEY = Report00Fragment::class.java.simpleName + "createdCase"
        val MEDIA_PATHS_KEY = Report00Fragment::class.java.simpleName + "mediaPaths"
    }

    override fun onResume() {
        super.onResume()
        getViewModel(LocationViewModel::class.java)?.observeCurrentLocation(this, this)
    }

    override fun onPause() {
        super.onPause()
        getViewModel(LocationViewModel::class.java)?.stopObservingCurrentLocation(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report00, container, false)

        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition()
            mLocation = mapsFragment.getSelectedPosition()
        }

        view.report_media_add_button.setOnClickListener {
            when (addedImagesCount) {
                0 -> {
                    addedImagesCount++
                    mImageView = view.report_media_00
                    dispatchTakePictureIntent()
                    mMediaFilePaths.add(mCurrentPhotoPath)
                    mCreatedCase.media += mCurrentPhotoPath.getFileName()
                }
                1 -> {
                    addedImagesCount++
                    mImageView = view.report_media_01
                    dispatchTakePictureIntent()
                    mMediaFilePaths.add(mCurrentPhotoPath)
                    mCreatedCase.media += mCurrentPhotoPath.getFileName()
                }
                2 -> {
                    addedImagesCount++
                    mImageView = view.report_media_02
                    dispatchTakePictureIntent()
                    mMediaFilePaths.add(mCurrentPhotoPath)
                    mCreatedCase.media += mCurrentPhotoPath.getFileName()
                }
            }
        }

        view.report_next_step_button.setOnClickListener {

            if (!saveLocation()) {
                context?.let { c ->
                    report_map_title.setTextColor(ContextCompat.getColor(c, R.color.errorColor))
                    report_map_title.setError("")
                }
            } else {
                val bundle = Bundle()
                bundle.putParcelable(CREATED_CASE_KEY, mCreatedCase)
                bundle.putStringArrayList(MEDIA_PATHS_KEY, ArrayList(mMediaFilePaths))

                Log.d(LOG_TAG, "Passed $mCreatedCase to next Fragment")
                Log.d(LOG_TAG, "Passed $mMediaFilePaths to next Fragment")

                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report01Fragment, bundle)
            }
        }

        view.fullscreen_button.setOnClickListener {

            if (view.bottom_layout.visibility == View.VISIBLE) {
                view.bottom_layout.visibility = View.GONE
                view.fullscreen_button.setImageResource(R.drawable.ic_fullscreen_exit_black_24dp)
            } else {
                view.bottom_layout.visibility = View.VISIBLE
                view.fullscreen_button.setImageResource(R.drawable.ic_fullscreen_black_24dp)
            }
        }

        return view
    }

    override fun onChanged(loc: Location?) {
        loc ?: return
        // If location not set, set to current location
        if (mLocation == null)
            mLocation = LatLng(loc.latitude, loc.longitude)
    }

    /**
     * saves location to created case
     * @return true if successful
     */
    private fun saveLocation(): Boolean {
        mLocation?.let {
            mCreatedCase.longitude = it.longitude
            mCreatedCase.latitude = it.latitude
            return true
        }
        return false
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            val packageManager = activity?.packageManager
            packageManager.let {
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
                    photoFile?.also {
                        /*val photoURI= FileProvider.getUriForFile(
                                if(context!=null) context as Context else return,
                                "com.example.android.fileprovider",
                                it
                        )*/
                        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, 1)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data.extras.get("data") as Bitmap
            mImageView.setImageBitmap(imageBitmap)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
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
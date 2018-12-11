package de.unihannover.se.tauben2.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.text.InputType
import android.transition.TransitionManager
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCasesinfoBinding
import de.unihannover.se.tauben2.dimBehind
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_casesinfo.*
import kotlinx.android.synthetic.main.fragment_casesinfo.view.*
import java.text.SimpleDateFormat
import java.util.*


class CasesInfoFragment: Fragment(), Observer<Location?> {

    private lateinit var mBinding: FragmentCasesinfoBinding

    private var mCurrentAnimator: Animator? = null

    private var mShortAnimationDuration: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_casesinfo, container, false)
        arguments?.getParcelable<Case>("case")?.let {
            mBinding.c = it

            val media = it.media
            val views = listOf(mBinding.root.media_00_card, mBinding.root.media_01_card, mBinding.root.media_02_card)

            views.forEachIndexed { i, image ->
                Picasso.get().load(if(media.size >= i+1) media[i] else null)
                        .into(image)
                ZoomImage(image)
            }

        }

        mBinding.root.let{v->
            //convert injury into string list, convert it into a newline seperated string and add it to the textview
            val injuryList = mBinding.c?.injury?.toStringList() ?: listOf()
            var injuryString = ""
            //iteration counter to not add newline after last element
            var i=0
            injuryList.forEach { s ->
                injuryString+="$s"
                if(++i<injuryList.size){
                    injuryString+="\n"
                }
            }
            v.injury_card_value.text=injuryString;

            v.additional_information_card_textfield.text = mBinding.c?.additionalInfo

            //set date string
            val timestamp = mBinding.c?.timestamp
            if(timestamp!=null){
                val sdf = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
                val netDate = Date(timestamp*1000)
                var formattedDate = sdf.format(netDate)
                val sinceString = mBinding.c?.getSinceString()
                v.submission_time.text = formattedDate
                v.time_elapsed.text = "Vor $sinceString"

            }

            //set name of rescuer
            if(mBinding.c?.rescuer!=null){
                v.rescued_by.text=mBinding.c?.rescuer
            }
            else{
                v.rescued_by.text="Niemandem zugewiesen"
            }

            v.additional_information_edit_button.setOnClickListener{
                v.additional_information_card_textfield.isCursorVisible = true;
                v.additional_information_card_textfield.isFocusableInTouchMode = true;
                v.additional_information_card_textfield.inputType = InputType.TYPE_CLASS_TEXT;
                v.additional_information_card_textfield.requestFocus();
            }

            v.injury_edit_button.setOnClickListener {

                val inflater: LayoutInflater = layoutInflater
                val popupView = inflater.inflate(R.layout.popup_injury_edit, null)

                var focusable = true
                val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                popupWindow.isFocusable=true
                popupWindow.isOutsideTouchable=true
                popupWindow.setTouchInterceptor{_,event ->
                    if(event.action==MotionEvent.ACTION_OUTSIDE){
                        popupWindow.dismiss()
                        true
                    }

                    false
                }

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                TransitionManager.beginDelayedTransition(c_layout)
                popupWindow.showAtLocation(c_layout, Gravity.CENTER, 0, 0)
                popupWindow.dimBehind()
            }

        }

        return mBinding.root
    }

    fun ZoomImage(image: ImageView) {
        if(image.drawable!=null) {
            image.setOnClickListener {
                zoomImageFromThumb(image, image.drawable)
            }
            mShortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        }
    }

    private fun zoomImageFromThumb(thumbView: View, imageRes: Drawable) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        mCurrentAnimator?.cancel()

        // Load the high-resolution "zoomed-in" image.
        val expandedImageView: ImageView = expanded_image
        expandedImageView.setImageDrawable(imageRes)

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBoundsInt)
        c_layout.getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2f
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f
        expandedImageView.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.pivotX = 0f
        expandedImageView.pivotY = 0f

        c_layout2.visibility = View.GONE


        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        mCurrentAnimator = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left)).apply {
                with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
            }
            duration = mShortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    mCurrentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    mCurrentAnimator = null
                }
            })
            start()
        }

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        expandedImageView.setOnClickListener {
            mCurrentAnimator?.cancel()

            c_layout2.visibility = View.VISIBLE

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            mCurrentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
                }
                duration = mShortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        mCurrentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        thumbView.alpha = 1f
                        expandedImageView.visibility = View.GONE
                        mCurrentAnimator = null
                    }
                })
                start()
            }
        }
    }

    override fun onChanged(location: Location?) {
        if(location == null)
            return
        view?.let {
            mBinding.c?.let {case ->
                val caseLoc = Location("").apply {
                    latitude = case.latitude
                    longitude = case.longitude
                }
                val res = ((Math.round(location.distanceTo(caseLoc)/10))/100.0).toString() + " km"
                it.distance_text.text = res
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getViewModel(LocationViewModel::class.java)?.observeCurrentLocation(this, this)
    }

    override fun onPause() {
        super.onPause()
        getViewModel(LocationViewModel::class.java)?.stopObservingCurrentLocation(this)
    }
}

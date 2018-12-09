package de.unihannover.se.tauben2.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCasesinfoBinding
import de.unihannover.se.tauben2.model.entity.Case
import kotlinx.android.synthetic.main.fragment_casesinfo.*
import kotlinx.android.synthetic.main.fragment_casesinfo.view.*
import java.text.SimpleDateFormat
import java.util.*


class CasesInfoFragment: Fragment() {

    private var mCurrentAnimator: Animator? = null

    private var mShortAnimationDuration: Int = 0


    companion object : Singleton<CasesInfoFragment>() {
        override fun newInstance() = CasesInfoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCasesinfoBinding>(inflater, R.layout.fragment_casesinfo, container, false)
        arguments?.getParcelable<Case>("case")?.let {
            binding.c = it

            val media = it.media
            val views = listOf(binding.root.media_00_card, binding.root.media_01_card, binding.root.media_02_card)

            views.forEachIndexed { i, image ->
                Picasso.get().load(if(media.size >= i+1) media[i] else null)
                        .placeholder(R.drawable.ic_logo_48dp)
                        .into(image)
                ZoomImage(image)
            }
        }

        binding.root.let{v->
            //convert injury into string list and pass it to the list view
            /*val injuryList = binding.c?.injury?.toStringList() ?: listOf()
            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, injuryList)
            v.injury_card_value.adapter=adapter*/

            v.additional_information_card_textfield.text = binding.c?.additionalInfo

            //set date string
            val timestamp = binding.c?.timestamp
            if(timestamp!=null){
                val sdf = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
                val netDate = Date(timestamp*1000)
                var formattedDate = sdf.format(netDate)
                val sinceString = binding.c?.getSinceString()
                v.submission_time.text = formattedDate
                v.time_elapsed.text = "Vor $sinceString)"

            }

            //TODO: Automatically scale height of injury_card_value and additional_information_card_textfield based on number of injuries/text length
            /*val params = v.injury_card_value.layoutParams
            params.height = injuryList.size*50
            v.injury_card_value.layoutParams = params*/
        }

        return binding.root
    }

    fun ZoomImage(image: ImageView) {
        image.setOnClickListener {
            zoomImageFromThumb(image, image.drawable)
        }
        mShortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
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


        context?.let { c ->
            c_layout.setBackgroundColor(ContextCompat.getColor(c, R.color.common_google_signin_btn_text_dark_focused))
            c_layout2.setBackgroundColor(ContextCompat.getColor(c, R.color.common_google_signin_btn_text_dark_focused))
        }

        media_card_layout.visibility = View.GONE
        accept_button.visibility = View.GONE
        //status_card_image_info.visibility = View.GONE

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        mCurrentAnimator = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left)).apply {
                with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.centerY(), finalBounds.centerY() - imageRes.intrinsicHeight/2))
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

            context?.let { c ->
                c_layout.setBackgroundColor(ContextCompat.getColor(c, R.color.design_default_color_background))
                c_layout2.setBackgroundColor(ContextCompat.getColor(c, R.color.design_default_color_background))
            }

            media_card_layout.visibility = View.VISIBLE
            accept_button.visibility = View.VISIBLE
            //status_card_image_info.visibility = View.VISIBLE

            //expanded_image_background.visibility = View.GONE

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            mCurrentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.centerY()))
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

}

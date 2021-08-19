package company.coutloot.common.xtensions

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import devx.app.licensee.R
import devx.app.licensee.common.AnimUtils
import devx.app.licensee.common.CircleProgressBarDrawable
import devx.app.licensee.common.utils.HelperMethods

//viewextensions
fun View?.show() {
    this?.visibility = View.VISIBLE
}
fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun ViewGroup?.show() {
    this?.visibility = View.VISIBLE
}

fun ViewGroup?.gone() {
    this?.visibility = View.GONE
}

fun View?.panWithCallback(animationListener: Animation.AnimationListener) {
    val anim = ScaleAnimation(
        1f, 0.8f,
        1f, 0.8f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.fillAfter = true
    anim.duration = 70

    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
            val anim1 = ScaleAnimation(
                0.8f, 1f,
                0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim1.fillAfter = true
            anim1.duration = 70
            anim1.setAnimationListener(animationListener)
            this@panWithCallback?.startAnimation(anim1)
        }

        override fun onAnimationRepeat(animation: Animation) {

        }
    })

    this?.startAnimation(anim)
}

fun View?.pan() {
    val anim = ScaleAnimation(
        1f, 0.8f,
        1f, 0.8f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.fillAfter = true
    anim.duration = 100

    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
            val anim1 = ScaleAnimation(
                0.8f, 1f,
                0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim1.fillAfter = true
            anim1.duration = 100
            this@pan?.startAnimation(anim1)
        }

        override fun onAnimationRepeat(animation: Animation) {

        }
    })

    this?.startAnimation(anim)
}

fun ViewGroup?.panWithCallback(animationListener: Animation.AnimationListener) {
    val anim = ScaleAnimation(
        1f, 0.8f,
        1f, 0.8f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.fillAfter = true
    anim.duration = 100

    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
            val anim1 = ScaleAnimation(
                0.8f, 1f,
                0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim1.fillAfter = true
            anim1.duration = 100
            anim1.setAnimationListener(animationListener)
            this@panWithCallback?.startAnimation(anim1)
        }

        override fun onAnimationRepeat(animation: Animation) {

        }
    })

    this?.startAnimation(anim)
}

fun ViewGroup?.pan() {
    val anim = ScaleAnimation(
        1f, 0.8f,
        1f, 0.8f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    )
    anim.fillAfter = true
    anim.duration = 200

    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {

        }

        override fun onAnimationEnd(animation: Animation) {
            val anim1 = ScaleAnimation(
                0.8f, 1f,
                0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            anim1.fillAfter = true
            anim1.duration = 200
            this@pan?.startAnimation(anim1)
        }

        override fun onAnimationRepeat(animation: Animation) {

        }
    })

    this?.startAnimation(anim)
}

fun SimpleDraweeView?.loadImage(
    imageUrl: String,
    tapToRetry: Boolean = false,
    hasPlaceHolder: Boolean = false
) {

    val controller = Fresco.newDraweeControllerBuilder()
        .setUri(Uri.parse(imageUrl))
        .setOldController(this@loadImage?.controller)
        .setTapToRetryEnabled(tapToRetry)
        .setAutoPlayAnimations(true)
        .build()
    if (hasPlaceHolder) {
        this@loadImage?.hierarchy?.setPlaceholderImage(HelperMethods.getGreyDrawable(context))
    }
    this@loadImage?.hierarchy?.setProgressBarImage(CircleProgressBarDrawable())
    this@loadImage?.controller = controller
}

fun SimpleDraweeView?.loadSmallImage(
    imageUrl: String,
    width: Int = 100,
    height: Int = 100,
    tapToRetry: Boolean = false,
    hasPlaceHolder: Boolean = false
) {
    val imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
    if (width > 0 && height > 0) {
        imageRequestBuilder.setResizeOptions(ResizeOptions(width, height))
    }
    val imageRequest = imageRequestBuilder.build()
    val controller = Fresco.newDraweeControllerBuilder()
        .setImageRequest(imageRequest)
        .setOldController(this@loadSmallImage?.controller)
        .setTapToRetryEnabled(tapToRetry)
        .setAutoPlayAnimations(true)
        .build()
    if (hasPlaceHolder) {
        this@loadSmallImage?.hierarchy?.setPlaceholderImage(HelperMethods.getGreyDrawable(context))
    }
    this@loadSmallImage?.hierarchy?.setProgressBarImage(CircleProgressBarDrawable())
    this@loadSmallImage?.controller = controller
}


fun View?.onclickPanOnTouch(onClickListener: View.OnClickListener) {
    this@onclickPanOnTouch?.setOnTouchListener { _, motionEvent ->
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                AnimUtils.touchStart(this)
                return@setOnTouchListener true
            }
            MotionEvent.ACTION_UP -> {
                AnimUtils.touchEnd(this, onClickListener)
                return@setOnTouchListener true
            }
            else -> false
        }

    }
}

fun RecyclerView?.setLayAnimation(resId: Int = R.anim.layout_animation_falldown) {
    if (this@setLayAnimation != null) {
        HelperMethods.setRecyclerAnim(this, this.context, resId)
    }
}

fun RelativeLayout?.setLayAnimation(resId: Int = R.anim.layout_animation_falldown1) {
    if (this@setLayAnimation != null) {
        HelperMethods.setRelativeLayAnim(this, this.context, resId)
    }
}



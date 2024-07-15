package com.itsmejcb.nsfwvalidator.Data.Block

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import java.net.URL
import java.util.UUID
import java.util.regex.Pattern
import com.itsmejcb.nsfwvalidator.R

class Helper(private val context: Context) {


    fun loadImage(
        url: String,
        imageView: ImageView,
        defaultImage: Drawable,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        Picasso.get()
            .load("Access.URL_PULL_ZONE_POSTurl")
            .error(defaultImage)
            .into(imageView, object : Callback {
                var retryCount = 0

                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    if (retryCount < 3) {
                        retryCount++
                        loadImage(url, imageView, defaultImage, progressBar)
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            })
    }

    fun loadProfile(
        url: String,
        imageView: ImageView,
        defaultImage: Drawable
    ) {
        Picasso.get()
            .load("Access.URL_PULL_ZONE_POSTurl")
            .error(defaultImage)
            .transform(CropCircleTransformation())
            .into(imageView, object : Callback {
                var retryCount = 0

                override fun onSuccess() {
                }

                override fun onError(e: Exception?) {
                    if (retryCount < 3) {
                        retryCount++
                        loadProfile(url, imageView, defaultImage)
                    }
                }
            })
    }

    fun loadBlur(url: String?, post: ImageView?, card: CardView?, progressbar: ProgressBar?) {
        val thread = Thread {
            try {
                val link = URL("Access.URL_PULL_ZONE_POST+url")
                val myBitmap = BitmapFactory.decodeStream(link.openConnection().getInputStream())

                // Apply the blur effect to the Bitmap
                val bl = BlurredBitmap()
                val a = bl.fastBlur(myBitmap, 1.0f, 75)

                // Update the ImageView on the UI thread
                post?.post {
                    post?.setImageBitmap(a)
                    // holder.srfpl_post_blur.setImageBitmap(a);
                    // holder.srfpl_post_blur.setVisibility(View.GONE);
                    // Now that the image is set, you can make the views visible
                    card?.visibility = View.VISIBLE
                    progressbar?.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    fun genUUID(): String? {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    // capitalize per words
    private fun capitalizeWords(input: String): String {
        val tokens = input.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val formattedString = StringBuilder()
        for (token in tokens) {
            if (token.isNotEmpty()) {
                val firstChar = token[0].uppercaseChar()
                formattedString.append(" ").append(firstChar).append(token.substring(1))
            }
        }
        return formattedString.toString().trim { it <= ' ' }
    }

    // to load the progressbar while processing the data
    fun loading(view1: TextView, view2: ProgressBar, condition: Boolean) {
        view1.visibility = if (condition) View.GONE else View.VISIBLE
        view2.visibility = if (condition) View.VISIBLE else View.GONE
    }

    fun isValid(key: String): Boolean {
        return key.length in 2..19
        // return when {
        //     key.length < 2 -> false
        //     key.length in 2..19 -> true
        //     key.length > 20 -> false
        //     else -> false // This handles any other cases not specified explicitly
        // }
    }

    fun isValidPassword(key: String): Boolean {
        return when {
            key.length < 7 -> false
            key.length in 7..19 -> true
            key.length > 20 -> false
            else -> false // This handles any other cases not specified explicitly
        }
    }

    fun validatePassword(key1: String, key2: String): Boolean {
        return key1 != key2
    }

    fun validate(key: String, num: Int): Boolean {
        val patterns = mapOf(
            1 to "${Patterns.EMAIL_ADDRESS}",
            2 to "${"KEY.NAME_PATTERN"}"
        )
        val pattern = patterns[num]?.toRegex()
        val emailPattern = Pattern.compile(pattern.toString())
        val matcher1 = emailPattern.matcher(key)
        return matcher1.matches()
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        val matcher = emailPattern.matcher(email)
        return matcher.matches()
    }

    // to intent the activity with animation
    fun intent(targetActivityClass: Class<*>, enterAnimationResId: Int, exitAnimationResId: Int) {
        val i = Intent(context, targetActivityClass)
        if (context is Activity) {
            val options = ActivityOptions.makeCustomAnimation(
                context,
                enterAnimationResId,
                exitAnimationResId
            )
            context.startActivity(i, options.toBundle())
        } else {
            context.startActivity(i)
        }
    }

    fun hideKeyboard(context: Context) {
        val view = (context as Activity).currentFocus
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showMessage(context: Context?, s: String?) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
    }

    fun description(
        context: Context?,
        username: String?,
        fullName: String?,
        description: String?,
        url: String?
    ) {
        val spotdDialog = BottomSheetDialog(context!!)
        val spotdView: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_description, null)
        spotdDialog.setContentView(spotdView)
        val usernameTextView = spotdView.findViewById<TextView>(R.id.spd_username)
        usernameTextView.text = username
        val fullNameTextView = spotdView.findViewById<TextView>(R.id.spd_name)
        fullNameTextView.text = fullName
        val descriptionTextView = spotdView.findViewById<TextView>(R.id.spd_caption)
        // descriptionTextView.setText(description);
        val spd_profile = spotdView.findViewById<ImageView>(R.id.spd_profile)
        if (url != null) {
            // Picasso.get()
            //     .load(Access.URL_PULL_ZONE_PROFILE + url)
            //     .transform(CropCircleTransformation())
            //     .placeholder(R.drawable.img_logo)
            //     .into(spd_profile)
        } else {
            // Picasso.get()
            //     .load(R.drawable.img_logo)
            //     .transform(CropCircleTransformation())
            //     .placeholder(R.drawable.img_logo)
            //     .into(spd_profile)
        }
        // ScrollView scrollView = spotdView.findViewById(R.id.spd_vscroll);
        // scrollView.setVerticalScrollBarEnabled(false);
        // Use the previously created instance of HighlightedTextView to set text with highlightsnew HighlightedTextView(context).setTextWithHighlights(descriptionTextView, description, 25,false);
        // HighlightedTextView(context).setTextWithHighlights(
        //     descriptionTextView,
        //     description,
        //     0,
        //     false
        // )
        spotdDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        spotdDialog.setCancelable(true)
        spotdDialog.show()
    }

}
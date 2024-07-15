package com.itsmejcb.nsfwvalidator.Class.Upload

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.itsmejcb.nsfwvalidator.Data.Adapter.ImageAdapter
import com.itsmejcb.nsfwvalidator.Data.Block.Dialog
import com.itsmejcb.nsfwvalidator.Data.Block.Helper
import com.itsmejcb.nsfwvalidator.Data.Model.GlobalData
import com.itsmejcb.nsfwvalidator.Data.Model.Image
import com.itsmejcb.nsfwvalidator.Data.SharedPreference.SharedPreferencesUtils
import com.itsmejcb.nsfwvalidator.Data.SharedPreference.SharedPreferencesUtils.loadImageArray
import com.itsmejcb.nsfwvalidator.R
import com.itsmejcb.nsfwvalidator.Class.Select.Select
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Calendar
import java.util.regex.Pattern

class Upload : AppCompatActivity(), Interface.View {
    private var object_clicked = ""
    private var imageAdapter: ImageAdapter? = null
    private lateinit var presenter: Presenter
    private lateinit var helper: Helper
    private lateinit var dialog: Dialog
    private var scp_recyclerview: RecyclerView? = null
    private var scp_post: TextView? = null
    private var scp_profile: ImageView? = null
    private var scp_add: ImageView? = null
    private var scp_back: ImageView? = null
    private var scp_setting: ImageView? = null
    private var scp_caption: EditText? = null
    private var scp_username: TextView? = null
    private var scp_length: TextView? = null
    private var scp_full_name: TextView? = null
    private var scp_create_post: TextView? = null
    private var imgs = ArrayList<Image>()
    private val characterLimit = 500
    private var imageLabeler: ImageLabeler? = null
    private var alertDialog: AlertDialog? = null
    private val cal = Calendar.getInstance()
    private val ms = cal.timeInMillis.toString()
    private val STORAGE_PERMISSION_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        initialize(savedInstanceState)
        event(savedInstanceState)
        initializeLogic()
        imageLabeler = ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
        )
    }

    private fun initialize(savedInstanceState: Bundle?) {
        presenter = Presenter(this, this)
        helper = Helper(this)
        dialog = Dialog(this)
        scp_recyclerview = findViewById<RecyclerView>(R.id.scp_recyclerview)
        scp_post = findViewById<TextView>(R.id.scp_post)
        scp_profile = findViewById<ImageView>(R.id.scp_profile)
        scp_caption = findViewById<EditText>(R.id.scp_caption)
        scp_length = findViewById<TextView>(R.id.scp_length)
        scp_add = findViewById<ImageView>(R.id.scp_add)
        scp_setting = findViewById<ImageView>(R.id.scp_setting)
        scp_username = findViewById<TextView>(R.id.scp_username)
        scp_create_post = findViewById<TextView>(R.id.scp_create_post)
        scp_full_name = findViewById<TextView>(R.id.scp_full_name)
        scp_back = findViewById<ImageView>(R.id.scp_back)
    }

    private fun event(savedInstanceState: Bundle?) {
        // Add any event listeners here if needed
    }

    private fun containsNSFW(arrayList: ArrayList<Image>): Boolean {
        for (image in arrayList) {
            if (image.status == "nsfw") {
                return true // If any image is NSFW, return true
            }
        }
        return false // If no image is NSFW, return false
    }

    override fun getFileExtension(filePath: String?): String? {
        return if (filePath != null && filePath.lastIndexOf(".") != -1) {
            "." + filePath.substring(filePath.lastIndexOf(".") + 1)
        } else {
            "" // No file extension found
        }
    }

    fun loadImage(view: RecyclerView?, images: ArrayList<Image>?) {
        imageAdapter = ImageAdapter(this@Upload, images!!)
        view?.adapter = imageAdapter
        val layoutManager =
            LinearLayoutManager(view?.context, LinearLayoutManager.HORIZONTAL, false)
        view?.layoutManager = layoutManager
        view?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val previousVisibleItem = intArrayOf(0)
            var swipeCount = intArrayOf(0)
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = intArrayOf(layoutManager.childCount)
                val totalItemCount = intArrayOf(layoutManager.itemCount)
                val firstVisibleItem = intArrayOf(layoutManager.findFirstVisibleItemPosition())
                if (visibleItemCount[0] > previousVisibleItem[0]) {
                    swipeCount[0]++
                    Log.d("Swipe count", "Swipe count FORWARD: " + swipeCount[0])
                }
                previousVisibleItem[0] = visibleItemCount[0]
            }
        })
        val animation: Animation = AnimationUtils.loadAnimation(view?.context, R.anim.fade_in)
        view?.startAnimation(animation)
        // Remove the current OnFlingListener before setting a new one
        if (view?.onFlingListener != null) {
            view.onFlingListener = null
        }

        // Create a new instance of OnFlingListener and set it on the RecyclerView
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(view)
    }

    private fun initializeLogic() {
        imgs = loadImageArray(applicationContext, "list_photos")
        scp_caption?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Not needed for character limit enforcement
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Not needed for character limit enforcement
            }

            override fun afterTextChanged(editable: Editable) {
                val currentLength = editable.length
                scp_length?.text = "$currentLength/$characterLimit"
                // Check if the length of the text exceeds the character limit
                if (editable.length > characterLimit) {
                    // If it exceeds the limit, truncate the text
                    scp_caption?.setText(editable.subSequence(0, characterLimit))
                    scp_caption?.setSelection(characterLimit) // Move cursor to the end
                }
            }
        })
        scp_add?.setOnClickListener {
            if (isStoragePermissionGranted()) {
                // Permission is already granted, proceed with your functionality
                Log.d("working","working")
                openImageSelector()
            } else {
                requestStoragePermission()
            }
        }

        scp_setting?.setOnClickListener {
            openURLInChromeCustomTab("https://github.com/itsmejcb/NSFWValidator.git")
        }

        scp_post?.setOnClickListener(View.OnClickListener {
            val arrayList = imgs
            if (containsNSFW(arrayList)) {
                val dialogView: View = LayoutInflater.from(this)
                    .inflate(R.layout.custom_dialog_message, null)
                val dialogMessageTextView =
                    dialogView.findViewById<TextView>(R.id.sdm_message)
                val message =
                    "The terms of service of the social media platform in question, as well as any applicable laws and regulations, will usually govern punishments for publishing sexual content. Depending on the seriousness of the offense and the rules of the platform, these penalties may differ. Learn more "
                textviewMh(dialogMessageTextView, message)
                val builder =
                    AlertDialog.Builder(this)
                builder.setTitle("Warning")
                    .setView(dialogView)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        // Handle OK button click
                        dialog.dismiss() // Close the dialog
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which ->
                        // Handle Cancel button click
                        dialog.dismiss() // Close the dialog
                    }
                alertDialog = builder.create()
                alertDialog!!.show()
                Log.i(
                    "ArrayList Status",
                    "This ArrayList contains NSFW images."
                )
            } else {
                Log.i(
                    "ArrayList Status",
                    "This ArrayList contains only SFW images."
                )

                for (image in imgs) {

                    Log.i(
                        "ArrayList data",
                        image.image_name.toString() + "\n"
                                + image.path.toString() + "\n"
                                + image.size
                    )
                }
                // val request = Request()
                // request.apply {
                //     val imageNamesBuilder1 = StringBuilder()
                //     val imageNamesBuilder2 = StringBuilder()
                //     for (image in imgs) {
                //         if (imageNamesBuilder1.isNotEmpty()) {
                //             imageNamesBuilder1.append(",")
                //         }
                //         if (imageNamesBuilder2.isNotEmpty()) {
                //             imageNamesBuilder2.append(",")
                //         }
                //         imageNamesBuilder1.append(image.image_name)
                //         imageNamesBuilder2.append(image.status)
                //
                //     }
                //     // uid = SharedPreferencesUtils.loadString(this@Upload, KEY.UID, "")
                //     // caption = scp_caption?.text.toString()
                //     // imageName = imageNamesBuilder1.toString()
                //     // imageStatus = imageNamesBuilder2.toString()
                //     // timestamp = ms
                //     // status = SharedPreferencesUtils.loadString(this@Upload, KEY.SET_POST_STATUS, "")
                //     // commentStats = SharedPreferencesUtils.loadString(this@Upload, KEY.SET_COMMENT_STATUS, "")
                // }
                // viewModel.userCreatePost(request)
                // Log.d("ResponseDebug", request.toString())

                // presenter.PostContent(scp_caption.getText().toString(), ms, imgs)
            }
        })
        if (imgs.size > 0) {
            loadImage(scp_recyclerview, imgs)
        }
    }

    private fun openURLInChromeCustomTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.charlestonGreen))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
    private fun openImageSelector() {
        helper.intent(Select::class.java, R.anim.right_in, R.anim.left_out)
    }

    private fun isStoragePermissionGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return permission == PackageManager.PERMISSION_GRANTED
    }
    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an explanation to the user
            AlertDialog.Builder(this)
                .setTitle("Storage Permission Needed")
                .setMessage("This app needs the Storage permission to function properly")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                }
                .create()
                .show()
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }


    fun textviewMh(txt: TextView, value: String?) {
        txt.movementMethod = LinkMovementMethod.getInstance()
        updateSpan(value.toString(), txt)
    }

    private fun updateSpan(str: String, txt: TextView) {
        val ssb = SpannableString(str)
        val pattern =
            Pattern.compile("(?<![^\\s])(([Terms]{5}|[Cookies Policy]{14}|[Data Policy]{11}|[Sign In]{7})(\\.?)+)(?![^\\s,])")
        val matcher = pattern.matcher(str)
        while (matcher.find()) {
            val span = ProfileSpan()
            ssb.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        txt.text = ssb
    }

    inner class ProfileSpan : ClickableSpan() {
        override fun onClick(view: View) {
            if (view is TextView) {
                val tv = view
                if (tv.text is Spannable) {
                    val sp = tv.text as Spannable
                    val start = sp.getSpanStart(this)
                    val end = sp.getSpanEnd(this)
                    object_clicked = sp.subSequence(start, end).toString()
                    if (object_clicked == "Sign In") {
                        // helper.intent(SignIn::class.java, R.anim.left_in, R.anim.right_out)
                        // i.setClass(this@Email, LoggedInto::class.java)
                        // startActivity(i)
                        // overridePendingTransition(R.anim.left_in, R.anim.right_out)
                    }
                    if (object_clicked == "Terms") {
                        // block.description(this@Email, "Terms", "developer team", terms, "")
                    }
                    if (object_clicked == "Data Policy") {
                        // block.description(this@Email, "Data Policy", "Developer team", data, "")
                    }
                    if (object_clicked == "Cookies Policy") {
                        // block.description(
                        //     this@Email,
                        //     "Cookies Policy",
                        //     "Developer team",
                        //     cookies,
                        //     ""
                        // )
                    }
                }
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.color = Color.parseColor("#FFD700")
            ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }

    override fun dialogHandling(num: Int) {
        when (num) {

            1 -> dialog.showDialog(
                "Warning",
                "Your file is to large.\n" +
                        " Maximum 5mb per upload.",
                "Ok",
                ""
            )

            1 -> dialog.showDialog(
                "Warning",
                "Sorry, we encountered an issue while uploading your photo. Please make sure the file you are attempting to upload meets the following requirements:\n" +
                        "\"• Supported Formats: We only accept JPEG and PNG files.\n" +
                        "\"• File Size: The file size should not exceed 5MB.",
                "Ok",
                ""
            )

            else -> dialog.showDialog(
                "Warning",
                "An error occurred. Please try again.",
                "Ok",
                "Learn more"
            )
        }
    }
}
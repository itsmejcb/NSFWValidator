package com.itsmejcb.nsfwvalidator.Class.Select

import android.Manifest
import com.itsmejcb.nsfwvalidator.R
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.itsmejcb.nsfwvalidator.Data.Adapter.PhotosAdapter
import com.itsmejcb.nsfwvalidator.Data.Block.Dialog
import com.itsmejcb.nsfwvalidator.Data.Block.Helper
import com.itsmejcb.nsfwvalidator.Data.Grid.GridSpacing
import com.itsmejcb.nsfwvalidator.Data.Model.GlobalData
import com.itsmejcb.nsfwvalidator.Data.Model.Image
import com.itsmejcb.nsfwvalidator.Data.SharedPreference.SharedPreferencesUtils
import com.itsmejcb.nsfwvalidator.Class.Upload.Upload
import java.util.Calendar


class Select : AppCompatActivity(), PhotosAdapter.ImageListener {
    private var recyclerView: RecyclerView? = null
    private var adapter: PhotosAdapter? = null
    private var txt_next: TextView? = null
    private var sip_label: TextView? = null
    private var folderSpinner: Spinner? = null
    private var sip_layout_1: LinearLayout? = null
    private var sip_allow_btn: LinearLayout? = null
    private var ssip_back: ImageView? = null
    private var renameCount = 1
    private var numCount = 0
    private var imageLabeler: ImageLabeler? = null
    private val cal = Calendar.getInstance()
    private lateinit var helper: Helper
    private lateinit var alertDialog: AlertDialog
    private lateinit var dialog: Dialog
    private val imgs = java.util.ArrayList<Image>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_select_image)
        initialize(savedInstanceState)
        imageLabeler = ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
        )
        val savedItems = SharedPreferencesUtils.loadImageArray(this, "list_photos")

    }

    private fun initialize(savedInstanceState: Bundle?) {
        helper = Helper(this)
        dialog = Dialog(this)
        recyclerView = findViewById(R.id.ssip_recycler_view)
        txt_next = findViewById(R.id.ssip_txt_next)
        folderSpinner = findViewById(R.id.ssip_folder_spinner)
        sip_layout_1 = findViewById(R.id.ssip_layout_1)
        sip_label = findViewById(R.id.ssip_label)
        sip_allow_btn = findViewById(R.id.ssip_allow_btn)
        ssip_back = findViewById(R.id.ssip_back)
        txt_next?.visibility = View.GONE
        ssip_back?.setOnClickListener {
            if (numCount != 0){
                dialog.showDialog(
                    "Opps",
                    "Are you sure you want to discard this post?",
                    "Continue editing",
                    "Discard"
                )
                return@setOnClickListener
            }
            onBackPressed()
            overridePendingTransition(R.anim.left_in, R.anim.right_out)

        }
        adapter = PhotosAdapter(ArrayList(), this)
        recyclerView.apply {
            this?.layoutManager = LinearLayoutManager(this@Select)
            adapter = this@Select.adapter
        }
        // val savedItems = SharedPreferencesUtils.loadImageArray(this, "list_photos")
        //         if (savedItems.size>0){
        //             Log.i("data status ", "array > 0 success")
        //         }
        //         Log.i("test",savedItems.toString())
        //         // this is my loaded item that i want to visible the scsii_image_check in may selected item here match in the list image in adapter
        //         adapter = PhotosAdapter(savedItems, this)
        //         recyclerView?.adapter = adapter
        // Populate adapter with images (replace this with your image list)
        val imageList = SharedPreferencesUtils.loadImageArray(applicationContext, "list_photos")
        adapter?.setImageList(imageList)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this@Select,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1000
            )
            return
        }
        initializeLogic()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLogic()
            } else {
                // Handle permission denied
                sip_layout_1!!.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
                sip_label!!.text = "Allow Spotd access to your internal and external photo"
            }
        }
    }

    override fun onImageSelected(selectedImages: ArrayList<Image>) {
        if (selectedImages.isNotEmpty()) {
            try {
                numCount = selectedImages.size
            }catch (e:Exception){
                throw RuntimeException(e)
            }
            txt_next!!.visibility = View.VISIBLE
            txt_next!!.text = selectedImages.size.toString() + "/1 Next"
            for (selectedImage in selectedImages) {
                val bitmap = loadBitmapFromPath(selectedImage.path)
                selectedImage.bitmap = bitmap
                if (imageLabeler != null) {
                    val inputImage = InputImage.fromBitmap(selectedImage.bitmap!!, 0)
                    imageLabeler!!.process(inputImage)
                        .addOnSuccessListener { imageLabels: List<ImageLabel> ->
                            // Handle the detected labels
                            val sb = StringBuilder()
                            for (label in imageLabels) {
                                sb.append(label.text).append(": ").append(label.confidence)
                                    .append("\n")
                            }
                            if (sb.toString().contains("Flesh") || sb.toString().contains("Skin")) {
                                selectedImage.status = "nsfw"
                                Log.i("status1", "nsfw")
                            } else {
                                selectedImage.status = "sfw"
                                Log.i("status2", "sfw")
                            }
                        }.addOnFailureListener { e: Exception -> e.printStackTrace() }
                } else {
                    // Handle the case where imageLabeler is null, but there's no need to change res[0]
                    Log.i("status", "ImageLabeler is null")
                }
            }
            txt_next!!.setOnClickListener { view: View? ->
                renameCount = 1
                for (selectedImagea in selectedImages) {
                    // Rename the selected image by adding a number to its name
                    val originalName =
                        cal.timeInMillis.toString() + SharedPreferencesUtils.loadString(
                            this@Select,
                            "KEY.UID",
                            ""
                        )
                    val extension = selectedImagea.extension
                    val renamedName = "$originalName$renameCount$extension"
                    // Update the image name
                    selectedImagea.image_name = renamedName
                    renameCount++
                }
                SharedPreferencesUtils.saveImageArray(this@Select, "list_photos", selectedImages)
                GlobalData.getInstance().dataList = selectedImages
                helper.intent(Upload::class.java, R.anim.left_in, R.anim.right_out)
                Log.i(
                    "status",
                    "working"
                )
                Log.i(
                    "status ip",
                    SharedPreferencesUtils.loadImageArray(this@Select, "list_photos").toString()
                )
            }
        } else {
            txt_next!!.visibility = View.GONE
            numCount = 0
        }
    }

    private fun loadBitmapFromPath(imagePath: String?): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // You can adjust the sample size based on your needs
        return BitmapFactory.decodeFile(imagePath, options)
    }

    private fun initializeLogic() {
        val folders = distinctFolders
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folders)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        folderSpinner!!.adapter = spinnerAdapter
        val spanCount = 3 // Number of columns in the grid
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing) // Spacing in pixels
        val includeEdge = true // Whether to include spacing at the edges
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager = GridLayoutManager(this, spanCount)
        recyclerView!!.addItemDecoration(GridSpacing(this, spanCount, spacing, includeEdge))
        folderSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedFolder = parent.getItemAtPosition(position).toString()
                if (selectedFolder == "All") {
                    if (allImageInfo.isEmpty()) {
                        // Show a toast message if the ArrayList is empty
                        Toast.makeText(this@Select, "No images found", Toast.LENGTH_SHORT).show()
                        sip_label!!.text = "No images found"
                        sip_layout_1!!.visibility = View.VISIBLE
                        recyclerView!!.visibility = View.GONE
                        sip_allow_btn!!.visibility = View.GONE
                    } else {
                        sip_layout_1!!.visibility = View.GONE
                        recyclerView!!.visibility = View.VISIBLE
                        val allImageUris: ArrayList<Image> = allImageInfo
                        adapter!!.setImageList(allImageUris) // Update the adapter with new data
                    }
                } else {
                    val filteredImageUris = getImagesInFolderInfo(selectedFolder)
                    adapter!!.setImageList(filteredImageUris) // Update the adapter with new data
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle nothing selected if needed
            }
        }
    }

    val allImageInfo: ArrayList<Image>
        get() {
            val imageInfoList = ArrayList<Image>()
            val contentResolver = contentResolver
            val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,  // Image data path
                MediaStore.Images.Media.SIZE // Image size
            )
            val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
            val cursor = contentResolver.query(imageUri, projection, null, null, sortOrder)
            if (cursor != null) {
                val dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
                while (cursor.moveToNext()) {
                    val imagePath = cursor.getString(dataIndex)
                    val imageSize = cursor.getLong(sizeIndex)

                    // Extract image extension from the data path
                    val imageExtension = imagePath.substring(imagePath.lastIndexOf("."))
                    val imageInfo = Image()
                    imageInfo.path = imagePath
                    imageInfo.size = imageSize.toString()
                    imageInfo.extension = imageExtension
                    imageInfoList.add(imageInfo)
                }
                cursor.close()
            }
            return imageInfoList
        }
    val distinctFolders: List<String>
        get() {
            val folders: MutableList<String> = ArrayList()
            val contentResolver = contentResolver
            val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
            val cursor = contentResolver.query(imageUri, projection, null, null, sortOrder)
            if (cursor != null) {
                val folderIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val folderName = cursor.getString(folderIndex)
                    if (!folders.contains(folderName)) {
                        folders.add(0, "All") // Add "All" option to the beginning
                        folders.add(folderName)
                    }
                }
                cursor.close()
            }
            return folders
        }

    fun getImagesInFolderInfo(folderName: String): ArrayList<Image> {
        val imageInfoList = ArrayList<Image>()
        val contentResolver = contentResolver
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,  // Image data path
            MediaStore.Images.Media.SIZE // Image size
        )
        val selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?"
        val selectionArgs = arrayOf(folderName)
        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val cursor =
            contentResolver.query(imageUri, projection, selection, selectionArgs, sortOrder)
        if (cursor != null) {
            val dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
            while (cursor.moveToNext()) {
                val imagePath = cursor.getString(dataIndex)
                val imageSize = cursor.getLong(sizeIndex)

                // Extract image extension from the data path
                val imageExtension = imagePath.substring(imagePath.lastIndexOf("."))
                val imageInfo = Image()
                imageInfo.path = imagePath
                imageInfo.size = imageSize.toString()
                imageInfo.extension = imageExtension
                imageInfoList.add(imageInfo)
            }
            cursor.close()
        }
        return imageInfoList
    }
}
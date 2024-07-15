package com.itsmejcb.nsfwvalidator.Data.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itsmejcb.nsfwvalidator.Data.Model.Image
import com.itsmejcb.nsfwvalidator.R

import com.squareup.picasso.Picasso


class PhotosAdapter(
    private var imageInfoList: ArrayList<Image>,
    private val imageListener: ImageListener?
) : RecyclerView.Adapter<PhotosAdapter.ImageViewHolder>() {
    private val itemDataList = ArrayList<ItemData>()
    private val selectedPositions = ArrayList<Int>() // To track selected positions
    private val selectedImages = ArrayList<Image>() // Track selected images
    init {
        for (i in imageInfoList.indices) {
            itemDataList.add(ItemData())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_select_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageInfoList[position]
        Picasso.get().load("file://" + imageUri.path).placeholder(R.drawable.img_image)
            .into(holder.imageView)
        val itemData = itemDataList[position]
        val isSelected = selectedImages.contains(imageUri)
        // holder.bind(imageUri, imageUri.path.toString())
        // var test = false
        // for (image in imageInfoList) {
        //     test = selectedImages.contains(image)
        // }
        // notifyDataSetChanged()
        //
        // if (test){
        //     holder.test11.visibility = View.VISIBLE
        //     // holder.test11.setVisibility(itemData.isTestVisible() ? View.VISIBLE : View.GONE);
        //     holder.img_check.visibility = View.VISIBLE
        // }else{
        //     holder.test11.visibility = if (isSelected) View.VISIBLE else View.GONE
        //     // holder.test11.setVisibility(itemData.isTestVisible() ? View.VISIBLE : View.GONE);
        //     holder.img_check.visibility = if (isSelected) View.VISIBLE else View.GONE
        // }
        holder.test11.visibility = if (isSelected) View.VISIBLE else View.GONE
        // holder.test11.setVisibility(itemData.isTestVisible() ? View.VISIBLE : View.GONE);
        holder.img_check.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.imageView.setOnClickListener {
            if (selectedImages.size < 1 || isSelected) {
                if (isSelected) {
                    // Deselect the item
                    selectedImages.remove(imageUri)
                } else {
                    // Select the item
                    selectedImages.add(imageUri)
                }
                notifyDataSetChanged()
                imageListener?.onImageSelected(selectedImages)
            } else {

            }
        }
    }
    fun setImageList(imageList: ArrayList<Image>) {
        imageInfoList = imageList
        selectedImages.clear() // Clear previously selected images
        itemDataList.clear()

        // Iterate through the new image list
        for (image in imageList) {
            // Check if the image is already selected (from previously stored selected images)
            val isSelected = selectedImages.contains(image)
            itemDataList.add(ItemData(isSelected))
            // Add to selectedImages if the image is already selected
            if (isSelected) {
                selectedImages.add(image)
            }
        }
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun getItemCount(): Int {
        return imageInfoList.size
    }

    interface ImageListener {
        fun onImageSelected(selectedImages: ArrayList<Image>)
    }
    fun getSelectedPositions(): ArrayList<Int> {
        val selectedPositions = ArrayList<Int>()
        for (i in imageInfoList.indices) {
            if (imageInfoList[i].isSelected) {
                selectedPositions.add(i)
            }
        }
        return selectedPositions
    }


    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var img_check: ImageView
        var test11: LinearLayout
        var textView: TextView? = null
        init {
            imageView = itemView.findViewById(R.id.scsii_photos)
            test11 = itemView.findViewById(R.id.scsii_select_hover)
            img_check = itemView.findViewById(R.id.scsii_image_check)
        }

    }

    inner class ItemData(val isSelected: Boolean = false)
}

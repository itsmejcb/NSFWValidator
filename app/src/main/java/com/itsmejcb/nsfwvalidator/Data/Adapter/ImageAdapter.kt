package com.itsmejcb.nsfwvalidator.Data.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.itsmejcb.nsfwvalidator.Data.Model.Image
import com.itsmejcb.nsfwvalidator.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import kotlin.math.pow

class ImageAdapter(private val context: Context, private val imagePaths: ArrayList<Image>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_create_post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val img = imagePaths[position]
        Log.d("ImageAdapter", "Image path: " + img.path)
        if (img.status != null) {
            Log.i("status for image", img.status!!)
        } else {
            Log.i("status for image", "null")
        }

        // runDetection(img.getBitmap());
        holder.loadImageWithBackgroundColor("file://" + img.path)
        Picasso.get().load("file://" + img.path).placeholder(R.drawable.img_image)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imagePaths.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var srcp_card: CardView

        init {
            imageView = itemView.findViewById(R.id.srcp_img)
            srcp_card = itemView.findViewById(R.id.srcp_card)
        }
        fun loadImageWithBackgroundColor(url: String?) {
            try {
                Picasso.get().load(url).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                        // Use Palette to extract the dominant color from the bitmap
                        Palette.from(bitmap)
                            .generate { palette -> // Get the dominant color from the Palette
                                val alpha = (255 * .3).toInt()
                                val defaultColor = ContextCompat.getColor(context, R.color.charlestonGreen)
                                val bgColor = palette?.getDominantColor(defaultColor)
                                val newBgColor = bgColor?.let {
                                    Color.argb(
                                        alpha,
                                        Color.red(it),
                                        Color.green(it),
                                        Color.blue(it)
                                    )
                                } ?: defaultColor

                                // Set the background color of the CardView
                                srcp_card.setCardBackgroundColor(newBgColor)
                            }
                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                        // Handle failed loading
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        // Check if placeHolderDrawable is null and provide a default value if needed
                        val placeholder = placeHolderDrawable ?: ContextCompat.getDrawable(
                            context,
                            R.drawable.img_image
                        )
                        // You can perform any preparations here if needed with the placeholder
                    }
                })
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        // fun loadImageWithBackgroundColor(url: String?) {
        //     try {
        //         Picasso.get().load(url).into(object : Target {
        //             override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
        //                 // Use Palette to extract the dominant color from the bitmap
        //                 Palette.from(bitmap)
        //                     .generate { palette -> // Get the dominant color from the Palette
        //                         val alpha = (255 * .3).toInt()
        //                         val bgColor = palette!!.getDominantColor(
        //                             ContextCompat.getColor(
        //                                 context, R.color.charlestonGreen
        //                             )
        //                         )
        //                         val newBgColor = Color.argb(
        //                             alpha,
        //                             Color.red(bgColor),
        //                             Color.green(bgColor),
        //                             Color.blue(bgColor)
        //                         )
        //
        //                         // Set the background color of the CardView
        //                         srcp_card.setCardBackgroundColor(newBgColor)
        //                     }
        //             }
        //
        //             override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
        //                 // Handle failed loading
        //             }
        //
        //             override fun onPrepareLoad(placeHolderDrawable: Drawable) {
        //                 // You can perform any preparations here if needed
        //             }
        //         })
        //     } catch (e: Exception) {
        //         throw RuntimeException(e)
        //     }
        // }
    }

    companion object {
        fun formatSize(sizeInBytes: Long): String {
            if (sizeInBytes <= 0) {
                return "0 B"
            }
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(sizeInBytes.toDouble()) / Math.log10(1024.0)).toInt()
            return String.format(
                "%.2f %s",
                sizeInBytes / 1024.0.pow(digitGroups.toDouble()),
                units[digitGroups]
            )
        }
    }
}
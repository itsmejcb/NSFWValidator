package com.itsmejcb.nsfwvalidator.Data.Model

import android.graphics.Bitmap

class Image {
    var image_name: String? = null
    var path: String? = null
    var extension: String? = null
    var size: String? = null
    var isSelected = false
    var bitmap: Bitmap? = null
    var status: String? = null
    var isNSFW = false

    constructor() {
        this.image_name = image_name
        this.path = path
        this.extension = extension
        this.size = size
        this.isSelected = isSelected
        this.bitmap = bitmap
        this.status = status
        this.isNSFW = isNSFW
    }

    constructor(isNSFW: Boolean) {
        this.isNSFW = isNSFW
    }

    companion object {
        fun setSelectedImages(images: ArrayList<Image>) {
            for (image in images) {
                image.isSelected = true
            }
        }
    }


}

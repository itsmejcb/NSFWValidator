package com.itsmejcb.nsfwvalidator.Class.Upload

import android.content.Context

class Presenter(view: Interface.View, context: Context) : Interface.Presenter {
    private val view: Interface.View = view
    private val context: Context = context
    init {

    }

    // fun uploadProfile(path: String) {
    //     val size: Int = (File(path).length() / 1024).toInt()
    //     val fileExtension = view.getFileExtension(path)
    //     if (size > 5000) {
    //         view.dialogHandling(1)
    //         return
    //     }
    //     if (fileExtension != ".jpeg" && fileExtension != ".jpg" && fileExtension != ".png" && fileExtension != ".JPEG" && fileExtension != ".JPG" && fileExtension != ".PNG") {
    //         // if (!fileExtension.equals("jpeg") && !fileExtension.equals("jpg") && !fileExtension.equals("png") && !fileExtension.equals("JPEG") && !fileExtension.equals("JPG") && !fileExtension.equals("PNG")) {
    //         view.dialogHandling(2)
    //         return
    //     }
    // }
}
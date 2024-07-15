package com.itsmejcb.nsfwvalidator.Class.Upload;

import com.itsmejcb.nsfwvalidator.Inteface.Interface
interface Interface {
    interface View: Interface.View{

        fun getFileExtension(filePath: String?): String?
        fun dialogHandling(num: Int)
    }
    interface Presenter: Interface.Presenter{

    }
}

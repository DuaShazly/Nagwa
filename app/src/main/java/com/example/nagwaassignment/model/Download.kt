package com.example.nagwaassignment.model

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator

class Download : Parcelable {
    var progress = 0
    var currentFileSize = 0
    var totalFileSize = 0

    constructor() {}
    private constructor(`in`: Parcel) {
        progress = `in`.readInt()
        currentFileSize = `in`.readInt()
        totalFileSize = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(progress)
        dest.writeInt(currentFileSize)
        dest.writeInt(totalFileSize)
    }

    companion object CREATOR : Creator<Download> {
        override fun createFromParcel(parcel: Parcel): Download {
            return Download(parcel)
        }

        override fun newArray(size: Int): Array<Download?> {
            return arrayOfNulls(size)
        }
    }
}
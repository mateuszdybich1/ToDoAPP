package com.dybich.todoapp

import android.os.Parcel
import android.os.Parcelable

class Task(var name : String?=null,
                var shortDescr : String?= "No short description",
                var extraInfo : String?="No extra informations",
                var author : String?= null,
                var creationTime : String?=null,
                var deadline : String?=null,
                var startedBy : String ?= "Not started yet",
                var endedBy : String ?= "Not finished yet") : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(this.name)
        dest?.writeString(this.shortDescr)
        dest?.writeString(this.extraInfo)
        dest?.writeString(this.author)
        dest?.writeString(this.creationTime)
        dest?.writeString(this.deadline)
        dest?.writeString(this.startedBy)
        dest?.writeString(this.endedBy)

    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}

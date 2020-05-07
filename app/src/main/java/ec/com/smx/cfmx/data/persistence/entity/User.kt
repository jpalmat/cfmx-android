package ec.com.smx.cfmx.data.persistence.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Javier Lage.
 * Copyright © 2018 Kruger Corporation. All rights reserved.
 */
class User() : Parcelable {
    // User model to map a non persistent object
    // entity response to store in db
    @SerializedName("userId")
    var userId: String? = null
    @SerializedName("userName")
    var userName: String? = null
    @SerializedName("userCompleteName")
    var userCompleteName: String? = null
    @SerializedName("companyId")
    var companyId: Int? = null
    @SerializedName("profileId")
    var profileId: String? = null
    @SerializedName("token")
    var token: String? = null
    @SerializedName("password")
    var password: String? = null
    @SerializedName("changePwd")
    var changePwd: Boolean = false
    @SerializedName("response")
    var response: Response? = null
    @SerializedName("defaultLocalId")
    var defaultLocalId: Int? = null
    // constructor
    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        userName = parcel.readString()
        userCompleteName = parcel.readString()
        companyId = parcel.readValue(Int::class.java.classLoader) as? Int
        profileId = parcel.readString()
        token = parcel.readString()
        password = parcel.readString()
        changePwd = parcel.readByte() != 0.toByte()
        response = parcel.readParcelable(Response::class.java.classLoader)
        defaultLocalId = parcel.readValue(Int::class.java.classLoader) as? Int
    }
    // status default response
    class Response() : Parcelable {
        @SerializedName("message")
        var message: String? = null
        @SerializedName("status")
        var status: Boolean? = null

        constructor(parcel: Parcel) : this() {
            message = parcel.readString()
            status = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(message)
            parcel.writeValue(status)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Response> {
            override fun createFromParcel(parcel: Parcel): Response {
                return Response(parcel)
            }

            override fun newArray(size: Int): Array<Response?> {
                return arrayOfNulls(size)
            }
        }
    }
    // parcelable method
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(userCompleteName)
        parcel.writeValue(companyId)
        parcel.writeString(profileId)
        parcel.writeString(token)
        parcel.writeString(password)
        parcel.writeByte(if (changePwd) 1 else 0)
        parcel.writeParcelable(response, flags)
        parcel.writeValue(defaultLocalId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}

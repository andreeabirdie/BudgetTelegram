package mobile.birdie.exam1.messages.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String,
    val unreadMsg: Int
) : Parcelable
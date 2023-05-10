package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class PopUpFragment() : DialogFragment(), Parcelable {

    constructor(parcel: Parcel) : this() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pop_up, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PopUpFragment> {
        override fun createFromParcel(parcel: Parcel): PopUpFragment {
            return PopUpFragment(parcel)
        }

        override fun newArray(size: Int): Array<PopUpFragment?> {
            return arrayOfNulls(size)
        }
    }
}
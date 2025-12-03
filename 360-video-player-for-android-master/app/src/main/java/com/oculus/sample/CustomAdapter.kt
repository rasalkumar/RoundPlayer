package com.oculus.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

class CustomAdapter(
    context: Context,
    private val pics: Array<Int>,
    private val url: Array<String>
) : ArrayAdapter<String>(context, R.layout.customadapter, url) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var newConvertView = convertView
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (newConvertView == null) {
            newConvertView = layoutInflater.inflate(R.layout.customadapter, null)
        }
        val imageView = newConvertView!!.findViewById<ImageView>(R.id.imageView)
        imageView.setBackgroundResource(pics[position])
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        return newConvertView
    }
}

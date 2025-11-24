package com.oculus.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class Custom(
    context: Context,
    private val url: Array<String?>,
    private val name: Array<String?>
) : ArrayAdapter<String?>(context, R.layout.customadapter, url) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var newConvertView = convertView
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (newConvertView == null) {
            newConvertView = layoutInflater.inflate(R.layout.row_layout, null)
        }
        val textView = newConvertView!!.findViewById<TextView>(R.id.item)
        textView.text = name[position]
        return newConvertView
    }
}

package com.oculus.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ContactsAdapter(context: Context, resource: Int) : ArrayAdapter<Any?>(context, resource) {

    private val list: MutableList<Any?> = ArrayList()

    fun add(obj: Contacts) {
        super.add(obj)
        list.add(obj)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any? {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        val contactHolder: ContactHolder
        if (row == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = layoutInflater.inflate(R.layout.row_layout, parent, false)
            contactHolder = ContactHolder()
            contactHolder.Item = row.findViewById(R.id.item)
            row.tag = contactHolder
        } else {
            contactHolder = row.tag as ContactHolder
        }
        val contacts = this.getItem(position) as Contacts
        contactHolder.Item!!.text = contacts.getItem()
        return row!!
    }

    internal class ContactHolder {
        var Item: TextView? = null
    }
}

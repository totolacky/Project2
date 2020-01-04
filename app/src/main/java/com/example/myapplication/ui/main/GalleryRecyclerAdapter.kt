package com.example.myapplication.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.*
import android.media.ThumbnailUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.GalleryItem
import com.example.myapplication.R
import kotlinx.android.synthetic.main.list_galleryitem.view.*

class GalleryRecyclerAdapter(private val context: Context, private val listener : OnListItemSelectedInterface,
                             private var items: ArrayList<GalleryItem>) :
    RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnListItemSelectedInterface {
        fun onItemSelected(view : View, position : Int)
    }

    private var mListener : OnListItemSelectedInterface = listener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { v ->
                val position = adapterPosition
                mListener.onItemSelected(v, position)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        var view :View = holder.itemView

        var imageWidth = item.image!!.getWidth()
        var imageHeight = item.image.getHeight()

        view.measure(View.MeasureSpec.makeMeasureSpec(imageWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(imageHeight, View.MeasureSpec.EXACTLY))
        var siz = view.measuredWidth

        var bitmap = item.image
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, siz, siz)

        //if(imageWidth>siz || imageHeight>siz)
        //    bitmap = Bitmap.createScaledBitmap(bitmap, siz, siz, true)

        view.thumbnail.setImageBitmap(bitmap)
        view.tag = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            GalleryRecyclerAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_galleryitem, parent, false))
    }
}
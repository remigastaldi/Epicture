package com.michel.team.epicture

import android.content.Context
import android.graphics.Point
import android.support.v7.widget.CardView
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso

/**
 * Created by puccio_c on 2/9/18.
 */
    class CustomAdapter(private val context : Context, private val list : List<Feed>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var titleTextView: TextView = itemView.findViewById(R.id.comment_feed)
            var countTextView: TextView = itemView.findViewById(R.id.like_counter)
            var thumbImageView : ImageView = itemView.findViewById(R.id.image_feed)
            var favoriteImageView : ImageView = itemView.findViewById(R.id.like_button_feed)
        }

        override fun onCreateViewHolder(parent : ViewGroup, type : Int) : CustomAdapter.ViewHolder{
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.activity_feed, parent, false)
            //val card = view.findViewById(R.id.card_view) as CardView
            //   card.setCardBackgroundColor(Color.parseColor("#E6E6E6"))
            /* card.maxCardElevation = 2.0F
            card.radius = 5.0F */
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder : CustomAdapter.ViewHolder, position : Int){
            val feed : Feed = list[position]

            holder.favoriteImageView.setOnClickListener {
                Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
            }

            if (feed.hasLiked)
                holder.favoriteImageView.background = context.getDrawable(R.drawable.ic_action_favorite)
            holder.titleTextView.text = feed.name
            holder.countTextView.text = "${feed.numOfLikes} likes"

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val imageHeight = (size.x / feed.imageWidth.toFloat()) * feed.imageHeight

            holder.thumbImageView.layoutParams.height = imageHeight.toInt()
            holder.thumbImageView.layoutParams.width = size.x

            Picasso.with(context).load(feed.thumbnail).into(holder.thumbImageView);
        }

        override fun getItemCount() : Int{
            return list.size
        }
}
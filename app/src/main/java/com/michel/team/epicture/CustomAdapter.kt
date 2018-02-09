package com.michel.team.epicture

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by puccio_c on 2/9/18.
 */
    class CustomAdapter(private val context : Context, private val list : List<Feed>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var titleTextView: TextView = itemView.findViewById(R.id.comment_feed) as TextView
            var countTextView: TextView = itemView.findViewById(R.id.like_counter) as TextView
            var thumbImageView : ImageView = itemView.findViewById(R.id.image_feed) as ImageView
            //var overflowImageView : ImageView

            init {
                //overflowImageView = itemView.findViewById(R.id.overflow) as ImageView
            }
        }
        override fun onCreateViewHolder(parent : ViewGroup, type : Int) : CustomAdapter.ViewHolder{
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.activity_feed, parent, false)
            val card = view.findViewById(R.id.card_view) as CardView
            //   card.setCardBackgroundColor(Color.parseColor("#E6E6E6"))
            card.maxCardElevation = 2.0F
            card.radius = 5.0F
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder : CustomAdapter.ViewHolder, position : Int){
            var feed : Feed = list.get(position)
            holder.titleTextView.text = feed.name
            holder.countTextView.text = feed.numOfSongs.toString()
            holder.thumbImageView.setImageResource(feed.thumbnail)
//        holder.overflowImageView.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(view: View) {
//                showPopupMenu(holder.overflowImageView)
//            }
//        });
            //holder.overflowImageView.setOnClickListener{showPopupMenu(holder.overflowImageView)};
        }
        private fun showPopupMenu(view: View) {
            // inflate menu
            val popup = PopupMenu(context, view)
            val inflater = popup.menuInflater
//            inflater.inflate(R.menu.menu_album, popup.getMenu())
            popup.setOnMenuItemClickListener(null)
            popup.show()
        }
        override fun getItemCount() : Int{
            return list.size
        }
}
package com.michel.team.epicture

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.media.AudioManager.AUDIOFOCUS_NONE
import android.net.Uri
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.squareup.picasso.Picasso
import android.media.MediaPlayer
import kotlinx.android.synthetic.main.activity_feed.view.*


/**
 * Created by puccio_c on 2/9/18.
 */
class CustomAdapter(private val context: Context, private val list: List<Feed>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.comment_feed)
        var countTextView: TextView = itemView.findViewById(R.id.like_counter)
        var thumbImageView: ImageView = itemView.findViewById(R.id.image_feed)
        var thumbVideoView: VideoView = itemView.findViewById(R.id.video_feed)
        var favoriteImageView: ImageView = itemView.findViewById(R.id.like_button_feed)
        var playButton: ImageButton = itemView.findViewById(R.id.play_button)
        var videoFeedMuted: ImageView = itemView.findViewById(R.id.video_feed_muted)
        var usernameTextView: TextView = itemView.findViewById(R.id.username_card)
        var user_picImageView: ImageView = itemView.findViewById(R.id.user_pic_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CustomAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_feed, parent, false)
        //val card = view.findViewById(R.id.card_view) as CardView
        //   card.setCardBackgroundColor(Color.parseColor("#E6E6E6"))
        /* card.maxCardElevation = 2.0F
        card.radius = 5.0F */
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val feed: Feed = list[position]

        holder.favoriteImageView.setOnClickListener {
            val req = InstagramApiContext.instagram?.likeContent("1710508171733092400")
            println(req)
            println(req?.text)
            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
        }

        if (feed.hasLiked)
            holder.favoriteImageView.background = context.getDrawable(R.drawable.ic_action_favorite)

        val text = feed.name
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.titleTextView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            holder.titleTextView.text = Html.fromHtml(text)
        }
        holder.countTextView.text = "${feed.numOfLikes} likes"

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        println("Type: " + feed.type)

        val imageHeight = (size.x / feed.imageWidth.toFloat()) * feed.imageHeight

        if (feed.type == 1) {
            println("Contructor")
            holder.playButton.visibility = ImageButton.VISIBLE
            holder.thumbVideoView.visibility = VideoView.GONE

            holder.playButton.setOnClickListener { event ->
                println("Start Button Clicked")

                holder.playButton.visibility = Button.GONE
                holder.thumbImageView.visibility = ImageView.GONE
                holder.thumbVideoView.visibility = VideoView.VISIBLE
                holder.thumbVideoView.setVideoURI(Uri.parse(feed.videoUrl))
                holder.thumbVideoView.layoutParams.height = imageHeight.toInt()
                holder.thumbVideoView.layoutParams.width = size.x
                holder.thumbVideoView.start()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    holder.thumbVideoView.setAudioFocusRequest(AUDIOFOCUS_NONE)
                }
                holder.thumbVideoView.setOnPreparedListener { mp ->
                    mp.isLooping = true
                }

                holder.thumbVideoView.setOnTouchListener { v, event ->
                    val btn = holder.thumbVideoView
                    if (btn.isPlaying) {
                        holder.playButton.visibility = ImageButton.VISIBLE
                        btn.pause()
                    }
                    false
                }

                holder.thumbVideoView.setOnCompletionListener {mp ->
                    holder.thumbVideoView.visibility = VideoView.GONE
                    holder.playButton.visibility = Button.VISIBLE
                    holder.videoFeedMuted.visibility = ImageView.GONE
                    holder.thumbImageView.visibility = ImageView.VISIBLE
                    mp.stop()
                    mp.release()
                }

            }
        } else {
            holder.thumbVideoView.visibility = VideoView.GONE
            holder.playButton.visibility = Button.GONE
            holder.videoFeedMuted.visibility = ImageView.GONE
        }


        holder.thumbImageView.visibility = ImageView.VISIBLE
        holder.thumbImageView.layoutParams.height = imageHeight.toInt()
        holder.thumbImageView.layoutParams.width = size.x

        Picasso.with(context).load(feed.thumbnail).into(holder.thumbImageView)

        holder.usernameTextView.text = feed.username
        Picasso.with(context).load(feed.user_pic).into(holder.user_picImageView)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
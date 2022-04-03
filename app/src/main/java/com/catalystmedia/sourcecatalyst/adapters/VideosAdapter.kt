package com.catalystmedia.sourcecatalyst.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.catalystmedia.sourcecatalyst.PDFActivity
import com.catalystmedia.sourcecatalyst.R
import com.catalystmedia.sourcecatalyst.VideoPlayerActivity
import com.catalystmedia.sourcecatalyst.models.Documents
import com.catalystmedia.sourcecatalyst.models.Profile
import com.catalystmedia.sourcecatalyst.models.Videos
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_documents.*
import java.io.File

class VideosAdapter(private var mContext: Context, private var mList: ArrayList<Videos>):
    RecyclerView.Adapter<VideosAdapter.ViewHolder>() {
    private var mCtx: Context? = null
    private var profileList: MutableList<Profile>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosAdapter.ViewHolder {
        return VideosAdapter.ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.documents_recycler,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: VideosAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        holder.docTitle.text = item.getvideoName()
        holder.docStatus.text = "Click To Watch"
        holder.iconType.setImageResource(R.drawable.play)
        holder.btnCloud.setImageResource(R.drawable.ic_play_fill)
        val videoID = item.getvideoLink()

        holder.cardDoc.setOnClickListener{
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra("videoID", videoID)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return mList.size
    }
    class ViewHolder (@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var iconType:ImageView = itemView.findViewById(R.id.resource_type_icon)
        var docTitle: TextView = itemView.findViewById(R.id.re_tv_doc_title)
        var docStatus: TextView = itemView.findViewById(R.id.re_tv_doc_status)
        var downloadProg: TextView = itemView.findViewById(R.id.re_download_prog)
        var docLevel: CircularProgressBar = itemView.findViewById(R.id.re_progress)
        var btnCloud: ImageView = itemView.findViewById(R.id.re_btn_cloud)
        var cardDoc: CardView = itemView.findViewById(R.id.re_card_doc)
        var docFrame: FrameLayout = itemView.findViewById(R.id.re_frame_docs)


    }



}
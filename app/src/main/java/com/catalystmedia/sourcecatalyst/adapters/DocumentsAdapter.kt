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
import androidx.recyclerview.widget.RecyclerView
import com.catalystmedia.sourcecatalyst.PDFActivity
import com.catalystmedia.sourcecatalyst.R
import com.catalystmedia.sourcecatalyst.models.Documents
import com.catalystmedia.sourcecatalyst.models.Profile
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_documents.*
import java.io.File

class DocumentsAdapter(private var mContext: Context, private var mList: ArrayList<Documents>):
    RecyclerView.Adapter<DocumentsAdapter.ViewHolder>() {
    private var mCtx: Context? = null
    private var profileList: MutableList<Profile>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsAdapter.ViewHolder {
        return DocumentsAdapter.ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.documents_recycler,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: DocumentsAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        holder.docTitle.text = item.getdocumentName()
        var dirPath = context.filesDir
        var docName = item.getdocumentName().toString()
        var filePath = "$dirPath/$docName.pdf"
        if (File(filePath).exists()) {
            holder.docFrame.visibility = View.GONE
            holder.docStatus.setTextColor(Color.parseColor("#609A1C"))
            holder.docStatus.text = "Downloaded"
        }
        //logic for card press
        holder.cardDoc.setOnClickListener {
            holder.docLevel.visibility = View.GONE
            val url = item.getdocumentLink().toString()
            var dirPath = context.filesDir
            var docName = item.getdocumentName().toString()
            var filePath = "$dirPath/$docName.pdf"

            if (File(filePath).exists()) {
                holder.docFrame.visibility = View.GONE
                holder.docStatus.setTextColor(Color.parseColor("#609A1C"))
                holder.docStatus.text = "Downloaded"
                //function to open file
                var dirPath = context.filesDir
                var filePath = "$dirPath/$docName.pdf"
                val intent = Intent(context, PDFActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("uri", filePath)
                context.startActivity(intent)


            } else {
                holder.btnCloud.visibility = View.GONE
                Toast.makeText(context, "Starting Download", Toast.LENGTH_SHORT).show()
                holder.docFrame.visibility = View.VISIBLE
                holder.docStatus.text = "downloading"
                PRDownloader.download(url, dirPath.toString(), "$docName.pdf")
                    .build()
                    .setOnStartOrResumeListener {
                    }
                    .setOnPauseListener { }
                    .setOnCancelListener {
                    }
                    .setOnProgressListener { progress ->
                        holder.btnCloud.visibility = View.GONE
                        holder.docLevel.visibility = View.VISIBLE

                        var currentVal = progress.currentBytes * 100 / progress.totalBytes
                        var numProg = currentVal.toFloat()
                        holder.docLevel.progress = numProg
                        var numString = numProg.toInt().toString()
                        holder.downloadProg.visibility = View.VISIBLE
                        holder.downloadProg.text = "$numString%"
                        if (numProg >= 100F) {
                            holder.docFrame.visibility  = View.GONE
                            holder.docStatus.setTextColor(Color.parseColor("#609A1C"))
                            holder.docStatus.text = "Downloaded"
                        }
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            Toast.makeText(
                                context,
                                "Download Complete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onError(error: com.downloader.Error?) {
                            Toast.makeText(
                                context,
                                "Download $error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

            }

        }

    }


    override fun getItemCount(): Int {
        return mList.size
    }
    class ViewHolder (@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var docTitle: TextView = itemView.findViewById(R.id.re_tv_doc_title)
        var docStatus: TextView = itemView.findViewById(R.id.re_tv_doc_status)
        var downloadProg: TextView = itemView.findViewById(R.id.re_download_prog)
        var docLevel: CircularProgressBar = itemView.findViewById(R.id.re_progress)
        var btnCloud: ImageView = itemView.findViewById(R.id.re_btn_cloud)
        var cardDoc: CardView = itemView.findViewById(R.id.re_card_doc)
        var docFrame: FrameLayout = itemView.findViewById(R.id.re_frame_docs)


    }



}
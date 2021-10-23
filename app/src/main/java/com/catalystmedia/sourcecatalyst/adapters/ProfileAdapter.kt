package com.catalystmedia.sourcecatalyst.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.catalystmedia.sourcecatalyst.R
import com.catalystmedia.sourcecatalyst.models.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileAdapter(private var mContext: Context, private var mList: ArrayList<Profile>):
    RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    private var mCtx: Context? = null
    private var profileList: MutableList<Profile>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.profile_recycler,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        holder.courseTitle.text = item!!.getCourseId()
        holder.status.text = item!!.getCompletionStatus()
        holder.certificateLink.text = item!!.getCertificateLink()
    }

    override fun getItemCount(): Int {
        return mList.size
    }
    class ViewHolder (@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var courseImg: ImageView = itemView.findViewById(R.id.re_course_img)
        var courseTitle: TextView = itemView.findViewById(R.id.re_tv_title)
        var courseLevel: TextView = itemView.findViewById(R.id.re_tv_level)
        var certificateLink: TextView = itemView.findViewById(R.id.re_tv_link)
        var status: TextView = itemView.findViewById(R.id.re_tv_status)

    }


}
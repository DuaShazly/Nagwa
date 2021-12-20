package com.example.nagwaassignment.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.nagwaassignment.model.Attachments
import com.example.nagwaassignment.R
import com.example.nagwaassignment.service.DownloadService
import com.example.nagwaassignment.utils.AppConstants
import com.example.nagwaassignment.utils.AppUtils

class AttachmentsAdapter(private val mList: List<Attachments>) :
    RecyclerView.Adapter<AttachmentsAdapter.AttachmentViewHolder>() {

    var mHolder: AttachmentViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attachment_card, parent, false)

        return AttachmentViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position as Long
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int, payloads: List<Any>) {
        when {
            payloads.isEmpty() -> holder.bind(mList[position]) else -> holder.setProgress2(payloads[0] as Int)
        }
        val mAttachment = mList[position]
        holder.itemView.tag = mList[position]

        holder.nameTxt?.text = mAttachment.name


        mHolder = holder
        holder.downloadBtn?.setOnClickListener {
            val itemAtPosition = mList[position]

            if (holder.itemView.tag == itemAtPosition) {
                val at = holder.itemView.tag as Attachments

                Toast.makeText(
                    holder.downloadBtn.context,
                    holder.downloadBtn.context?.getString(R.string.downloading),
                    Toast.LENGTH_SHORT
                ).show()
                holder.progressBar.visibility = View.VISIBLE
                holder.progressBar.progress = 0

                holder.progressBar.secondaryProgress = 100 // Secondary Progress
                mHolder?.progressTxt?.visibility = View.VISIBLE
                holder.progressBar.max = 100
                if(itemAtPosition == holder.itemView.tag){
                    if (!holder.downloadBtn.context?.let { it1 ->
                            AppUtils.isMyServiceRunning(
                                DownloadService::class.java,
                                it1
                            )
                        }!!
                    ) {
                        val intent = Intent(holder.downloadBtn.context, DownloadService::class.java)
                        intent.putExtra(AppConstants.URL, mAttachment.url)
                        intent.putExtra(AppConstants.attachObj, mAttachment)
                        intent.putExtra(AppConstants.APP_NAME, AppConstants.Nagwa)
                        intent.putExtra(AppConstants.ItemPostion, position)

                        holder.downloadBtn.context.startService(intent)
                    } else
                        Toast.makeText(
                            it.context,
                            it.context.getString(R.string.already_downloading),
                            Toast.LENGTH_SHORT
                        ).show()
                }

            }
        }



    }

    fun setProgress(progresses: List<Int>) {
        progresses.forEachIndexed { i, it ->
            notifyItemChanged(i, it)
        }
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {

        val mAttachment = mList[position]
        holder.itemView.tag = mList[position]

        holder.nameTxt?.text = mAttachment.name


        mHolder = holder
        holder.downloadBtn?.setOnClickListener {
            val itemAtPosition = mList[position]

            if (holder.itemView.tag == itemAtPosition) {
                val at = holder.itemView.tag as Attachments
                Toast.makeText(
                    holder.downloadBtn.context,
                    holder.downloadBtn.context?.getString(R.string.downloading),
                    Toast.LENGTH_SHORT
                ).show()
                holder.progressBar.visibility = View.VISIBLE
                holder.progressBar.progress = 0

                holder.progressBar.secondaryProgress = 100 // Secondary Progress
                mHolder?.progressTxt?.visibility = View.VISIBLE
                holder.progressBar.max = 100
                if(itemAtPosition == holder.itemView.tag){
                    if (!holder.downloadBtn.context?.let { it1 ->
                            AppUtils.isMyServiceRunning(
                                DownloadService::class.java,
                                it1
                            )
                        }!!
                    ) {
                        val intent = Intent(holder.downloadBtn.context, DownloadService::class.java)
                        intent.putExtra(AppConstants.URL, mAttachment.url)
                        intent.putExtra(AppConstants.attachObj, mAttachment)
                        intent.putExtra(AppConstants.APP_NAME,AppConstants.Nagwa)


                        holder.downloadBtn.context.startService(intent)
                    } else
                        Toast.makeText(
                            it.context,
                            it.context.getString(R.string.already_downloading),
                            Toast.LENGTH_SHORT
                        ).show()
                }

            }

        }



    }


    override fun getItemCount(): Int {
        return mList.size
    }

    class AttachmentViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val downloadBtn: ImageButton? = itemView.findViewById(R.id.attachment_imageview)
        val nameTxt: TextView? = itemView.findViewById(R.id.attachment_name)
        val progressBar: ProgressBar = itemView.findViewById(R.id.attachment_progress)
        val progressTxt: TextView? = itemView.findViewById(R.id.textViewSecondary)



        fun bind(model: Attachments){

        }

        fun setProgress2(value: Int){
            progressBar.progress = value
        }
    }

    fun setProgress_(progress:  Int, itemPos: Int) {

        if(itemPos == mHolder?.absoluteAdapterPosition!!){
//            if(progress > 0) {
                mHolder?.progressBar?.progress = progress
                mHolder?.progressBar?.secondaryProgress = progress
                mHolder?.progressTxt?.visibility = View.VISIBLE
                mHolder?.progressTxt?.text = "Complete $progress% of 100"
//            }

            if (progress == 100) {
                mHolder?.progressTxt?.visibility = View.VISIBLE
                mHolder?.progressTxt?.text = "All tasks completed"
            }

        notifyItemChanged(itemPos);
        }




    }

    fun setValue(complete: String, itemPos: Int, progress :Int) {

        if(itemPos == mHolder?.absoluteAdapterPosition!!){

            if (complete == "complete") {
                if(progress == 0) {
                  mHolder?.progressBar?.visibility = View.GONE
                }
            }else if (complete.equals("error")) {
                if(progress == 0) {
                    mHolder?.progressBar?.visibility = View.GONE
                }
                Toast.makeText(
                    mHolder?.itemView?.context,
                    mHolder?.itemView?.context?.getString(R.string.error_msg),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        notifyItemChanged(itemPos);


    }
}
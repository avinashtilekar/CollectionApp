package com.example.ajspire.collection.ui.collection_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ajspire.collection.R
import com.example.ajspire.collection.ui.model.ItemModel


class ListAdapter(private val mList: List<ItemModel>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel = mList[position]

        holder.ivSync.setImageResource(if (itemModel.server_tran_id != null) R.drawable.ic_upload_done else R.drawable.ic_upload_pending)
        holder.tvTitle.text = itemModel.mobile_tran_key
        holder.tvSubTitle.text = itemModel.fee_type
        holder.tvFooter.visibility = View.GONE

        if (!itemModel.customer_name.isNullOrEmpty() || !itemModel.customer_mobile_number.isNullOrEmpty()) {
            holder.tvFooter.visibility = View.VISIBLE
            holder.tvFooter.text = itemModel.customer_name + itemModel.customer_mobile_number
        }

        holder.btnAmt.text = itemModel.amount


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivSync: ImageView = itemView.findViewById(R.id.ivSync)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubTitle: TextView = itemView.findViewById(R.id.tvSubTitle)
        val tvFooter: TextView = itemView.findViewById(R.id.tvFooter)
        val btnAmt: TextView = itemView.findViewById(R.id.btnAmt)
    }
}

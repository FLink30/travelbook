package de.hdmstuttgart.travelbook

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import de.hdmstuttgart.travelbook.PhotoItem.PhotoItemOverviewActivity
import de.hdmstuttgart.travelbook.databinding.ActivityMainBinding
import de.hdmstuttgart.travelbook.databinding.DialogeditTravelbookBinding
import de.hdmstuttgart.travelbook.databinding.TravelbookItemBinding
import de.hdmstuttgart.travelbook.models.TravelbookModel


class TravelbookAdapter(travelbookList: LiveData<List<TravelbookModel>>?, private val listener: RecyclerViewInterface):
    RecyclerView.Adapter<TravelbookAdapter.TravelbookViewHolder>() {

    private var travelbookAdapterList: LiveData<List<TravelbookModel>>? = travelbookList

    private var onItemLongClickListener: ((Int) -> Unit)? = null
    private var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelbookViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TravelbookItemBinding.inflate(layoutInflater, parent, false)
        return TravelbookViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("TravelbookAdapter", "" + travelbookAdapterList?.value?.size)
        return travelbookAdapterList?.value?.size ?: 0
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: TravelbookViewHolder, position: Int) {

        var travelbookList = travelbookAdapterList?.value
        var travelbook: TravelbookModel? = travelbookList?.get(position)

        holder.bind(travelbook)

        Log.d("MovieAdapter", "onBindViewHolder")
        holder.itemView.setOnLongClickListener{
            onItemLongClickListener?.invoke(position)
            true
        }
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(position)
            true
        }

    }

    fun setOnItemLongClickListener(listener: (Int) -> Unit) {
        onItemLongClickListener = listener
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClick = listener
    }



    inner class TravelbookViewHolder(private val binding: TravelbookItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnLongClickListener, View.OnClickListener {

        var travelbookButton: Button = binding.travelbookTitle

        fun bind(travelbookModel: TravelbookModel?) {
            binding.travelbookTitle.text = travelbookModel?.title
        }

        init{
            travelbookButton.setOnLongClickListener(this)
            travelbookButton.setOnClickListener(this)
        }


        fun markSelectedItem(index: Int): Boolean {
        return false
        }

        override fun onLongClick(p0: View?): Boolean {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemLongClick(position)
                return true
            }
            return false
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }
}


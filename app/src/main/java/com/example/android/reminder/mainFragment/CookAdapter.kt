package com.example.android.reminder.mainFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.reminder.R
import com.example.android.reminder.convertLongToDateString
import com.example.android.reminder.database.Cook
import com.example.android.reminder.database.CookDatabaseDao
import kotlinx.android.synthetic.main.cook_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CookAdapter(val cookDatabaseDao: CookDatabaseDao) :
    ListAdapter<Cook, CookAdapter.CookViewHolder>(CookDiffCallback()){

    //=========================================

    // here we create new ViewHolder for RecyclerView when it needs one.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookViewHolder {
        return CookViewHolder.from(parent)
    }

    //=========================================

    // this is how the a item is created.
    override fun onBindViewHolder(holder: CookViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, cookDatabaseDao)
    }

    //=========================================

    // the constructor is private because form function is creating the instances of the viewHolder
    class CookViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: Cook, cookDatabaseDao: CookDatabaseDao) {
            val res = itemView.context.resources
            itemView.cook_name.text = item.name
            itemView.last_date_cooked_text.text = convertLongToDateString(item.lastTimeCooked)
            itemView.cook_now_img.setOnClickListener {
                val currentDate = System.currentTimeMillis()
                item.lastTimeCooked = currentDate
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        cookDatabaseDao.update(item)
                    }
                }
            }
            itemView.delete_img.setOnClickListener {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        cookDatabaseDao.delete(item)
                    }
                }
            }
        }

        // companion object to create an instance of viewHolder class
        companion object {
            fun from(parent: ViewGroup): CookViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.cook_item, parent, false)
                return CookViewHolder(view)
            }
        }
    }

    //=========================================
}

// used to compare the old list with the new list and not replace the whole list
class CookDiffCallback : DiffUtil.ItemCallback<Cook>() {
    override fun areItemsTheSame(oldItem: Cook, newItem: Cook): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cook, newItem: Cook): Boolean {
        return oldItem == newItem
    }
}
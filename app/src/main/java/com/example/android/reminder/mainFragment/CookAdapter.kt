package com.example.android.reminder.mainFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.reminder.utils.createRangeOfTen
import com.example.android.reminder.database.Cook
import com.example.android.reminder.databinding.CookItemBinding
import com.example.android.reminder.databinding.HeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

class CookAdapter(
    val updateCookLastCookDateListener: UpdateCookLastCookDateListener,
    val deleteItemListener: DeleteItemListener
) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(CookDiffCallback()) {

    //========================================= init stuff
    // The default dispatcher that is used when coroutines are launched in GlobalScope
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    //========================================= Overriding stuff

    // here we create new ViewHolder for RecyclerView when it needs one.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> CookViewHolder.from(parent)
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    // Here a view gets bound with a viewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CookViewHolder -> {
                val cookItem = getItem(position) as DataItem.CookDataItem
                holder.bind(cookItem.cook, updateCookLastCookDateListener, deleteItemListener)
            }
            is HeaderViewHolder -> {
                val headerItem = getItem(position) as DataItem.HeaderDataItem
                holder.bind(headerItem.dayNumber)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.HeaderDataItem -> ITEM_VIEW_TYPE_HEADER
            else -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun addHeaderAndSubmitList(list: List<Cook>?, order: Int) {
        adapterScope.launch {
            val listOfLists = list?.groupBy {
                createRangeOfTen(it.lastTimeCooked)
            }?.map { it.value }
            var endList: List<DataItem> = listOf()
            if(!listOfLists.isNullOrEmpty()){
                val size = listOfLists.size
                if(order == ASCENDING_ORDER){
                    for((index, itemList) in listOfLists.withIndex()){
                        endList = endList + DataItem.HeaderDataItem(size - index - 1) + itemList.reversed().map { DataItem.CookDataItem(it) }
                    }
                    
                }else{
                    for((index, itemList) in listOfLists.reversed().withIndex()){
                        endList = endList + DataItem.HeaderDataItem(index) + itemList.reversed().map { DataItem.CookDataItem(it) }
                    }
                }

            }
            withContext(Dispatchers.Main) {
                submitList(endList)
            }
        }
    }

    //========================================= View Holders

    class HeaderViewHolder private constructor(val binding: HeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dayNumber: Int) {
            binding.dayNumber = dayNumber
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }

    // the constructor is private because form function is creating the instances of the viewHolder
    class CookViewHolder private constructor(val binding: CookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Cook,
            updateCookLastCookDateListener: UpdateCookLastCookDateListener,
            deleteItemListener: DeleteItemListener
        ) {
            binding.cook = item
            // this will bind the listeners defined in the viewModel and passed form the Fragment with the
            // Variable int the Ui and make them operational.
            binding.updateCookLastCookDateListener = updateCookLastCookDateListener
            binding.deleteItemListener = deleteItemListener

            // This call is an optimization that asks data binding to execute any pending bindings right away
            binding.executePendingBindings()
        }

        // companion object to create an instance of viewHolder class
        companion object {
            fun from(parent: ViewGroup): CookViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CookItemBinding.inflate(layoutInflater, parent, false)
                return CookViewHolder(binding)
            }
        }
    }
}

//========================================= Helper classes

// used to compare the old list with the new list and not replace the whole list
class CookDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class DeleteItemListener(val clickListener: (cook: Cook) -> Unit) {
    // this fun will execute the lambda passed as a property
    fun onClick(cook: Cook) = clickListener(cook)
}

class UpdateCookLastCookDateListener(val clickListener: (cook: Cook) -> Unit) {
    fun onClick(cook: Cook) = clickListener(cook)
}


//========================================= Header stuff

// classes outside this file can't implement this class.
sealed class DataItem() {
    abstract val id: Int

    data class CookDataItem(val cook: Cook) : DataItem() {
        override val id = cook.id
    }

    // this object could implement the sealed class
    class HeaderDataItem(val dayNumber: Int) : DataItem() {
        override val id = Int.MIN_VALUE
    }
}
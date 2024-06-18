package com.dicoding.areunemia.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.CalendarData

class CalendarAdapter(
    private val listener: OnItemClickListener,
    private var calendarList: ArrayList<CalendarData>
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    private var selectedPosition = -1

    interface OnItemClickListener {
        fun onItemClick(calendarData: CalendarData, position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfMonth: TextView = itemView.findViewById(R.id.tvCalendarDate)
        val dayOfWeek: TextView = itemView.findViewById(R.id.tvCalendarDay)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(calendarList[adapterPosition], adapterPosition)
                selectItem(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calendarData = calendarList[position]
        holder.dayOfMonth.text = calendarData.calendarDate
        holder.dayOfWeek.text = calendarData.calendarDay

        // Highlight selected day or day with event
        if (calendarData.isSelected || calendarData.hasEvent) {
            holder.itemView.setBackgroundResource(R.color.red)
            holder.dayOfMonth.setTextColor(holder.itemView.context.getColor(R.color.white))
            holder.dayOfWeek.setTextColor(holder.itemView.context.getColor(R.color.white))
        } else {
            holder.itemView.setBackgroundResource(android.R.color.white)
            holder.dayOfMonth.setTextColor(holder.itemView.context.getColor(android.R.color.black))
            holder.dayOfWeek.setTextColor(holder.itemView.context.getColor(android.R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return calendarList.size
    }

    private fun selectItem(position: Int) {
        if (selectedPosition != position) {
            val previousPosition = selectedPosition
            selectedPosition = position
            calendarList[selectedPosition].isSelected = true

            // Ensure previous item is deselected only if it does not have an event
            if (previousPosition != -1) {
                val previousData = calendarList[previousPosition]
                if (!previousData.hasEvent) {
                    previousData.isSelected = false
                }
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)
        }
    }

    fun updateList(newList: ArrayList<CalendarData>) {
        calendarList.clear()
        calendarList.addAll(newList)
        notifyDataSetChanged()
    }
}

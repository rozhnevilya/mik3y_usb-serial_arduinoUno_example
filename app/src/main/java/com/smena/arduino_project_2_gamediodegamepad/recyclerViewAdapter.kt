package com.smena.arduino_project_2_gamediodegamepad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recyclerViewAdapter (
    private val dataList: List<DC_recyclerView_item>
) : RecyclerView.Adapter<recyclerViewAdapter.CustomViewHolder>() {

    // Инициализация кастомного ViewHolder
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Пользовательный интерфейс
        val time = itemView.findViewById<TextView>(R.id.textView_time)
        val data = itemView.findViewById<TextView>(R.id.textView_data)
    }
    // Создание экземпляра на основе макета (.xml) - view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recyclerViewAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return recyclerViewAdapter.CustomViewHolder(view)
    }
    // Связывание данных из списка с item
    // Заполнение экземпляра
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = dataList[position]

        holder.time.text = currentItem.time
        holder.data.text = currentItem.data

    }
    // Возвращание количества элементов в списке данных
    override fun getItemCount() = dataList.size
}
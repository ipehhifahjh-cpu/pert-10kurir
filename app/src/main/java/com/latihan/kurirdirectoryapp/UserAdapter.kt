package com.latihan.kurirdirectoryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private var listKurir: List<UserResponse>
) : RecyclerView.Adapter<UserAdapter.KurirViewHolder>() {

    class KurirViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.textViewName)
        val tvCompany: TextView = view.findViewById(R.id.textViewCompany)
        val tvEmail: TextView = view.findViewById(R.id.textViewEmail)
        val tvPhone: TextView = view.findViewById(R.id.textViewPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KurirViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_kurir, parent, false)
        return KurirViewHolder(layout)
    }

    override fun onBindViewHolder(holder: KurirViewHolder, position: Int) {
        val item = listKurir[position]
        holder.tvName.text = item.name
        holder.tvCompany.text = "Perusahaan: ${item.company.companyName}"
        holder.tvEmail.text = "Email: ${item.email}"
        holder.tvPhone.text = "Telepon: ${item.phone}"
    }

    override fun getItemCount(): Int = listKurir.size

    fun updateData(newList: List<UserResponse>) {
        this.listKurir = newList
        notifyDataSetChanged()
    }
}
package com.example.highfive.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.highfive.Models.Connection
import com.example.highfive.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.listitem_contact.view.*

class RolodexRecyclerViewAdapter(private val users: ArrayList<Connection>, private val listener: RolodexListener):
    RecyclerView.Adapter<RolodexRecyclerViewAdapter.RolodexViewHolder>() {

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: RolodexViewHolder, position: Int) {
        val user: Connection = users.get(position)
        holder.name.text = user.name
        if(!user.picture.isNullOrBlank()) {
            Picasso.get().load(user.picture).into(holder.profileIcon)
        } else {
            holder.profileIcon.setImageResource(R.drawable.ic_user)
        }
        holder.company.text = user.company
        holder.callButton.setOnClickListener { listener.onCallButton(user.phone) }
        holder.emailButton.setOnClickListener { listener.onEmailButton(user.email) }
        holder.name.setOnClickListener { listener.onUserSelected(user) }
        holder.company.setOnClickListener { listener.onUserSelected(user) }
        holder.profileIcon.setOnClickListener { listener.onUserSelected(user) }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolodexViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.listitem_contact, parent, false)
        return RolodexViewHolder(view)
    }

    class RolodexViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.textview_user_name
        var profileIcon: CircleImageView = itemView.imageview_thumbnail
        var company: TextView = itemView.textview_company
        var callButton: Button = itemView.button_call
        var emailButton: Button = itemView.button_email

    }

    interface RolodexListener {
        fun onUserSelected(user: Connection)
        fun onCallButton(phone: String)
        fun onEmailButton(email: String)
    }

}

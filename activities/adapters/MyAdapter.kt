package com.example.myapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.models.CardModel

public data class MyAdapter(
		private var list: ArrayList <CardModel>?,
		private var context: Context?
		) : RecyclerView.Adapter <MyAdapter.MyViewHolder> () {

	public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var articleName : TextView = itemView.findViewById(R.id.article_name)
		var sourceName : TextView = itemView.findViewById(R.id.source_name)
		var dateAdded : TextView = itemView.findViewById(R.id.date_added)

		init {
			// emne hoyna ken jani
			articleName = itemView.findViewById(R.id.article_name)
			itemView.setOnClickListener { MainActivity.onClickListener_Cards(articleName.toString()) }
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
		val view : View = LayoutInflater.from(context).inflate(R.layout.recyclerview, parent, false)
		return MyViewHolder(view)
	}

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		var cardModel : CardModel = list!![position]
		holder.articleName.text = cardModel.articleName
		holder.sourceName.text = cardModel.sourceName
		holder.dateAdded.text = cardModel.dateAdded
	}

	override fun getItemCount(): Int {
		return list!!.size
	}
}
package com.example.bibliotaph.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliotaph.R
import com.example.bibliotaph.models.CardModel

data class MyAdapter(
		private var cardList: ArrayList <CardModel>,
		private val onCardListener : OnCardListener
		) : RecyclerView.Adapter <MyAdapter.MyViewHolder> () {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
		val view : View = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
		return MyViewHolder(view)
	}

	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		val cardModel = cardList[position]
		holder.articleName.text = cardModel.fileName
		holder.dateAdded.text = cardModel.dateAdded
		Log.d("recyclerview", ": binded $position")
	}

	override fun getItemCount(): Int {
		return cardList.size
	}

	inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
		var articleName : TextView = itemView.findViewById(R.id.article_name)
		var dateAdded : TextView = itemView.findViewById(R.id.date_added)

		init {
			itemView.setOnClickListener(this)

			Log.d("recyclerview", ": holder init ")
		}

		override fun onClick(v: View) {
			if(adapterPosition != RecyclerView.NO_POSITION) {
				onCardListener.onCardClick(adapterPosition)

				Log.d("recyclerview", ": onclick  ")
			}
		}
	}

	interface OnCardListener {
		fun onCardClick (position: Int)
	}
}

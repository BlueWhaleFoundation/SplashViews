package foundation.bluewhale.splashviews.demo

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class FeatureListAdapter(val list: ArrayList<ListData>, var onItemClickListener: OnItemClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<FeatureListAdapter.ViewHolder>() {
    companion object {
        data class ListData(val index: Int, val text: String)
    }

    interface OnItemClickListener {
        fun onItemClicked(listData: ListData?)
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.a_text, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(vh: ViewHolder, p1: Int) {
        vh.tv.text = list[p1].text
    }

    inner class ViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var tv: TextView

        init {
            tv = itemView.findViewById(R.id.tv)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (onItemClickListener!= null) onItemClickListener.onItemClicked(list[adapterPosition])
        }
    }


}
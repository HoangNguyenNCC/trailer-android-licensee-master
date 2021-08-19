package devx.app.seller.modules.home.hometab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import devx.app.licensee.R
import devx.app.licensee.common.utils.HelperMethods
import devx.app.seller.modules.rentals.details.TrailerDetailsActivity
import devx.app.seller.webapi.response.trailerslist.Trailers

class FeaturedTrailerAdapter(
    private val context: Context,
    val trailerList: List<Trailers>,
    private val isHorizontalList: Boolean) : RecyclerView.Adapter<FeaturedTrailerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View? = if (isHorizontalList)
            LayoutInflater.from(parent.context).inflate(
                R.layout.featured_trailers_item_layout,
                parent,
                false
            )
        else
            LayoutInflater.from(parent.context).inflate(
                R.layout.all_trailers_item_layout,
                parent,
                false
            )
        return ViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailers = trailerList[position]
        if (trailers.photo != null)
            HelperMethods.downloadImage(trailers.photo, context, holder.trailerImage)
        holder.trailerTitle.text = trailers.name

        if (isHorizontalList) {
                holder.trailerPrice.text = "From " + trailers.price+" / HR"
        } else {
            holder.trailerPrice.text = "By " + trailers.licensee
        }

        holder.itemView.setOnClickListener {
            TrailerDetailsActivity().openTrailer(
                context,
                trailerList[holder.adapterPosition]._id
            )
        }
    }

    override fun getItemCount() = trailerList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerImage: ImageView = itemView.findViewById(R.id.trailerImage)
        val trailerTitle: TextView = itemView.findViewById(R.id.trailerTitle)
        val trailerPrice: TextView = itemView.findViewById(R.id.trailerPrice)
    }
}
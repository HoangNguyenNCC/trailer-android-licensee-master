package devx.app.licensee.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import devx.app.licensee.R

class PlaceHolderAdapter(private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var resId = -1

    constructor(context: Context?, resId: Int) : this(context) {
        this.resId = resId
    }

    constructor(context: Context?, resId: Int, size: Int) : this(context) {
        this.resId = resId
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val bankListLayout: View = LayoutInflater.from(context).inflate(resId, parent, false)
        return ViewHolder(bankListLayout)
    }

    override fun onBindViewHolder(
        mholder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val holder =
            mholder as ViewHolder
        if (resId == R.layout.trailer_placeholder) {
            (holder.itemView.findViewById<View>(
                R.id.rootShimmer
            ) as ShimmerFrameLayout).startShimmer()
        } else {
            try {
                (holder.itemView.findViewById<View>(R.id.rootShimmer) as ShimmerFrameLayout).startShimmer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    inner class ViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    init {
        resId = R.layout.trailer_placeholder
    }
}
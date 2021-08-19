package devx.app.licensee.modules.addUpSellItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import devx.app.licensee.R;
import devx.app.licensee.common.custumViews.RegularTextView;
import devx.app.seller.webapi.response.trailerslist.Trailers;
import devx.app.seller.webapi.response.upSellItem.UpsellItemsByAdmin;

public class UpsellByAdminAdapter extends BaseAdapter {
    List<UpsellItemsByAdmin> mList;
    Context context;
    private LayoutInflater mInflater;
    String flag;

    public UpsellByAdminAdapter(List<UpsellItemsByAdmin> mList, Context context, String flg) {
        this.mList = mList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.flag=flg;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        UpsellByAdminAdapter.ViewHolder holder;

        if (convertView == null) {
            holder = new UpsellByAdminAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.sgl_spinner_item_trailer, parent, false);
            holder.text = convertView.findViewById(R.id.txt);
            convertView.setTag(holder);
        } else {
            holder = (UpsellByAdminAdapter.ViewHolder) convertView.getTag();
        }
        holder.text.setText(mList.get(position).getName());


        return convertView;
    }

    class ViewHolder {
        RegularTextView text;
    }
}

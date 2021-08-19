//package devx.app.seller.modules.search
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import devx.app.licensee.R
//import kotlinx.android.synthetic.main.fragment_advance_search.*
//
//class AdvanceSearchBottomSheet : BottomSheetDialogFragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_advance_search, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        textview.setOnClickListener {
//            (activity as SearchTrailerActivity).getFeaturedTrailer(textview.text.toString())
//        }
//    }
//}
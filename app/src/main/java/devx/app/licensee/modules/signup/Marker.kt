package devx.app.licensee.modules.signup

import com.google.android.gms.maps.model.LatLng

class Marker {
    fun remove() {
        position = null
    }

    var position: LatLng? = null
}
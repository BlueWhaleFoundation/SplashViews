package foundation.bluewhale.splashviews.demo.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import foundation.bluewhale.splashviews.demo.R

class EditFragment:BaseFragment() {
    override fun getTitle(): String {
        return "BWEditText"
    }

    override fun getTAG(): String {
        return EditFragment::class.java.canonicalName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}
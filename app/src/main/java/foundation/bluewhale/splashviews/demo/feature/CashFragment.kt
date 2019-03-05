package foundation.bluewhale.splashviews.demo.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import foundation.bluewhale.splashviews.demo.R
import kotlinx.android.synthetic.main.f_cash.*

class CashFragment : BaseFragment() {
    override fun getTitle(): String {
        return "BWCashText"
    }

    override fun getTAG(): String {
        return CashFragment::class.java.canonicalName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_cash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_name.text="abc"

    }
}
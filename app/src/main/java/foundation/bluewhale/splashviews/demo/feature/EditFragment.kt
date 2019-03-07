package foundation.bluewhale.splashviews.demo.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import kotlinx.android.synthetic.main.f_edit.*


class EditFragment : BaseFragment() {
    override fun getTitle(): String {
        return "BWEditText"
    }

    override fun getTAG(): String {
        return EditFragment::class.java.canonicalName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(foundation.bluewhale.splashviews.demo.R.layout.f_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        email.et_text.onFocusChangeListener = OnFocusChangeListener { _, gainFocus ->
            if (gainFocus) {
                email.setShowClearButton(true)
            } else {
                email.setShowClearButton(false)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }
}
package foundation.bluewhale.splashviews.demo.feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.f_ect_view.*


class ViewsFragment : BaseFragment() {
    override fun getTitle(): String {
        return "BWEditText"
    }

    override fun getTAG(): String {
        return ViewsFragment::class.java.canonicalName
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(foundation.bluewhale.splashviews.demo.R.layout.f_ect_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        disposables.add(number_picker.numberObserver
            .subscribe({
                tv_name.text = "BwNumberPicker : ${it.toString()}"
            }, {
                it.printStackTrace()
            }))

        switch_for_button.isChecked = button.isEnabled
        switch_for_button.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                button.isEnabled = isChecked
            }
        })
        button.setOnClickListener {
            Toast
                .makeText(
                    context
                    , "Clicked"
                    , Toast.LENGTH_LONG
                )
                .show()
        }
    }
}
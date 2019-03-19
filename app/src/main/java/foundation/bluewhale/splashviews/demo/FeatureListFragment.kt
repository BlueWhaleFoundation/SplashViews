package foundation.bluewhale.splashviews.demo

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import foundation.bluewhale.splashviews.demo.feature.BaseFragment
import foundation.bluewhale.splashviews.demo.feature.CashFragment
import foundation.bluewhale.splashviews.demo.feature.EditFragment
import foundation.bluewhale.splashviews.dialog.DoubleButtonDialog
import foundation.bluewhale.splashviews.dialog.PasswordValidationDialog
import foundation.bluewhale.splashviews.model.PasswordViewColors
import kotlinx.android.synthetic.main.f_list.*

class FeatureListFragment : BaseFragment() {
    override fun getTitle(): String {
        return "SplashViews FeatureList"
    }

    override fun getTAG(): String {
        return FeatureListFragment::class.java.canonicalName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        var deco = androidx.recyclerview.widget.DividerItemDecoration(
            activity,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        )
        deco.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.divider)!!)
        recyclerView.addItemDecoration(deco)

        val adapter = FeatureListAdapter(
            makeList(),
            object : FeatureListAdapter.OnItemClickListener {
                override fun onItemClicked(listData: FeatureListAdapter.Companion.ListData?) {
                    loadFragment(listData)
                }

            })
        recyclerView.adapter = adapter
    }

    val indexCash = 0
    val indexEdit = 1
    val indexDoubleBtnDialog = 2
    val indexPasswordDialog = 3
    fun makeList(): ArrayList<FeatureListAdapter.Companion.ListData> {
        val list = ArrayList<FeatureListAdapter.Companion.ListData>()
        list.add(FeatureListAdapter.Companion.ListData(indexCash, "CashFragment"))
        list.add(FeatureListAdapter.Companion.ListData(indexEdit, "EditFragment"))
        list.add(FeatureListAdapter.Companion.ListData(indexDoubleBtnDialog, "DoubleBtnDialog"))
        list.add(FeatureListAdapter.Companion.ListData(indexPasswordDialog, "PasswordDialog"))
        return list
    }

    fun loadFragment(data: FeatureListAdapter.Companion.ListData?) {
        data?.also {
            when (it.index) {
                indexCash -> addFragment(CashFragment())
                indexEdit -> addFragment(EditFragment())
                indexDoubleBtnDialog -> DoubleButtonDialog.make(context, "hihihi")
                indexPasswordDialog -> {
                    val d = PasswordValidationDialog.make(
                        context!!,
                        true,
                        "비밀번호",
                        false,
                        object : PasswordValidationDialog.StatusChangeListener {
                            override fun onPasswordForgotClicked() {
                                makeToast("onPasswordForgotClicked()")
                            }

                            override fun onPasswordCompleted(password: String) {
                                makeToast("onPasswordCompleted(): " + password)
                            }

                            override fun onKeyPressed(dialog: DialogInterface, keyCode: Int, event: KeyEvent) {

                            }


                        })
                    d.setCancelListener(object:PasswordValidationDialog.CancelListener{
                        override fun onCancel() {
                            makeToast("onCancel(): ")
                        }

                    })
                    d.show(childFragmentManager, "비밀번호")
                    d.setPasswordViewColors(
                        PasswordViewColors(
                            ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                            , ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                            , ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                            , ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                        )
                    )
                }
            }
        }
    }

    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
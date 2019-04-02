package foundation.bluewhale.splashviews.demo

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import foundation.bluewhale.splashviews.demo.QRTestActivity.Companion.RESULT_RESTART_CAMERA
import foundation.bluewhale.splashviews.demo.QRTestActivity.Companion.RESULT_TEXT
import foundation.bluewhale.splashviews.demo.feature.BaseFragment
import foundation.bluewhale.splashviews.demo.feature.CashFragment
import foundation.bluewhale.splashviews.demo.feature.ViewsFragment
import foundation.bluewhale.splashviews.demo.feature.EditFragment
import foundation.bluewhale.splashviews.dialog.DoubleButtonDialog
import foundation.bluewhale.splashviews.dialog.PasswordValidationDialog
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

        val deco = androidx.recyclerview.widget.DividerItemDecoration(
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

    val indexViews = 0
    val indexCash = 1
    val indexEdit = 2
    val indexDoubleBtnDialog = 3
    val qrscanner = 4
    val indexPasswordDialog = 5
    fun makeList(): ArrayList<FeatureListAdapter.Companion.ListData> {
        val list = ArrayList<FeatureListAdapter.Companion.ListData>()
        list.add(FeatureListAdapter.Companion.ListData(indexViews, "ViewsFragment"))
        list.add(FeatureListAdapter.Companion.ListData(indexCash, "CashFragment"))
        list.add(FeatureListAdapter.Companion.ListData(indexEdit, "EditFragment"))
        list.add(FeatureListAdapter.Companion.ListData(indexDoubleBtnDialog, "DoubleBtnDialog"))
        list.add(FeatureListAdapter.Companion.ListData(qrscanner, "QRScannerActivity"))
        list.add(FeatureListAdapter.Companion.ListData(indexPasswordDialog, "PasswordDialog"))

        return list
    }

    val requestCodeForQRActivity = 3840
    fun startQrActivity() {
        activity?.also {
            QRTestActivity.launch(it, requestCodeForQRActivity)
        }
    }

    fun loadFragment(data: FeatureListAdapter.Companion.ListData?) {
        data?.also {
            when (it.index) {
                indexCash -> addFragment(CashFragment())
                indexEdit -> addFragment(EditFragment())
                indexDoubleBtnDialog -> DoubleButtonDialog.make(context, "hihihi")
                qrscanner -> {
                    startQrActivity()
                }
                indexViews -> addFragment(ViewsFragment())
                indexPasswordDialog -> {
                    val d = PasswordValidationDialog.make(
                        context!!,
                        true,
                        getString(R.string.password),
                        false,
                        object : PasswordValidationDialog.StatusChangeListener {
                            override fun onPasswordForgotClicked() {
                                makeToast("onPasswordForgotClicked()")
                            }

                            override fun onPasswordCompleted(password: String) {
                                makeToast("onPasswordCompleted(): $password")
                            }

                            override fun onKeyPressed(dialog: DialogInterface, keyCode: Int, event: KeyEvent) {

                            }


                        })
                    d.setCancelListener(object : PasswordValidationDialog.CancelListener {
                        override fun onCancel() {
                            makeToast("onCancel(): ")
                        }

                    })
                    d.show(childFragmentManager, "Password")
                    /*d.setPasswordViewColors(
                        PasswordViewColors(
                            ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                            , ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                            , ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                            , ContextCompat.getColor(context!!, R.color.colorBlueGreen)
                        )
                    )*/
                }
            }
        }
    }

    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeForQRActivity && resultCode == Activity.RESULT_OK) {
            data?.also {
                val isRestarting = it.getBooleanExtra(RESULT_RESTART_CAMERA, false)
                if (isRestarting) {
                    startQrActivity()
                } else {
                    Toast
                        .makeText(
                            context
                            , "Scanned! :\n ${it.getStringExtra(RESULT_TEXT)}"
                            , Toast.LENGTH_LONG
                        )
                        .show()
                }
            }

        }
    }
}
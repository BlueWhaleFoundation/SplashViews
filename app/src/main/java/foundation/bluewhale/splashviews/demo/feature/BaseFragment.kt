package foundation.bluewhale.splashviews.demo.feature

import android.support.v4.app.Fragment
import foundation.bluewhale.splashviews.demo.MainActivity

abstract class BaseFragment:Fragment() {
    abstract fun getTAG():String

    abstract fun getTitle():String

    protected fun addFragment(fragmentBase: BaseFragment) {
        (activity as MainActivity).addFragment(fragmentBase)
    }

    protected fun replaceFragment(fragmentBase: BaseFragment) {
        (activity as MainActivity).replaceFragment(fragmentBase)
    }
}
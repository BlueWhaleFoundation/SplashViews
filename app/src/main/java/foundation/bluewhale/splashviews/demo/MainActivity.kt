package foundation.bluewhale.splashviews.demo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.view.inputmethod.InputMethodManager
import foundation.bluewhale.splashviews.demo.feature.BaseFragment

class MainActivity : AppCompatActivity() {
    lateinit var rFragmentManager: androidx.fragment.app.FragmentManager
    private var rFragment: BaseFragment? = null

    override fun onDestroy() {
        if (mOnBackStackChangedListener != null && rFragmentManager != null)
            rFragmentManager.removeOnBackStackChangedListener(mOnBackStackChangedListener)

        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rFragmentManager = supportFragmentManager
        rFragmentManager.addOnBackStackChangedListener(mOnBackStackChangedListener)

        replaceFragment(FeatureListFragment())
    }

    private val mOnBackStackChangedListener = androidx.fragment.app.FragmentManager.OnBackStackChangedListener {
        try {
            val fragmentCount = rFragmentManager.backStackEntryCount
            if (fragmentCount > 0) {

                val backEntry = rFragmentManager.getBackStackEntryAt(getFragmentcount() - 1)
                val fragment = rFragmentManager.findFragmentByTag(backEntry.name)
                rFragment = fragment as BaseFragment
                /*if (rFragment != null && rFragment !is MainTabFragment) {
                    changeColorTheme(rFragment)
                }*/

                supportActionBar?.also {
                    it.title = rFragment!!.getTitle()
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFragmentcount(): Int {
        return if (rFragmentManager != null) rFragmentManager.backStackEntryCount else 0
    }

    fun replaceFragment(baseFragment: BaseFragment) {
        changeFragment(baseFragment, true)
    }

    fun addFragment(baseFragment: BaseFragment) {
        changeFragment(baseFragment, false)
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager ?: return

        if (currentFocus != null && currentFocus.windowToken != null)
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun changeFragment(baseFragment: BaseFragment, removingChildFragments: Boolean) {
        hideKeyboard()
        try {
            if (rFragment != null
                && rFragment!!.getTAG().equals(baseFragment.getTAG())
                && removingChildFragments
            )
                return

            val ft = rFragmentManager.beginTransaction()
            val backStackCount = rFragmentManager.backStackEntryCount
            ft.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

            if (removingChildFragments
                && backStackCount > 0) {
                rFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }

            if (backStackCount > 0) {
                ft.setCustomAnimations(
                    R.anim.slide_in_right_left,
                    R.anim.slide_out_right_left,
                    R.anim.slide_in_left_right,
                    R.anim.slide_out_left_right
                )
            } else {
                //ft.setCustomAnimations(R.anim.slide_in_left_right, 0, 0, R.anim.slide_out_left_right);
                ft.setCustomAnimations(0, 0, 0, 0)
            }

            ft.addToBackStack(baseFragment!!.getTAG())
            ft.replace(R.id.fragment_contain, baseFragment!!, baseFragment!!.getTAG())
            ft.commitAllowingStateLoss()

            rFragment = baseFragment

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

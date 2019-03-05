package foundation.bluewhale.splashviews.demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.runner.AndroidJUnit4
import foundation.bluewhale.splashviews.demo.feature.CashFragment
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        /*val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("foundation.bluewhale.splashviews.demo", appContext.packageName)*/

        val factory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String,
                                     args: Bundle?): Fragment {
                return CashFragment()
            }
        }
        val f = object:FragmentFactory(){}

        // The "state" and "factory" arguments are optional.
        val fragmentArgs = Bundle().apply {
            putInt("selectedListItem", 0)
        }
        //val factory = RegisterInformationFragment.getInstanceForSignUp(true)
        val scenario = launchFragmentInContainer<CashFragment>()
            //fragmentArgs, factory)
        Thread.sleep(2000)
        //scenario.moveToState(Lifecycle.State.CREATED)
        Espresso.onView(ViewMatchers.withId(R.id.tvn_ame))
            .check(ViewAssertions.matches(ViewMatchers.withText("BWCashText")))
    }
}

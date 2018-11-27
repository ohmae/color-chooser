/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.colorchooser

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_color_chooser.*
import kotlinx.android.synthetic.main.fragment_color_chooser.view.*
import net.mm2d.colorchooser.ControlFragment.OnControlClickListener

class ColorChooserActivity : AppCompatActivity(), ColorChangeObserver, OnControlClickListener {
    private var sectionsPagerAdapter: SectionsPagerAdapter? = null
    private var color = Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_chooser)
        sectionsPagerAdapter = SectionsPagerAdapter(
            supportFragmentManager,
            listOf(
                { ManualSelectFragment() },
                { PlaceholderFragment.newInstance(2) }
            )
        )
        container.requestDisallowInterceptTouchEvent(true)
        container.adapter = sectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        savedInstanceState?.let {
            color = it.getInt(KEY_COLOR, Color.BLACK)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(KEY_COLOR, color)
    }

    override fun onResume() {
        super.onResume()
        onColorChange(color, null)
    }

    override fun onColorChange(color: Int, fragment: Fragment?) {
        this.color = color
        supportFragmentManager.fragments.forEach {
            (it as? ColorChangeObserver)?.onColorChange(color, fragment)
        }
    }

    override fun onDoneClick() {
        finish()
    }

    override fun onCancelClick() {
        finish()
    }

    inner class SectionsPagerAdapter(
        fm: FragmentManager,
        private val list: List<() -> Fragment>
    ) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return list[position]()
        }

        override fun getCount(): Int {
            return list.size
        }
    }

    class PlaceholderFragment : Fragment(), ColorChangeObserver {

        override fun onColorChange(color: Int, fragment: Fragment?) {
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_color_chooser, container, false)
            rootView.section_label.text =
                    getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            private const val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    companion object {
        private const val KEY_COLOR = "KEY_COLOR"
    }
}

/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.color.chooser

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

internal class ViewPagerAdapter(
    pageViews: List<View>,
    pageTitles: List<String>
) : PagerAdapter() {
    private val pageViews = pageViews.toList()
    private val pageTitles = pageTitles.toList()

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
    override fun getCount(): Int = pageViews.size
    override fun getPageTitle(position: Int): CharSequence = pageTitles[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any =
        pageViews[position].also { container.addView(it) }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) =
        container.removeView(obj as View)
}

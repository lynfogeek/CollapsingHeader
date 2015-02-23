package fr.arnaud.camus.collapsingheader;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener {
    // Some random countries
    final static String[] DUMMY_DATA = {
            "France",
            "Sweden",
            "Germany",
            "USA",
            "Portugal",
            "The Netherlands",
            "Belgium",
            "Spain",
            "United Kingdom",
            "Mexico",
            "Finland",
            "Norway",
            "Italy",
            "Ireland",
            "Brazil",
            "Japan"
    };

    Toolbar mToolbar;
    View mContainerHeader;
    FloatingActionButton mFab;

    ObjectAnimator fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFab = (FloatingActionButton)findViewById(R.id.favorite);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        ListView listView = (ListView)findViewById(R.id.listview);

        if (mToolbar != null) {
            mToolbar.setTitle(getString(R.string.title));
            setSupportActionBar(mToolbar);
        }

        // Inflate the header view and attach it to the ListView
        View headerView = LayoutInflater.from(this)
                                        .inflate(R.layout.listview_header, listView, false);
        mContainerHeader = headerView.findViewById(R.id.container);
        listView.addHeaderView(headerView);

        // prepare the fade in/out animator
        fade =  ObjectAnimator.ofFloat(mContainerHeader, "alpha", 0f, 1f);
        fade.setInterpolator(new DecelerateInterpolator());
        fade.setDuration(400);

        listView.setOnScrollListener(this);
        listView.setAdapter(new ArrayAdapter<>(this,
                                        android.R.layout.simple_list_item_1,
                                        DUMMY_DATA));
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    /**
     * Listen to the scroll events of the listView
     * @param view the listView
     * @param firstVisibleItem the first visible item
     * @param visibleItemCount the number of visible items
     * @param totalItemCount the amount of items
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onScroll(AbsListView view,
                         int firstVisibleItem,
                         int visibleItemCount,
                         int totalItemCount) {
        // we make sure the list is not null and empty, and the header is visible
        if (view != null && view.getChildCount() > 0 && firstVisibleItem == 0) {

            // we calculate the FAB's Y position
            int translation = view.getChildAt(0).getHeight() + view.getChildAt(0).getTop();
            mFab.setTranslationY(translation>0  ? translation : 0);

            // if we scrolled more than 16dps, we hide the content and display the title
            if (view.getChildAt(0).getTop() < -dpToPx(16)) {
                toggleHeader(false, false);
            } else {
                toggleHeader(true, true);
            }
        } else {
            toggleHeader(false, false);
        }

        // if the device uses Lollipop or above, we update the ToolBar's elevation
        // according to the scroll position.
        if (isLollipop()) {
            if (firstVisibleItem == 0) {
                mToolbar.setElevation(0);
            } else {
                mToolbar.setElevation(dpToPx(4));
            }
        }
    }

    /**
     * Start the animation to fade in or out the header's content
     * @param visible true if the header's content should appear
     * @param force true if we don't wait for the animation to be completed
     *              but force the change.
     */
    private void toggleHeader(boolean visible, boolean force) {
        if ((force && visible) || (visible && mContainerHeader.getAlpha() == 0f)) {
            fade.setFloatValues(mContainerHeader.getAlpha(), 1f);
            fade.start();
        } else if (force || (!visible && mContainerHeader.getAlpha() == 1f)){
            fade.setFloatValues(mContainerHeader.getAlpha(), 0f);
            fade.start();
        }
        // Toggle the visibility of the title.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(!visible);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, m);
        return super.onCreateOptionsMenu(m);
    }

    /**
     * Convert Dps into Pxs
     * @param dp a number of dp to convert
     * @return the value in pixels
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int)(dp * (displayMetrics.densityDpi / 160f));
    }

    /**
     * Check if the device rocks, and runs Lollipop
     * @return true if Lollipop or above
     */
    public static boolean isLollipop() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}

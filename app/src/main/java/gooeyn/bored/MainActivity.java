package gooeyn.bored;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

/*
MAIN ACTIVITY ADDS THE TABS TO THE LAYOUT AND SET SECTIONS PAGE ADAPTER
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INITIALIZE TAB LAYOUT AND ADD TABS
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_people));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_chat));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.icon_menu));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //CREATES NEW SECTIONS PAGE ADAPTER AND SET VIEW PAGER
        final SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        //VIEW PAGES ON PAGE CHANGE LISTENER
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition()); //SET CURRENT ITEM TO SELECTED TAB
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.setCurrentItem(0); //INITIALIZES WITH CURRENT ITEM AS 0
    }

    /*
    SETS THE RESPECTIVE FRAGMENT ACCORDING TO TAB POSITION
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;

        //CONSTRUCTOR. FRAGMENT MANAGER AND THE NUMBER OF TABS
        public SectionsPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        //RETURNS THE RESPECTIVE FRAGMENT WITHIN THE POSITION
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new PeopleFragment();
                case 1:
                    return new ChatFragment();
                case 2:
                    return new MenuFragment();
                default:
                    return null;
            }
        }

        //RETURNS NUMBER OF TABS
        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}

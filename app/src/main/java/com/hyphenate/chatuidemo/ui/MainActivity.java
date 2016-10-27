package com.hyphenate.chatuidemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.ui.sign.SignInActivity;
import com.hyphenate.chatuidemo.ui.user.AddContactsActivity;
import com.hyphenate.chatuidemo.ui.user.ContactListFragment;
import com.hyphenate.chatuidemo.ui.chat.ConversationListFragment;
import com.hyphenate.chatuidemo.ui.settings.SettingsFragment;

import com.hyphenate.chatuidemo.ui.user.InviteMembersActivity;
import com.hyphenate.chatuidemo.ui.user.PublicGroupsListActivity;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wei on 2016/9/27.
 * The main activity of demo app
 */
public class MainActivity extends BaseActivity {
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    @Override protected void onCreate(Bundle savedInstanceState) {
        // Check that you are logged in
        if (EMClient.getInstance().isLoggedInBefore()) {
            // Load the group into memory
            EMClient.getInstance().groupManager().loadAllGroups();
            // Load all conversation into memory
            EMClient.getInstance().chatManager().loadAllConversations();
        } else {
            // Go sign in
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }

        // Set default setting values
        //PreferenceManager.setDefaultValues(this, R.xml.preferences_default, false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_main);
        ButterKnife.bind(this);
        //setup viewpager
        setupViewPager();
        //setup tabLayout with viewpager
        setupTabLayout();
    }

    private void setupViewPager() {
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        //add fragments to adapter
        adapter.addFragment(ContactListFragment.newInstance(), "Contacts");
        adapter.addFragment(ConversationListFragment.newInstance(), "Chats");
        adapter.addFragment(SettingsFragment.newInstance(), "Settings");
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                Toolbar toolbar = getActionBarToolbar();
                toolbar.setTitle(adapter.getPageTitle(position));
                toolbar.getMenu().clear();
                if (position == 0) {
                    getActionBarToolbar().inflateMenu(R.menu.em_contacts_menu);
                } else if (position == 1) {
                    getActionBarToolbar().inflateMenu(R.menu.em_conversations_menu);
                }
            }

            @Override public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
            }

            @Override public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupTabLayout() {
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_indicator));
        mTabLayout.setupWithViewPager(mViewPager);
        //        mTabLayout.setSelectedTabIndicatorHeight(R.dimen.tab_indicator_height);
        for (int i = 0; i < 3; i++) {
            View customTab = LayoutInflater.from(this).inflate(R.layout.em_tab_layout_item, null);
            ImageView imageView = (ImageView) customTab.findViewById(R.id.img_tab_item);
            if (i == 0) {
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.em_tab_contacts_selector));
            } else if (i == 1) {
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.em_tab_chats_selector));
            } else {
                imageView.setImageDrawable(
                        getResources().getDrawable(R.drawable.em_tab_settings_selector));
            }
            //set the custom tabview
            mTabLayout.getTabAt(i).setCustomView(customTab);
        }
    }

    //    @OnClick(R.id.btn_test) void test(){
    //        Toolbar toolbar = getActionBarToolbar();
    //        toolbar.getMenu().clear();
    //        toolbar.inflateMenu(R.menu.test_menu);
    //        toolbar.setOnMenuItemClickListener(new ToolBarMenuItemClickListener());
    //    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //add the action buttons to toolbar
        Toolbar toolbar = getActionBarToolbar();
        if (mViewPager.getCurrentItem() == 0) {
            toolbar.inflateMenu(R.menu.em_contacts_menu);
        } else if (mViewPager.getCurrentItem() == 1) {
            toolbar.inflateMenu(R.menu.em_conversations_menu);
        }
        toolbar.setOnMenuItemClickListener(new ToolBarMenuItemClickListener());
        return true;
    }

    /**
     * Toolbar menu item onclick listener
     */
    private class ToolBarMenuItemClickListener implements Toolbar.OnMenuItemClickListener {

        @Override public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.menu_new_chat:

                    break;

                case R.id.menu_create_group:

                    startActivity(new Intent(MainActivity.this, InviteMembersActivity.class));
                    break;

                case R.id.menu_public_groups:
                    startActivity(new Intent(MainActivity.this, PublicGroupsListActivity.class));
                    
                case R.id.menu_add_contacts:
                    startActivity(new Intent(MainActivity.this, AddContactsActivity.class));
                    break;
            }

            return false;
        }
    }

    /**
     * Fragment pager adapter
     */
    private class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override public int getCount() {
            return mFragments.size();
        }

        @Override public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}

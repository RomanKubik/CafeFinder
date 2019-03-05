package com.example.kubik.cafefinder.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.helpers.ImageConverter;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Activity to configure NavigationDrawer for all activities
 * except LoginActivity and CreateAccountActivity.
 * Created by Kubik on 12/21/16.
 */

public class BaseDrawerCafeActivity extends BaseCafeActivity {

    private static AccountHeader sAccountHeader;
    private static ProfileDrawerItem sProfileItem;
    private static PrimaryDrawerItem item1;
    private static SecondaryDrawerItem item2;

    protected Drawer mDrawer;
    protected Toolbar mToolbar;

    static {
        item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Primary");
        item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Secondary");
        sProfileItem = new ProfileDrawerItem()
                .withName(sProfile.getName())
                .withEmail(sProfile.getEmail())
                .withIcon(ImageConverter.byteToBitmapConverter(sProfile.getPhoto()));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initToolbar(int toolbarId) {
        mToolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(mToolbar);
//        initDrawer();
    }

    private void initDrawer() {
        sAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.logo_cafe_profile_background)
                .withTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .addProfiles(sProfileItem)
                .withSelectionListEnabledForSingleProfile(false)
                .build();
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withSliderBackgroundColorRes(R.color.colorBackground)
                .withToolbar(mToolbar)
                .withAccountHeader(sAccountHeader)
                .addDrawerItems(item1,
                        new DividerDrawerItem(),
                        item2,
                        new SecondaryDrawerItem().withName("Settings"))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.d("MyTag", String.valueOf(position));
                        return true;
                    }
                })
                .build();
    }

}

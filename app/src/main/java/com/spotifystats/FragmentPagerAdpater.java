package com.spotifystats;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    public ScreenSlidePagerAdapter(
            FragmentManager fm)
    {
        super(fm);

    }

    @Override
    public Fragment getItem(int position)
    {

        if (position == 0) {
            return new UserTopArtistsFragment();

        }else if (position == 1){

            return new UserTopTracksFragment();
        }else{
            return null;
        }


    }

    @Override
    public int getCount()
    {
        return 2;
    }
}

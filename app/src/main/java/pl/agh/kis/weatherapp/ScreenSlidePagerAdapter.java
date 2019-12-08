package pl.agh.kis.weatherapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    private MainActivity _mainActivity;

    public ScreenSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
        _mainActivity = (MainActivity)fa;
    }

    @Override
    public Fragment createFragment(int position) {
        return new WeatherSlidePageFragment(_mainActivity, position);
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}

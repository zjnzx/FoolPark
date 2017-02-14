package cn.com.fooltech.smartparking.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.adapter.GuideAdapter;

public class GuideActivity extends BaseActivity {
    @Bind(R.id.vp_app)
    ViewPager mViewPager;
    private int imgId[] = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        initAdapter();

    }

    private void initAdapter() {
        GuideAdapter mAdapter = new GuideAdapter(imgId, this);
        mViewPager.setAdapter(mAdapter);

    }


}

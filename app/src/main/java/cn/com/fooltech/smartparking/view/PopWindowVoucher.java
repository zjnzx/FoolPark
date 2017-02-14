package cn.com.fooltech.smartparking.view;

/**
 * Created by YY on 2016/7/20.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.IndexActivity;
import cn.com.fooltech.smartparking.activity.ScanPayActivity;
import cn.com.fooltech.smartparking.adapter.VoucherPopAdapter;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.VoucherInfo;
import cn.com.fooltech.smartparking.common.Common;
import cn.com.fooltech.smartparking.utils.ToastUtils;

public class PopWindowVoucher extends PopupWindow {


    private View mMenuView;
    private ListView mListView;
    private Button btnUse;
    private WindowManager.LayoutParams lp;
    private Activity activity;

    public PopWindowVoucher(final Activity context, List<VoucherInfo> list) {
        super(context);
        this.activity = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_voucher_layout, null);
        mListView = (ListView) mMenuView.findViewById(R.id.lv_voucher_pop);
        btnUse = (Button) mMenuView.findViewById(R.id.btn_use);

        VoucherPopAdapter mAdapter = new VoucherPopAdapter(context,list);
        mListView.setAdapter(mAdapter);

        //使用
        btnUse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyApplication.voucherMap != null && MyApplication.voucherMap.size() > 0){
                    if(activity instanceof IndexActivity){
                        IndexActivity act = (IndexActivity) activity;
                        act.hideAnimator();
                    }else if(activity instanceof ScanPayActivity){
                        ScanPayActivity act = (ScanPayActivity) activity;
                        act.hideAnimator();
                    }
                    MyApplication.isUse = true;
                    dismiss();
                    MyApplication.voucherId = MyApplication.voucherMap.get("voucherId");
                }else {
                    ToastUtils.showShort(context,"请选择停车券");
                }
            }
        });

        //设置背景透明
        Common.setShadow(context,0.6f);

        //设置按钮监听
//        tvMan.setOnClickListener(itemsOnClick);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.pop_sex_anim);
        //实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#00000000"));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_voucher_lay).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}

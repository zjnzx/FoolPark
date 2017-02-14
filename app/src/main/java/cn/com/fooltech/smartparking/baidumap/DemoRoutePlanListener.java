package cn.com.fooltech.smartparking.baidumap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import cn.com.fooltech.smartparking.application.MyApplication;

/**
 * Created by YY on 2016/7/30.
 */
public class DemoRoutePlanListener  implements BaiduNaviManager.RoutePlanListener{
    private BNRoutePlanNode mBNRoutePlanNode = null;
    private Context context;
    public DemoRoutePlanListener(BNRoutePlanNode node,Context ctx) {
        mBNRoutePlanNode = node;
        context = ctx;
    }

    @Override
    public void onJumpToNavigator() {
        Intent intent = new Intent(context, BNRouteGuideActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MyApplication.ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    @Override
    public void onRoutePlanFailed() {
        // TODO Auto-generated method stub
        Toast.makeText(context, "算路失败", Toast.LENGTH_SHORT).show();
    }
}

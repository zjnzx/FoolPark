package cn.com.fooltech.smartparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.application.MyApplication;
import cn.com.fooltech.smartparking.bean.BindUserInfo;
import cn.com.fooltech.smartparking.cache.BitmapCache;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/7.
 */
public class AccountRelatingAdapter extends BaseAdapter {
    private Context context;
    public List<BindUserInfo> list;
    private LayoutInflater inflater;
    public AccountRelatingAdapter(Context context, List<BindUserInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    public void removeData(int position){
        list.remove(position);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        BindUserInfo info = (BindUserInfo) list.get(i);
        ViewHolder holder = null;
        View view = null;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.listview_account_relating_item,null);
            holder = new ViewHolder();
            holder.tvUserHead = (ImageView) view.findViewById(R.id.user_head_show3);
            holder.tvOwner = (ImageView) view.findViewById(R.id.owner);
            holder.tvNickName = (TextView) view.findViewById(R.id.nick_name_show2);
            holder.tvMobile = (TextView) view.findViewById(R.id.user_mobile);
            holder.tvAmount = (TextView) view.findViewById(R.id.amount1);
            holder.tvDriver = (ImageView) view.findViewById(R.id.driver);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        String balance = Utils.decimalFormat((double) info.getBalance() / 100);
        holder.tvNickName.setText(info.getNickname());
        holder.tvMobile.setText(info.getMobile());
        holder.tvAmount.setText("现有余额  " + balance + "元");

        ImageLoader imageLoader = new ImageLoader(MyApplication.getHttpRequestQueue(), new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.tvUserHead, R.drawable.default_user, R.drawable.default_user);
        if(info.getAvatarUrl().contains("http")) {
            imageLoader.get(info.getAvatarUrl(), listener);
        }

        setOwnerOrDriver(holder.tvOwner,holder.tvDriver,info);

        return view;
    }

    class ViewHolder{
        TextView tvNickName,tvMobile,tvAmount;
        ImageView tvUserHead,tvDriver,tvOwner;
    }

    //设置车主和当前使用者
    private void setOwnerOrDriver(ImageView owner,ImageView driver,BindUserInfo info){
        if(info.getIsOwner() == 0){ //不是车主
            owner.setVisibility(View.GONE);
        }else if(info.getIsDriver() == 1){ //是车主
            owner.setVisibility(View.VISIBLE);
        }
        if(info.getIsDriver() == 0){ //不是当前使用者
            driver.setVisibility(View.GONE);
        }else if(info.getIsDriver() == 1){ //是
            driver.setVisibility(View.VISIBLE);
        }

    }
}

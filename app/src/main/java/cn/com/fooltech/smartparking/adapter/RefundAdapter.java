package cn.com.fooltech.smartparking.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.fooltech.smartparking.R;
import cn.com.fooltech.smartparking.activity.RefundActivity;
import cn.com.fooltech.smartparking.bean.OrderInfo;
import cn.com.fooltech.smartparking.common.Urls;
import cn.com.fooltech.smartparking.utils.HttpUtils;
import cn.com.fooltech.smartparking.utils.SPUtils;
import cn.com.fooltech.smartparking.utils.Utils;

/**
 * Created by YY on 2016/7/7.
 */
public class RefundAdapter extends BaseAdapter {
    private RefundActivity context;
    public List<OrderInfo> list;
    private LayoutInflater inflater;
    public RefundAdapter(RefundActivity context, List<OrderInfo> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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
        final OrderInfo info = (OrderInfo) list.get(i);
        ViewHolder holder = null;
//        if(convertView == null){
            convertView = inflater.inflate(R.layout.listview_refund_item,null);
            holder = new ViewHolder();
            holder.tvOrderNo = (TextView) convertView.findViewById(R.id.order_no);
            holder.tvOrderAmount = (TextView) convertView.findViewById(R.id.order_amount);
            holder.tvRefundAmount = (TextView) convertView.findViewById(R.id.refund_amount);
            holder.tvOrderTime = (TextView) convertView.findViewById(R.id.order_time);
            holder.tvPayment = (TextView) convertView.findViewById(R.id.payment);
            holder.etAmount = (EditText) convertView.findViewById(R.id.amount);
            holder.btnSumit = (Button) convertView.findViewById(R.id.submit_refund);
            holder.rbAlipay = (RadioButton) convertView.findViewById(R.id.alipay);
            holder.rbWxpay = (RadioButton) convertView.findViewById(R.id.wxpay);
            convertView.setTag(holder);
//        }else{
//            holder = (ViewHolder) convertView.getTag();
//        }


        String refundAmount = Utils.decimalFormat((double) info.getRefundAmount() / 100);
        String orderAmount = Utils.decimalFormat((double) info.getOrderAmount() / 100);
        holder.etAmount.setText("");
        holder.tvOrderNo.setText("账单号: " + info.getOrderNo());
        holder.tvOrderAmount.setText("账单金额: " + orderAmount + "元");
          holder.tvRefundAmount.setText("可提金额: " + refundAmount + "元");
        holder.tvOrderTime.setText(info.getOrderTime());
        if(info.getOrderPayment() == 1){
            holder.rbWxpay.setChecked(true);
        }else if(info.getOrderPayment() == 2){
            holder.rbAlipay.setChecked(true);
        }

        final ViewHolder finalHolder1 = holder;
        holder.etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String amount = editable.toString();
                if(!("").equals(amount)){
                    finalHolder1.btnSumit.setBackgroundResource(R.drawable.selector_btn_click);
                    finalHolder1.btnSumit.setEnabled(true);
                    finalHolder1.btnSumit.setTextColor(context.getResources().getColor(R.color.white));
                }else{
                    finalHolder1.btnSumit.setBackgroundResource(R.drawable.btn_submit_bg);
                    finalHolder1.btnSumit.setEnabled(false);
                    finalHolder1.btnSumit.setTextColor(context.getResources().getColor(R.color.submit));
                }
            }
        });

        //提交
        final ViewHolder finalHolder = holder;
        holder.btnSumit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountStr = finalHolder.etAmount.getText().toString();
                int amount = (int) (Utils.strToDouble(amountStr) * 100);
                showDialog(amount,info);
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView tvOrderNo,tvOrderAmount,tvRefundAmount,tvOrderTime,tvPayment;
        EditText etAmount;
        Button btnSumit;
        RadioButton rbAlipay,rbWxpay;
    }

    /**
     * 显示对话框
     */
    private void showDialog(final int amount, final OrderInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true)
                .setMessage(context.getResources().getString(R.string.dialog_refund))
                .setPositiveButton("提现", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.refund(info.getOrderNo(),amount);
                    }
                })
                .setNegativeButton("点错了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

}

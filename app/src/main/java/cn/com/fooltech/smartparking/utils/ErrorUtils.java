package cn.com.fooltech.smartparking.utils;

import android.content.Context;
import android.content.Intent;

import cn.com.fooltech.smartparking.activity.LoginActivity;

/**
 * Created by YY on 2016/8/11.
 */
public class ErrorUtils {

    public static void errorCode(Context context,int code){
        switch (code){
            case 10001:
                ToastUtils.showShort(context,"远程服务器接口错误");
                break;
            case 10002:
                ToastUtils.showShort(context,"请求方法错误");
                break;
            case 10003:
                ToastUtils.showShort(context,"请求参数类型错误");
                break;
            case 10004:
                ToastUtils.showShort(context,"请求方法无效");
                break;
            case 20001:
                ToastUtils.showShort(context,"缺少必要参数");
                break;
            case 20002:
                ToastUtils.showShort(context,"传入参数错误");
                break;
            case 20003:
                ToastUtils.showShort(context,"签名验证错误");
                break;
            case 30001:
                ToastUtils.showShort(context,"手机号码已被注册");
                break;
            case 30002:
                ToastUtils.showShort(context,"验证码失效或手机号码错误");
                break;
            case 30003:
                ToastUtils.showShort(context,"用户登录信息错误");
                break;
            case 30004:
                ToastUtils.showShort(context,"当前用户尚未登录");
                break;
            case 30005:
                context.startActivity(new Intent(context, LoginActivity.class));
                ToastUtils.showShort(context,"Token已过期,请重新登录");
                break;
            case 30006:
                ToastUtils.showShort(context,"上传图像超出限制，最大1MB");
                break;
            case 30007:
                ToastUtils.showShort(context,"不支持的图像类型，仅支持PNG和JPG");
                break;
            case 30008:
                ToastUtils.showShort(context,"提供的手机号码或ID尚未注册");
                break;
            case 30009:
                ToastUtils.showShort(context,"邀请码无效");
                break;
            case 30099:
                ToastUtils.showShort(context,"该手机号码已被列入黑名单");
                break;
            case 40001:
                ToastUtils.showShort(context,"该停车场ID不存在");
                break;
            case 40002:
                ToastUtils.showShort(context,"该停车场暂不支持预订");
                break;
            case 40003:
                ToastUtils.showShort(context,"该停车场尚未更新车位状态信息");
                break;
            case 40004:
                ToastUtils.showShort(context,"该停车场已没有空余车位");
                break;
            case 40005:
                ToastUtils.showShort(context,"无法找到匹配的停车记录信息");
                break;
            case 40006:
                ToastUtils.showShort(context,"不能重复预约车位");
                break;
            case 40007:
                ToastUtils.showShort(context,"无法找到匹配的预约记录信息");
                break;
            case 40008:
                ToastUtils.showShort(context,"当前停车场已经收藏");
                break;
            case 40009:
                ToastUtils.showShort(context,"账户余额不足");
                break;
            case 40011:
                ToastUtils.showShort(context,"指定的车位编号错误");
                break;
            case 40012:
                ToastUtils.showShort(context,"指定的收藏停车场记录不存在");
                break;
            case 40013:
                ToastUtils.showShort(context,"积分为负，无法预约车位");
                break;
            case 40014:
                ToastUtils.showShort(context,"当前记录处于预订状态，无法删除");
                break;
            case 40015:
                ToastUtils.showShort(context,"已购买过月卡，不能重复购买");
                break;
            case 50001:
                ToastUtils.showShort(context,"超出绑定数量限制，最多绑定3个车牌");
                break;
            case 50002:
                ToastUtils.showShort(context,"不能重复绑定");
                break;
            case 50003:
                ToastUtils.showShort(context,"当前车辆已被其他用户绑定，需要车主授权");
                break;
            case 50004:
                ToastUtils.showShort(context,"无法找到匹配的车牌号码,请在首页选择指定车辆");
                break;
            case 50005:
                ToastUtils.showShort(context,"该用户ID没有绑定指定车辆");
                break;
            case 50006:
                ToastUtils.showShort(context,"无法找到匹配的申请绑定消息记录");
                break;
            case 50007:
                ToastUtils.showShort(context,"当前车辆账号尚有余额，无法解绑");
                break;
            case 50008:
                ToastUtils.showShort(context,"您没有操作权限");
                break;
            case 50009:
                ToastUtils.showShort(context,"该车辆已申请解绑，暂时无法绑定");
                break;
            case 60001:
                ToastUtils.showShort(context,"没有找到匹配的订单记录");
                break;
            case 60002:
                ToastUtils.showShort(context,"当前用户与订单记录不匹配");
                break;
            case 60003:
                ToastUtils.showShort(context,"提交提现申请失败");
                break;
            case 60004:
                ToastUtils.showShort(context,"提现金额超出用户可用余额");
                break;
            case 60005:
                ToastUtils.showShort(context,"提现金额超出当前订单金额");
                break;
            case 60006:
                ToastUtils.showShort(context,"提现金额超出可用积分");
                break;
            case 60007:
                ToastUtils.showShort(context,"代金券无效");
                break;
            case 60008:
                ToastUtils.showShort(context,"费用已缴，不能重复缴费");
                break;
            case 60009:
                ToastUtils.showShort(context,"用户违规，扣除1000积分");
                break;
            case 60010:
                ToastUtils.showShort(context,"当前积分低于-2000，直接拉黑");
                break;
            case 60011:
                ToastUtils.showShort(context,"积分不足");
                break;
            case 60012:
                ToastUtils.showShort(context,"输入金额/ 数值不能为负");
                break;
            case 60013:
                ToastUtils.showShort(context,"未检索到付款信息");
                break;
            case 60014:
                ToastUtils.showShort(context,"订单支付失败");
                break;
            case 60015:
                ToastUtils.showShort(context,"单次停车仅可使用一张停车券");
                break;
            case 70001:
                ToastUtils.showShort(context,"活动记录不存在");
                break;
            case 99999:
                ToastUtils.showShort(context,"系统维护");
                break;
            case -1:
                ToastUtils.showShort(context,"服务器请求错误");
                break;
        }
    }

}

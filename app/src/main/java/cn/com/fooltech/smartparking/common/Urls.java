package cn.com.fooltech.smartparking.common;

/**
 * Created by YY on 2016/7/19.
 */
public class Urls {

//        private static String HTTP = "https://";
//    private static String HOST = "api.fooltech.com.cn";
    private static String HTTP = "http://";   //测试
    private static String HOST = "192.168.1.199";

    public static final  String URL_WEATHER = "http://api.map.baidu.com/telematics/v3/weather"; //首页天气

    public static final  String URL_LOGIN = HTTP + HOST + "/user/v2/login"; //登录
    public static final  String URL_REGISTER = HTTP + HOST + "/user/v2/register"; //注册
    public static final  String URL_MOBILE_CODE = HTTP + HOST + "/user/v2/getValidCode"; //获取验证码
    public static final  String URL_INDEX_BANNER = HTTP + HOST + "/app/v1/getBannerList"; //首页banner
    public static final  String URL_SPACE_STATUS = HTTP + HOST + "/park/v1/getSpaceStatus"; //车位状态
    public static final  String URL_PARK_INFO = HTTP + HOST + "/park/v1/getParkInfo"; //停车场详情
    public static final  String URL_PARK_LIST = HTTP + HOST + "/park/v1/getParkListByLoc"; //根据当前位置获取停车场列表
    public static final  String URL_POI_LIST = HTTP + HOST + "/park/v1/getPoiListByLoc"; //根据当前位置获取兴趣点
    public static final  String URL_SEARCH_PARK = HTTP + HOST + "/park/v1/getParkListByName"; //根据名称搜索停车场
    public static final  String URL_FEEDBACK = HTTP + HOST + "/app/v1/feedback"; //反馈
    public static final  String URL_COLLECT_PARK = HTTP + HOST + "/park/v1/collect"; //停车场收藏
    public static final  String URL_UNCOLLECT_PARK = HTTP + HOST + "/park/v1/uncollect"; //取消停车场收藏
    public static final  String URL_GET_COLLECT_PARK = HTTP + HOST + "/park/v1/getCollectParks"; //获取收藏记录列表
    public static final  String URL_BOOK_SPACE = HTTP + HOST + "/park/v1/bookSpace"; //预定车位
    public static final  String URL_REMOVE_BOOK_SPACE = HTTP + HOST + "/park/v1/removeBookRecord"; //删除预定车位
    public static final  String URL_CANCLE_BOOK_SPACE = HTTP + HOST + "/park/v1/cancelBook"; //取消预定车位
    public static final  String URL_GET_BOOK_RECORD = HTTP + HOST + "/park/v1/getBookRecord"; //获取预定记录
    public static final  String URL_RESET_PASSWORD = HTTP + HOST + "/user/v1/resetPassword"; //重置密码
    public static final  String URL_LOGOUT = HTTP + HOST + "/user/v1/logout"; //注销登录
    public static final  String URL_CHANGE_MOBILE = HTTP + HOST + "/user/v1/changeMobile"; //更换手机号
    public static final  String URL_CHANGE_USER_HEAD = HTTP + HOST + "/user/v1/changeHeadPic"; //更换用户头像
    public static final  String URL_UPDATE_USER_INFO = HTTP + HOST + "/user/v1/updateUserInfo"; //更换用户基本信息
    public static final  String URL_GET_USER_INFO = HTTP + HOST + "/user/v2/getUserInfo"; //获取用户基本信息
    public static final  String URL_GET_VOUCHER = HTTP + HOST + "/user/v1/getVoucherList"; //获取用户代金券列表
    public static final  String URL_GET_POINT = HTTP + HOST + "/user/v1/getPointsDetail"; //获取积分明细列表
    public static final  String URL_BIND_CAR = HTTP + HOST + "/car/v1/bind"; //绑定车牌
    public static final  String URL_UNBIND = HTTP + HOST + "/car/v1/unbind"; //解除绑定
    public static final  String URL_FORCE_UNBIND = HTTP + HOST + "/car/v1/forceUnbind"; //强行解除绑定
    public static final  String URL_APPLY_BIND = HTTP + HOST + "/car/v1/applyBind"; //申请绑定
    public static final  String URL_REJECT_BIND = HTTP + HOST + "/car/v1/rejectBind"; //拒绝绑定
    public static final  String URL_ALLOW_BIND = HTTP + HOST + "/car/v1/allowBind"; //授权绑定
    public static final  String URL_REGAIN_CAR = HTTP + HOST + "/user/v1/regainCar"; //申请找回
    public static final  String URL_GET_BIND_CAR = HTTP + HOST + "/user/v1/getBindCarList"; //获取用户绑定车辆信息
    public static final  String URL_SET_DRIVER = HTTP + HOST + "/car/v1/setDriver"; //设置成为车辆当前使用用户
    public static final  String URL_GET_ACCESS_TOKEN = HTTP + HOST + "/user/v1/getAccessToken"; //用户token续期
    public static final  String URL_GET_DRIVER = HTTP + HOST + "/car/v1/getDriver"; //获取车辆当前用户
    public static final  String URL_GET_BIND_USERS = HTTP + HOST + "/car/v1/getBindUsers"; //获取当前所有关联用户
    public static final  String URL_GET_ACTIVITY_LIST = HTTP + HOST + "/app/v1/getActivityList"; //获取活动列表
    public static final  String URL_UPDATE_ACTIVITY_COUNT = HTTP + HOST + "/app/v1/updateActivityCount"; //活动点赞
    public static final  String URL_GET_SYSTEM_MESSAGE = HTTP + HOST + "/message/v1/getSysMessages"; //获取系统消息列表
    public static final  String URL_GET_USER_MESSAGE = HTTP + HOST + "/message/v1/getUserMessages"; //获取用户消息列表
    public static final  String URL_DELETE_USER_MESSAGE = HTTP + HOST + "/message/v1/deleteUserMessage"; //删除用户消息
    public static final  String URL_DELETE_SYS_MESSAGE = HTTP + HOST + "/message/v1/deleteSysMessage"; //删除系统消息
    public static final  String URL_EXCHANGE_VOUCHER = HTTP + HOST + "/trade/v1/exchange"; //兑换停车券
    public static final  String URL_RECHARGE = HTTP + HOST + "/trade/v1/recharge"; //用户充值
    public static final  String URL_REFUND = HTTP + HOST + "/trade/v1/refund"; //用户提现
    public static final  String URL_GET_REFUND = HTTP + HOST + "/trade/v1/getRefundInfo"; //获取个人提现信息
    public static final  String URL_DETAIL = HTTP + HOST + "/trade/v1/detail"; //获取交易明细记录
    public static final  String URL_GET_PAYINFO = HTTP + HOST + "/user/v1/getPayInfo"; //获取付款信息
    public static final  String URL_GET_POSITION_RECORD = HTTP + HOST + "/car/v1/getPositionRecord"; //获取停车记录
    public static final  String URL_REMOVE_POSITION_RECORD = HTTP + HOST + "/car/v1/removePosition"; //删除停车记录
    public static final  String URL_GET_PARK_INFO = HTTP + HOST + "/car/v1/getParkingInfo"; //获取停车信息
    public static final  String URL_GET_EXCHANGE_RECORD = HTTP + HOST + "/trade/v1/getExchangeRecord"; //获取兑换记录
    public static final  String URL_PAY = HTTP + HOST + "/user/v1/pay"; //付款
    public static final  String URL_GET_FREE_SPACE = HTTP + HOST + "/park/v1/getNearestFreeSpace"; //获取最近的空闲车位
    public static final  String URL_GET_FLOOR = HTTP + HOST + "/park/v1/getFloorList"; //获取停车场楼层信息
    public static final  String URL_ADD_POSITION = HTTP + HOST + "/car/v1/addPosition"; //添加停车位
    public static final  String URL_SCAN_PAY = HTTP + HOST + "/user/v1/scanPay"; //扫码缴费
    public static final  String URL_GET_APP_INFO = HTTP + HOST + "/app/v1/getAppBaseInfo"; //app信息
    public static final  String URL_GET_LINK = HTTP + HOST + "/app/v1/getAppLinkUrl"; //获取html页面链接
    public static final  String URL_GET_PAY_RESULT = HTTP + HOST + "/trade/v1/queryPayResult"; //获取支付结果
    public static final  String URL_GET_LEASABLE_PARK = HTTP + HOST + "/lease/v1/getLeasableParks"; //获取支持月卡停车场列表
    public static final  String URL_BUY_CARD = HTTP + HOST + "/lease/v1/buyCard"; //购买月卡
    public static final  String URL_SUBMIT_COMMENT = HTTP + HOST + "/user/v1/submitComment"; //提交评论
    public static final  String URL_GET_COMMENT_LIST = HTTP + HOST + "/park/v1/getCommentList"; //获取评论列表
    public static final  String URL_SUPPORT_COMMENT = HTTP + HOST + "/user/v1/supportComment"; //支持评论内容
    public static final  String URL_CARD_LIST = HTTP + HOST + "/user/v1/getCardList"; //获取已购买的月卡
    public static final  String URL_GET_SHARE_URL = HTTP + HOST + "/app/v1/getShareUrl"; //获取分享好友链接地址
}

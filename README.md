# cordova-qdc-wxpay
微信APP支付cordova,ionic插件(Android版，Ios版)

* 2015.06.30 微信支付Android及IOS版集成，初步完成
* 2015.06.26 微信Android SDK【2014.12.12】

# 1. Android客户端安装
开发工程下执行以下命令导入本插件：

	$ ionic plugin add https://github.com/mrwutong/cordova-qdc-wxpay.git

已安装插件查看：

	$ionic plugin list


执行以下命令删本插件：

	# 【com.qdc.plugins.wxpay】是插件ID，不是插件文件夹名
	$ionic plugin rm com.qdc.plugins.wxpay

## 1.1 Android开发环境导入--Eclipse
导入路径：开发工程->platform->android

## 1.2 IOS开发环境导入--Xcode
导入路径：开发工程->platform->ios

确认没有编译错误。

## 1.3 JS调用说明

* 事先前调用后台预支付API生成订单数据及签名数据
* 调用plugin的JS方法【wxpay.payment】进行支付
>     **wxpay.payment(json, cb_success, cb_failure);**
>     # 参数说明：格式为JSON格式
>     # cb_success:调用成功回调方法
>     # cb_failure:调用失败回调方法
>     {
>      appid: 公众账号ID
>      noncestr: 随机字符串
>      package: 扩展字段
>      partnerid: 商户号
>      prepayid: 预支付交易会话ID
>      timestamp: 时间戳
>      sign: 签名
>     }
>    注：订单总金额，只能为整数，单位为【分】，参数值不能带小数。



# cordova-qdc-wxpay
微信APP支付cordova插件(Android版)

-- 2015.06.26 微信Android SDK【L2-4.4.1】

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

事先前调用后台生成API生成订数据

调用微信支付API【/api/wx/unifiedorder】，生成预支付订单及加密数据

	参数：{body:商品描述,out_trade_no:商户订单号,total_fee:总金额}
	注：订单总金额，只能为整数，单位为【分】，参数值不能带小数。


利用上步的返回结果调用，wxpay.payment

	wxpay.payment([], cb_success, cb_failure);
	# 参数说明：格式为JSON格式字符串
	# appid: 公众账号ID
	# noncestr: 随机字符串
	# package: 扩展字段
	# partnerid: 商户号
	# prepayid: 预支付交易会话ID
	# timestamp: 时间戳
	# sign: 签名
	# cb_success:调用成功回调方法
	# cb_failure:调用失败回调方法


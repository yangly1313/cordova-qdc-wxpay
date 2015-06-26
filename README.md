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

wxpay.payment

	wxpay.payment([], cb_success, cb_failure);
	# 参数说明：格式[数组]，要求保证参数传入顺序
	# [0]:订单编号
	# [1]:商品描述
	# [2]:总金额 **整体，单位分
	# cb_success:调用成功回调方法
	# cb_failure:调用失败回调方法


var exec = require('cordova/exec');

var wxpay = {
  payment: function(args, successFn, failureFn) {
    exec(successFn, failureFn, 'WeixinPay', 'payment', args);
  }
}

module.exports = wxpay;

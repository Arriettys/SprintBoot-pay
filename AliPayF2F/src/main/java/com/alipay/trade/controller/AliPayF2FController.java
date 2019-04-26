package com.alipay.trade.controller;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.trade.result.AlipayF2FPayResult;
import com.alipay.trade.result.AlipayF2FPrecreateResult;
import com.alipay.trade.result.AlipayF2FQueryResult;
import com.alipay.trade.result.AlipayF2FRefundResult;
import com.alipay.trade.service.IAlipayTradeService;
import com.alipay.trade.utils.ZxingUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/alipay")
public class AliPayF2FController
{	
	
	@Autowired
    private IAlipayTradeService alipayTradeService;

	@ApiOperation(value="测试当面付2.0支付", notes="按照json格式输入参数，完整参数可参考：https://openhome.alipay.com/platform/demoManage.htm#/alipay.trade.pay  \n"
			+ "以下为json格式例子：  \n"
			+ "ps.订单号outTradeNo怕你不知道怎么设计，已经帮你内置好了"
			+ "{  \n"
			+ "-----\"scene\":\"\" //说明：支付场景 条码支付，取值：bar_code 声波支付，取值：wave_code  \n" + 
			"-------\"subject\":\"xxx品牌xxx门店当面付消费\",//必填 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”  \n" + 
			"-------\"totalAmount\":\"0.01\",//(必填) 订单总金额，单位为元，不能超过1亿元  \n" + 
			"-------\"authCode\":\"用户自己的支付宝付款码\", // (必填) 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码,条码示例，286648048691290423，另外付款码是临时生成的，不同时间是不同的  \n" + 
			"-------//可选部分  \n" + 
			"-------// (可选，根据需要决定是否使用) 订单可打折金额，可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折  \n" + 
			"-------// 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】  \n" + 
			"-------//\"discountableAmount\":\"0.01\",  \n" + 
			"-------// (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段  \n" + 
			"-------// 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】  \n" + 
			"-------\"undiscountableAmount\":\"0.0\",  \n" + 
			"-------// 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)  \n" + 
			"-------// 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID  \n" + 
			"-------\"sellerId\":\"\",  \n" + 
			"-------// 订单描述，可以对交易或商品进行一个详细地描述，比如填写\"购买商品3件共20.00元\"  \n" + 
			"-------\"body\":\"购买商品3件共20.00元\",  \n" + 
			"-------// 商户操作员编号，添加此参数可以为商户操作员做销售统计  \n" + 
			"-------\"operatorId\":\"test_operator_id\",  \n" + 
			"-------// 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持  \n" + 
			"-------\"storeId\":\"test_store_id\",  \n" + 
			"-------// 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持  \n" + 
			"-------\"extendParams\":{\"sysServiceProviderId\":\"2088100200300400500\"},  \n" + 
			"-------// 支付超时，线下扫码交易定义为5分钟  \n" + 
			"-------\"timeoutExpress\":\"5m\",  \n" + 
			"-------// 商品明细列表，需填写购买商品详细信息，  \n" + 
			"-------\"goods_detail\":  \n" + 
			"--------------[  \n" + 
			"---------------------{  \n" + 
			"----------------------------\"goods_id\":\"goods_id001\",  \n" + 
			"----------------------------\"goods_name\":\"xxx面包\",  \n" + 
			"----------------------------\"quantity\":1,\"price\":\"10\"  \n" + 
			"---------------------},  \n" + 
			"---------------------{  \n" + 
			"----------------------------\"goods_id\":\"goods_id002\",  \n" + 
			"----------------------------\"goods_name\":\"xxx牙刷\",  \n" + 
			"----------------------------\"quantity\":2,\"price\":\"5\"  \n" + 
			"---------------------}  \n" + 
			"--------------],    \n" + 
			"	}  \n"
			+ "ps.以上'-'代表的是空格键，不知道为什么会被压缩掉，所以用这个代替  \n"
			+ "有的时候会回调到你的手机沙盒上让你输入密码，不知道是不是时间拖太长的缘故，还是只有第一次要这样")
	@ApiImplicitParam(name = "model", value = "参数集", required = true, dataType = "com.alipay.api.domain.AlipayTradePayModel")
	@RequestMapping(value="/pay", method = RequestMethod.POST)
	public String tradePay (@RequestBody AlipayTradePayModel model)
	{     
		if (StringUtils.isEmpty(model.getOutTradeNo()))
		{
			model.setOutTradeNo("tradepay" + System.currentTimeMillis()
	        + (long) (Math.random() * 10000000L));
		}	
		if (StringUtils.isEmpty(model.getScene())) 
		{
            return "scene should not be NULL!";
        }
        if (StringUtils.isEmpty(model.getAuthCode())) 
        {
            return "auth_code should not be NULL!";
        }
        if (!Pattern.matches("^\\d{10,}$", model.getAuthCode())) 
        {
            return "invalid auth_code!";
        }if (StringUtils.isEmpty(model.getSubject())) 
        {
            return "subject should not be NULL!";
        }		
		
        // 调用tradePay方法获取当面付应答
        AlipayF2FPayResult result = alipayTradeService.tradePay(model);
        switch (result.getTradeStatus()) 
        {
            case SUCCESS:
                return "支付宝支付成功";
            case FAILED:
                return "支付宝支付失败!!!";

            case UNKNOWN:
                return "系统异常，订单状态未知!!!";

            default:
                return "不支持的交易状态，交易返回异常!!!";
        }
	}
	
	@ApiOperation(value="测试当面付2.0查询订单", notes="说明：订单支付时传入的商户订单号,和支付宝交易号不能同时为空。 trade_no,out_trade_no如果同时存在优先取trade_no  \n"
			+ "订单号可通过手机沙盒钱包查询交易记录得到，完整参数集：https://openhome.alipay.com/platform/demoManage.htm#/alipay.trade.query  \n"
			+ "格式：  \n"
			+ "{  \n"
			+ "-----\"outTradeNo\":\"\",  \n"
			+ "-----\"tradeNo\":\"\"  \n"
			+ "}")
	@ApiImplicitParam(name = "model", value = "参数集", required = true, dataType = "AlipayTradeQueryModel")
	@RequestMapping(value="/query", method = RequestMethod.POST)
	public String tradeQuery (@RequestBody AlipayTradeQueryModel model)
	{
		if (StringUtils.isEmpty(model.getOutTradeNo()) && StringUtils.isEmpty(model.getTradeNo()))
		{
			return "out_trade_no and trade_no can't be empty at the same time";
		}
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(model);
        System.out.println(request.getBizContent());
		 AlipayF2FQueryResult result = alipayTradeService.queryTradeResult(model);
	     switch (result.getTradeStatus()) 
	     {
	     	case SUCCESS:
	        	AlipayTradeQueryResponse response = result.getResponse();
	        	return "查询返回该订单支付成功\n" + response.getTradeStatus();
	     	case FAILED: return "查询返回该订单支付失败或被关闭!!!";
	     	case UNKNOWN: return "系统异常，订单支付状态未知!!!";
	    	default: return "不支持的交易状态，交易返回异常!!!";
	     }		
	}
	
	@ApiOperation(value="测试当面付2.0生成支付二维码", notes="说明：生成二维码后，展示给用户，由用户扫描二维码完成订单支付。  \n"
			+ "完整参数集：https://openhome.alipay.com/platform/demoManage.htm#/alipay.trade.precreate  \n"
			+ "ps.实验前需在controller的tradePrecreate()中填写好二维码存放路径  \n"
			+ "格式：  \n"
			+ "{  \n"
			+ "-----（必填项）  \n"
			+ "-----\"outTradeNo\":\"\",//同样，这里的订单也帮你内置了，不填也可以  \n"
			+ "-----\"subject\":\"\"  \n"
			+ "-----\"totalAmount\":\"\"  \n"
			+ "... //可选项  \n"
			+ "}")
	@ApiImplicitParam(name = "model", value = "参数集", required = true, dataType = "AlipayTradePrecreateModel")
	@RequestMapping(value="/precreate", method = RequestMethod.POST)
	public String tradePrecreate (@RequestBody AlipayTradePrecreateModel model)
	{
		if (StringUtils.isEmpty(model.getOutTradeNo()))
		{
			model.setOutTradeNo("tradepay" + System.currentTimeMillis()
	        + (long) (Math.random() * 10000000L));
		}	
		if (StringUtils.isEmpty(model.getSubject())) 
        {
            return "subject should not be NULL!";
        }
		if (StringUtils.isEmpty(model.getTotalAmount()))
		{
			return "total_amount should not be NULL!";
		}

		AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(model);
		switch (result.getTradeStatus()) 
		{
	        case SUCCESS:	
	            AlipayTradePrecreateResponse response = result.getResponse();	      	
	            // 需要修改为运行机器上的路径
	            String filePath = String.format("/home/img/qr-%s.png",
	                response.getOutTradeNo());
	            if (StringUtils.isEmpty(filePath))
	            {
	            	return "QRcode filePath should not be NULL!";
	            }
	            ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
	            return "支付宝预下单成功";
	
	        case FAILED: return "支付宝预下单失败!!!";
	
	        case UNKNOWN: return "系统异常，预下单状态未知!!!";
	
	        default: return "不支持的交易状态，交易返回异常!!!";
		}
	}
	
	@ApiOperation(value="测试当面付2.0退货", notes="说明：需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数，建议事先沙盒钱包查看下订单金额，以防超过。订单支付时传入的商户订单号,和支付宝交易号不能同时为空。  \n"
			+ "完整参数集：https://openhome.alipay.com/platform/demoManage.htm#/alipay.trade.refund  \n"
			+ "格式：  \n"
			+ "{  \n"
			+ "-----（必填项）  \n"
			+ "-----\"outTradeNo\":\"\",  \n"
			+ "-----\"tradeNo\":\"\"  \n"
			+ "-----\"refundAmount\":\"\"  \n"
			+ "... //可选项  \n"
			+ "}")
	@ApiImplicitParam(name = "model", value = "参数集", required = true, dataType = "AlipayTradeRefundModel")
	@RequestMapping(value="/refund", method = RequestMethod.POST)
	public String tradeRefund (@RequestBody AlipayTradeRefundModel model)
	{
		if (StringUtils.isEmpty(model.getRefundAmount()))
		{
			return "refund_amount should not be NULL!";
		}	
		if (StringUtils.isEmpty(model.getOutTradeNo()) && StringUtils.isEmpty(model.getTradeNo()))
		{
			return "out_trade_no and trade_no can't be empty at the same time";
		}	
		AlipayF2FRefundResult result = alipayTradeService.tradeRefund(model);
		switch (result.getTradeStatus()) 
		{
	        case SUCCESS: return "支付宝退款成功: )";
	
	        case FAILED: return "支付宝退款失败!!!";
	
	        case UNKNOWN: return "系统异常，订单退款状态未知!!!";
	
	        default: return "不支持的交易状态，交易返回异常!!!";
	    }
	}
}

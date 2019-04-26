package com.alipay.trade.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.trade.hb.Constants;
import com.alipay.trade.hb.statusEnum.TradeStatus;
import com.alipay.trade.properties.AlipayInfo;
import com.alipay.trade.result.AlipayF2FPayResult;
import com.alipay.trade.result.AlipayF2FPrecreateResult;
import com.alipay.trade.result.AlipayF2FQueryResult;
import com.alipay.trade.result.AlipayF2FRefundResult;
@Service
public class AlipayTradeService extends AbsAlipayTradeService
{
	@Autowired
	private AlipayInfo alipayInfo; 
	
	// 商户可以直接使用的pay方法
	@Override
	public AlipayF2FPayResult tradePay(AlipayTradePayModel payModel)
	{
		if (client==null)
		{
			client = new DefaultAlipayClient(alipayInfo.getOpenApiDomain(), alipayInfo.getAppid(), alipayInfo.getPrivateKey(),
					alipayInfo.getFormat(), alipayInfo.getCharset(), alipayInfo.getAlipayPublicKey(), alipayInfo.getSignType());
		}
		final String outTradeNo = payModel.getOutTradeNo();
		AlipayTradePayRequest request = new AlipayTradePayRequest();
        // 设置平台参数
        request.setNotifyUrl(alipayInfo.getNotifyUrl());
        String appAuthToken = alipayInfo.getAppAuthToken();
        // todo 等支付宝sdk升级公共参数后使用如下方法
        // request.setAppAuthToken(appAuthToken);
        request.putOtherTextParam("app_auth_token", alipayInfo.getAppAuthToken());

        // 设置业务参数
        request.setBizModel(payModel);

        // 首先调用支付api
        AlipayTradePayResponse response = (AlipayTradePayResponse) getResponse(client, request);

        AlipayF2FPayResult result = new AlipayF2FPayResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) 
        {
            // 支付交易明确成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (response != null && Constants.PAYING.equals(response.getCode())) 
        {
            // 返回用户处理中，则轮询查询交易是否成功，如果查询超时，则调用撤销
        	AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
        	queryModel.setOutTradeNo(outTradeNo);
			AlipayTradeQueryResponse loopQueryResponse = loopQueryResult(queryModel, alipayInfo);
            return checkQueryAndCancel(outTradeNo, appAuthToken, result, loopQueryResponse);

        } else if (tradeError(response)) 
        {
            // 系统错误，则查询一次交易，如果交易没有支付成功，则调用撤销
        	AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
        	queryModel.setOutTradeNo(outTradeNo);
            AlipayTradeQueryResponse queryResponse = tradeQuery(queryModel);
            return checkQueryAndCancel(outTradeNo, appAuthToken, result, queryResponse);

        } else 
        {
            // 其他情况表明该订单支付明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
	}
	
	@Override
	public AlipayF2FQueryResult queryTradeResult(AlipayTradeQueryModel model)
	{		
		if (client==null)
		{
			client = new DefaultAlipayClient(alipayInfo.getOpenApiDomain(), alipayInfo.getAppid(), alipayInfo.getPrivateKey(),
					alipayInfo.getFormat(), alipayInfo.getCharset(), alipayInfo.getAlipayPublicKey(), alipayInfo.getSignType());
		}
		AlipayTradeQueryResponse response = tradeQuery(model);

        AlipayF2FQueryResult result = new AlipayF2FQueryResult(response);
        if (querySuccess(response)) 
        {
            // 查询返回该订单交易支付成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) {
            // 查询发生异常，交易状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else 
        {
            // 其他情况均表明该订单号交易失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
	}
	
	@Override
	public AlipayF2FPrecreateResult tradePrecreate(AlipayTradePrecreateModel preModel)
	{
		if (client==null)
		{
			client = new DefaultAlipayClient(alipayInfo.getOpenApiDomain(), alipayInfo.getAppid(), alipayInfo.getPrivateKey(),
					alipayInfo.getFormat(), alipayInfo.getCharset(), alipayInfo.getAlipayPublicKey(), alipayInfo.getSignType());
		}
		AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(alipayInfo.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", alipayInfo.getAppAuthToken());
        request.setBizModel(preModel);

        AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) getResponse(client, request);

        AlipayF2FPrecreateResult result = new AlipayF2FPrecreateResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) 
        {
            // 预下单交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) 
        {
            // 预下单发生异常，状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else 
        {
            // 其他情况表明该预下单明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
	}
	
	@Override
    public AlipayF2FRefundResult tradeRefund(AlipayTradeRefundModel model) 
	{
		if (client==null)
		{
			client = new DefaultAlipayClient(alipayInfo.getOpenApiDomain(), alipayInfo.getAppid(), alipayInfo.getPrivateKey(),
					alipayInfo.getFormat(), alipayInfo.getCharset(), alipayInfo.getAlipayPublicKey(), alipayInfo.getSignType());
		}
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setNotifyUrl(alipayInfo.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", alipayInfo.getAppAuthToken());
        request.setBizModel(model);

        AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) getResponse(client, request);

        AlipayF2FRefundResult result = new AlipayF2FRefundResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) 
        {
            // 退货交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) 
        {
            // 退货发生异常，退货状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else 
        {
            // 其他情况表明该订单退货明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }
}

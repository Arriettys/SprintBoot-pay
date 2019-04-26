package com.alipay.trade.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.trade.hb.Constants;
import com.alipay.trade.hb.statusEnum.TradeStatus;
import com.alipay.trade.properties.AlipayInfo;
import com.alipay.trade.result.AlipayF2FPayResult;
import com.alipay.trade.service.IAlipayTradeService;
import com.alipay.trade.utils.Utils;

public abstract class AbsAlipayTradeService extends AbsAlipayService implements IAlipayTradeService
{
	protected static ExecutorService executorService = Executors.newCachedThreadPool();
    protected AlipayClient client;
	
	protected AlipayTradeQueryResponse tradeQuery(AlipayTradeQueryModel model) 
	{
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizModel(model);
        return (AlipayTradeQueryResponse) getResponse(client, request);
    }
	
	
	
	// 根据查询结果queryResponse判断交易是否支付成功，如果支付成功则更新result并返回，如果不成功则调用撤销
    protected AlipayF2FPayResult checkQueryAndCancel(String outTradeNo, String appAuthToken, AlipayF2FPayResult result,AlipayTradeQueryResponse queryResponse) 
    {
        if (querySuccess(queryResponse)) 
        {
            // 如果查询返回支付成功，则返回相应结果
            result.setTradeStatus(TradeStatus.SUCCESS);
            result.setResponse(toPayResponse(queryResponse));
            return result;
        }
		return result;
    }
    
    // 查询返回“支付成功”
    protected boolean querySuccess(AlipayTradeQueryResponse response) 
    {
        return response != null && Constants.SUCCESS.equals(response.getCode()) && ("TRADE_SUCCESS".equals(response.getTradeStatus()) || "TRADE_FINISHED".equals(response.getTradeStatus()));
    }
    
    // 轮询查询订单支付结果
    protected AlipayTradeQueryResponse loopQueryResult(AlipayTradeQueryModel queryModel, AlipayInfo alipayInfo) 
    {
        AlipayTradeQueryResponse queryResult = null;
        for (int i = 0; i < alipayInfo.getMaxQueryRetry(); i++) 
        {
            Utils.sleep(alipayInfo.getQueryDuration());
            AlipayTradeQueryResponse response = tradeQuery(queryModel);
            if (response != null) 
            {
                if (stopQuery(response)) 
                {
                    return response;
                }
                queryResult = response;
            }
        }
        return queryResult;
    }
    
    // 判断是否停止查询
    protected boolean stopQuery(AlipayTradeQueryResponse response) 
    {
        if (Constants.SUCCESS.equals(response.getCode())) {
            if ("TRADE_FINISHED".equals(response.getTradeStatus()) ||
                    "TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                    "TRADE_CLOSED".equals(response.getTradeStatus())) {
                // 如果查询到交易成功、交易结束、交易关闭，则返回对应结果
                return true;
            }
        }
        return false;
    }
    
    // 将查询应答转换为支付应答
    protected AlipayTradePayResponse toPayResponse(AlipayTradeQueryResponse response) 
    {
        AlipayTradePayResponse payResponse = new AlipayTradePayResponse();
        // 只有查询明确返回成功才能将返回码设置为10000，否则均为失败
        payResponse.setCode(querySuccess(response) ? Constants.SUCCESS : Constants.FAILED);
        // 补充交易状态信息
        StringBuilder msg = new StringBuilder(response.getMsg())
                .append(" tradeStatus:")
                .append(response.getTradeStatus());
        payResponse.setMsg(msg.toString());
        payResponse.setSubCode(response.getSubCode());
        payResponse.setSubMsg(response.getSubMsg());
        payResponse.setBody(response.getBody());
        payResponse.setParams(response.getParams());

        // payResponse应该是交易支付时间，但是response里是本次交易打款给卖家的时间,是否有问题
        // payResponse.setGmtPayment(response.getSendPayDate());
        payResponse.setBuyerLogonId(response.getBuyerLogonId());
        payResponse.setFundBillList(response.getFundBillList());
        payResponse.setOpenId(response.getOpenId());
        payResponse.setOutTradeNo(response.getOutTradeNo());
        payResponse.setReceiptAmount(response.getReceiptAmount());
        payResponse.setTotalAmount(response.getTotalAmount());
        payResponse.setTradeNo(response.getTradeNo());
        return payResponse;
    }
    
    // 交易异常，或发生系统错误
    protected boolean tradeError(AlipayResponse response) 
    {
        return response == null || Constants.ERROR.equals(response.getCode());
    }
}

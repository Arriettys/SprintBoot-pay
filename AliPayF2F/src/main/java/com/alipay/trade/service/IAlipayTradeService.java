package com.alipay.trade.service;

import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.trade.result.AlipayF2FPayResult;
import com.alipay.trade.result.AlipayF2FPrecreateResult;
import com.alipay.trade.result.AlipayF2FQueryResult;
import com.alipay.trade.result.AlipayF2FRefundResult;

public interface IAlipayTradeService
{
	// 当面付2.0流程支付
    public AlipayF2FPayResult tradePay(AlipayTradePayModel model);
    // 当面付2.0消费查询
    public AlipayF2FQueryResult queryTradeResult(AlipayTradeQueryModel model);
    // 当面付2.0预下单(生成二维码)
    public AlipayF2FPrecreateResult tradePrecreate(AlipayTradePrecreateModel model);
    // 当面付2.0消费退款
    public AlipayF2FRefundResult tradeRefund(AlipayTradeRefundModel builder);
}

package com.alipay.trade.hb;

import com.alipay.trade.hb.statusEnum.HbStatus;

public interface TradeInfo
{
	// 获取交易状态
    public HbStatus getStatus();

    // 获取交易时间
    public double getTimeConsume();
}

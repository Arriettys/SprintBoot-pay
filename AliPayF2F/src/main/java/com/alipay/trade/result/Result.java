package com.alipay.trade.result;

public interface Result
{
	// 判断交易是否在业务上成功, 返回true说明一定成功，但是返回false并不代表业务不成功！因为还有unknown的状态可能业务已经成功了
    public boolean isTradeSuccess();
}

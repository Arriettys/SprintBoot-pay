package com.alipay.trade.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;

public abstract class AbsAlipayService
{
	protected Log log = LogFactory.getLog(getClass());
	
	// 调用AlipayClient的execute方法，进行远程调用
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected AlipayResponse getResponse(AlipayClient client, AlipayRequest request) 
    {
        try 
        {
            AlipayResponse response = client.execute(request);
            if (response != null) 
            {
                log.info(response.getBody());
            }
            return response;

        } catch (AlipayApiException e) 
        {
            e.printStackTrace();
            return null;
        }
    }
}

package com.alipay.trade.hb;

import com.alipay.trade.hb.statusEnum.HbStatus;
import com.google.gson.annotations.SerializedName;

public class SysTradeInfo implements TradeInfo
{
	@SerializedName("OTN")
    private String outTradeNo;

    @SerializedName("TC")
    private double timeConsume;

    @SerializedName("STAT")
    private HbStatus status;
    
    private SysTradeInfo() 
    {
        // no public constructor.
    }

    public static SysTradeInfo newInstance(String outTradeNo, double timeConsume, HbStatus status) 
    {
        SysTradeInfo info = new SysTradeInfo();
        info.setOutTradeNo(outTradeNo);
        if (timeConsume > 99 || timeConsume < 0) 
        {
            timeConsume = 99;
        }
        info.setTimeConsume(timeConsume);
        info.setStatus(status);
        return info;
    }
    
	@Override
	public HbStatus getStatus()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTimeConsume()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getOutTradeNo()
	{
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo)
	{
		this.outTradeNo = outTradeNo;
	}

	public void setTimeConsume(double timeConsume)
	{
		this.timeConsume = timeConsume;
	}

	public void setStatus(HbStatus status)
	{
		this.status = status;
	}
	
}

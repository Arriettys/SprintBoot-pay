package com.alipay.trade.hb;

import com.alipay.trade.hb.statusEnum.HbStatus;

public class PosTradeInfo implements TradeInfo
{
	private HbStatus status;
    private String time;
    private int timeConsume;
    
    
	public PosTradeInfo()
	{
		super();
	}
	
	public static PosTradeInfo newInstance(HbStatus status, String time, int timeConsume) 
	{
        PosTradeInfo info = new PosTradeInfo();
        if (timeConsume > 99 || timeConsume < 0) 
        {
            timeConsume = 99;
        }
        info.setTimeConsume(timeConsume);
        info.setStatus(status);
        info.setTime(time);
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

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public void setStatus(HbStatus status)
	{
		this.status = status;
	}

	public void setTimeConsume(int timeConsume)
	{
		this.timeConsume = timeConsume;
	}
	

}

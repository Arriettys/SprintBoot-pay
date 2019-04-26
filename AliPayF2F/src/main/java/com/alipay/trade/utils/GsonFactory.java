package com.alipay.trade.utils;

import java.lang.reflect.Type;
import java.util.List;

import com.alipay.trade.hb.TradeInfo;
import com.alipay.trade.hb.TradeInfoAdapter;
import com.alipay.trade.hb.statusAdapter.EquipStatusAdapter;
import com.alipay.trade.hb.statusAdapter.ExceptionStatusAdapter;
import com.alipay.trade.hb.statusEnum.EquipStatus;
import com.alipay.trade.hb.statusEnum.ExceptionStatus;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 使用google gson作为json序列化反序列化工具
 */
public class GsonFactory
{
	 private static class GsonHolder 
	 {
		 	//使用TypeToken获知泛型参数的类型
	        private static Type exceptionListType = new TypeToken<List<ExceptionStatus>>(){}.getType();
	        private static Type tradeInfoListType = new TypeToken<List<TradeInfo>>(){}.getType();

	        private static Gson gson = new GsonBuilder()
	                                .registerTypeAdapter(exceptionListType, new ExceptionStatusAdapter())
	                                .registerTypeAdapter(tradeInfoListType, new TradeInfoAdapter())
	                                .registerTypeAdapter(EquipStatus.class, new EquipStatusAdapter())
	                                .create();
	    }

	    public static Gson getGson() 
	    {
	        return GsonHolder.gson;
	    }
}

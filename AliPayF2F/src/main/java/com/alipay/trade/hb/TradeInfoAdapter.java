package com.alipay.trade.hb;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alipay.trade.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TradeInfoAdapter implements JsonSerializer<List<TradeInfo>>
{
	@Override
    public JsonElement serialize(List<TradeInfo> tradeInfoList, Type type, JsonSerializationContext jsonSerializationContext) 
	{
        if (Utils.isListEmpty(tradeInfoList)) 
        {
            return null;
        }

        TradeInfo tradeInfo = tradeInfoList.get(0);
        if (tradeInfo instanceof PosTradeInfo) 
        {
            return new JsonPrimitive(StringUtils.join(tradeInfoList, ""));
        }

        return jsonSerializationContext.serialize(tradeInfoList);
    }
}

package com.alipay.trade.hb.statusAdapter;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alipay.trade.hb.statusEnum.ExceptionStatus;
import com.alipay.trade.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ExceptionStatusAdapter implements JsonSerializer<List<ExceptionStatus>> 
{
	@Override
    public JsonElement serialize(List<ExceptionStatus> exceptionInfos, Type type, JsonSerializationContext jsonSerializationContext) 
	{
        if (Utils.isListEmpty(exceptionInfos)) 
        {
            return null;
        }
        return new JsonPrimitive(StringUtils.join(exceptionInfos, "|"));
    }
}

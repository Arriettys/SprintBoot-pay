package com.alipay.trade.hb.statusAdapter;


import java.lang.reflect.Type;

import com.alipay.trade.hb.statusEnum.EquipStatus;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EquipStatusAdapter implements JsonSerializer<EquipStatus>
{
	@Override
    public JsonElement serialize(EquipStatus equipStatus, Type type, JsonSerializationContext jsonSerializationContext) 
	{
        return new JsonPrimitive(equipStatus.getValue());
    }
}

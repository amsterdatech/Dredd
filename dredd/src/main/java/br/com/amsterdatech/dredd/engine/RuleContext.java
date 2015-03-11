package br.com.amsterdatech.dredd.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william on 28/01/15.
 */
public class RuleContext
{

    private volatile Map<String, Object> mapContext = new HashMap<String, Object>();

    public synchronized RuleContext put(String k, Object v)
    {
        mapContext.put(k, v);
        return this;
    }

    public Object get(String key)
    {
        return mapContext.get(key);
    }

}

package br.com.amsterdatech.dredd.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 22/01/15.
 */
public abstract class Rule
{
    protected boolean hasCalledOnRulePostExecute;

    protected RuleContext ruleContext;

    private List<Rule> children;

    public static Rule[] getRules(List<Rule> rules)
    {
        return (rules != null ? rules.toArray(new Rule[rules.size()]) : new Rule[0]);
    }

    public Rule[] getChildren()
    {
        if (children == null)
        {
            return null;
        }

        return children.toArray(new Rule[children.size()]);
    }

    public Rule addChild(Rule rule)
    {
        if (children == null)
        {
            children = new ArrayList<Rule>();
        }
        children.add(rule);
        return this;
    }

    public void setRuleContext(RuleContext ruleContext)
    {
        this.ruleContext = ruleContext;
    }

    public abstract boolean eval();

    public void onRulePreExecute()
    {

    }

    public abstract void execute();

    public void onRulePostExecute()
    {

    }

    public abstract void runChildren();

    public abstract boolean fire();

}

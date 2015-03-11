package br.com.amsterdatech.dredd.engine;

/**
 * Created by williamgouvea on 2/20/15.
 */
public class ChainRuleRunner implements RuleRunner
{
    @Override
    public void run(RuleContext ruleContext, Rule... rules)
    {
        if (rules != null && rules.length > 0)
        {
            Rule rule = rules[0];
            if (rule != null)
            {
                rule.setRuleContext(ruleContext);
                rule.fire();
            }
        }
    }
}

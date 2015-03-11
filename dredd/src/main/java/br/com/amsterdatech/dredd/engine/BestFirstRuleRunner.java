package br.com.amsterdatech.dredd.engine;

/**
 * Created by williamgouvea on 2/20/15.
 */
public class BestFirstRuleRunner implements RuleRunner
{
    @Override
    public void run(RuleContext ruleContext, Rule... rules)
    {
        if (rules != null)
        {
            for (Rule rule : rules)
            {
                if (rule != null)
                {
                    rule.setRuleContext(ruleContext);
                    if (!rule.fire())
                    {
                        break;
                    }
                }
            }
        }
    }
}

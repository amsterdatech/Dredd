package br.com.amsterdatech.dredd.engine;

/**
 * Created by williamgouvea on 2/20/15.
 */
public interface RuleRunner
{
    public void run(RuleContext ruleContext, Rule... rules);
}

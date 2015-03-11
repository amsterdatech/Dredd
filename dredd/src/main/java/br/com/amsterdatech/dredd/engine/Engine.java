package br.com.amsterdatech.dredd.engine;

/**
 * Created by williamgouvea on 2/20/15.
 */
public class Engine
{
    public final static String CHAIN = "CHAIN";
    public final static String BEST_FIRST = "BEST_FIRST";

    private static RuleRunner chainRunner;
    private static RuleRunner bestFirstRunner;

    public static RuleRunner bestFirstRunner()
    {
        return getRunner(Engine.BEST_FIRST);
    }

    public static RuleRunner chainRunner()
    {
        return getRunner(Engine.CHAIN);
    }

    public static RuleRunner getRunner(String type)
    {
        RuleRunner runner = new RuleRunner()
        {
            @Override
            public void run(RuleContext ruleContext, Rule... rules)
            {
                throw new IllegalStateException("Not implemented yet");
            }
        };

        if (type.equalsIgnoreCase(BEST_FIRST))
        {
            runner = new BestFirstRuleRunner();
        }
        if (type.equalsIgnoreCase(CHAIN))
        {
            runner = new ChainRuleRunner();
        }
        return runner;
    }
}

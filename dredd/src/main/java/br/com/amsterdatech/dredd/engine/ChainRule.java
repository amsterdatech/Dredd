package br.com.amsterdatech.dredd.engine;

/**
 * Created by alexandre on 05/02/15.
 */
public abstract class ChainRule extends Rule
{
    @Override
    public boolean fire()
    {
        if (eval())
        {
            onRulePreExecute();
            execute();
            onRulePostExecute();
            runChildren();
        }
        return true;
    }

    @Override
    public void runChildren()
    {
        if (getChildren() != null)
        {
            Engine.chainRunner()
                  .run(ruleContext, getChildren());
        }
    }

    @Override
    public Rule addChild(Rule rule)
    {
        if (rule instanceof ChainRule && getChildren() != null && getChildren().length == 1)
        {
            throw new IllegalStateException("You can't add more than one child!");
        }
        return super.addChild(rule);
    }
}

package br.com.amsterdatech.dredd.engine;

/**
 * Created by alexandre on 05/02/15.
 */
public abstract class BestFirstRule extends Rule
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
            return false;
        }
        return true;
    }

    @Override
    public void runChildren()
    {
        if (getChildren() != null)
        {
            Engine.bestFirstRunner()
                  .run(ruleContext, getChildren());
        }
    }
}
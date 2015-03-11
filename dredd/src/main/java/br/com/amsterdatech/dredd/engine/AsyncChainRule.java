package br.com.amsterdatech.dredd.engine;

/**
 * Created by williamgouvea on 2/24/15.
 */
public abstract class AsyncChainRule extends ChainRule implements OnFinishInteraction
{
    protected boolean hasUserInteracted;

    @Override
    public boolean fire()
    {
        if (eval())
        {
            onRulePreExecute();
            execute();
            onRulePostExecute();
            verifyUserInteraction();
        } else
        {
            runChildren();
        }
        return true;
    }

    private void verifyUserInteraction()
    {
        if (!hasUserInteracted)
        {
            throw new IllegalStateException("You should get onUserInteraction callback in order to call dispatchNextChainRule method when the user interaction has finished on UI and ChainRunner be able to run his own child.");
        }
    }

    public OnFinishInteraction getOnUserInteraction()
    {
        hasUserInteracted = true;
        return this;
    }

    @Override
    public void dispatchNextChainRule()
    {
        runChildren();
    }
}

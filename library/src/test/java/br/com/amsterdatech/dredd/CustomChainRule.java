package br.com.amsterdatech.dredd;


import br.com.amsterdatech.dredd.engine.ChainRule;

/**
 * Created by william on 12/02/15.
 */
public class CustomChainRule extends ChainRule
{
    @Override
    public boolean eval()
    {
        return false;
    }

    @Override
    public void execute()
    {

    }
}

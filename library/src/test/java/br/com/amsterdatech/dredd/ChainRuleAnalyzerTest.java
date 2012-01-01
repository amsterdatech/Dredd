package br.com.amsterdatech.dredd;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import br.com.amsterdatech.dredd.engine.AsyncChainRule;
import br.com.amsterdatech.dredd.engine.BestFirstRule;
import br.com.amsterdatech.dredd.engine.ChainRule;
import br.com.amsterdatech.dredd.engine.Engine;
import br.com.amsterdatech.dredd.engine.Rule;
import br.com.amsterdatech.dredd.engine.RuleContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by williamgouvea on 2/20/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ChainRuleAnalyzerTest
{
    @Mock
    RuleContext engineContext;
    private List<Rule> testRules;

    @Before
    public void setup()
    {
        initMocks(this);
        testRules = new ArrayList<Rule>();
    }

    @Test
    public void shouldRunWithOnlyOneChainRuleAndEval()
    {
        ChainRule mockRule1 = mock(ChainRule.class);

        when(mockRule1.eval()).thenReturn(true);
        when(mockRule1.fire()).thenCallRealMethod();

        Engine.chainRunner()
              .run(engineContext, mockRule1);

        InOrder inOrder = inOrder(mockRule1);

        inOrder.verify(mockRule1).setRuleContext(engineContext);
        inOrder.verify(mockRule1).fire();
        inOrder.verify(mockRule1).eval();
        assertTrue(mockRule1.eval());
        inOrder.verify(mockRule1).onRulePreExecute();
        inOrder.verify(mockRule1).execute();
        inOrder.verify(mockRule1).onRulePostExecute();

    }

    @Test
    public void shouldRunWithOnlyOneChainRuleAndNotEval()
    {
        ChainRule mockRule1 = mock(ChainRule.class);

        when(mockRule1.eval()).thenReturn(false);
        when(mockRule1.fire()).thenCallRealMethod();

        testRules.add(mockRule1);

        Engine.chainRunner()
              .run(engineContext, mockRule1);

        InOrder inOrder = inOrder(mockRule1);

        inOrder.verify(mockRule1).setRuleContext(engineContext);
        inOrder.verify(mockRule1).fire();
        inOrder.verify(mockRule1).eval();
        assertFalse(mockRule1.eval());

        inOrder.verify(mockRule1, never()).onRulePreExecute();
        inOrder.verify(mockRule1, never()).execute();
        inOrder.verify(mockRule1, never()).onRulePostExecute();

        //inOrder.verify(mockRule1).runChildren();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldOnlyAllowOneChildOfChainRuleTypeInsideChainRule()
    {
        ChainRule childRule1 = spy(new CustomChainRule());
        ChainRule childRule2 = mock(ChainRule.class);
        ChainRule childRule3 = mock(ChainRule.class);

        childRule1.addChild(childRule2).addChild(childRule3);
    }

    @Test
    public void shouldHaveMoreThanOneChildIfTheyWereBestFirstInsideChainRule()
    {
        ChainRule childRule1 = spy(new CustomChainRule());
        ChainRule childRule2 = mock(ChainRule.class);
        BestFirstRule childRule3 = mock(BestFirstRule.class);

        childRule1.addChild(childRule2).addChild(childRule3);
    }

    @Test
    public void shouldChainRuleFirstHaveOneChildrenAndChainSecondNone()
    {
        ChainRule childRule1 = spy(new CustomChainRule());
        ChainRule childRule2 = mock(ChainRule.class);

        childRule1.addChild(childRule2);

        assertNotNull(childRule1.getChildren());
        assertEquals(childRule1.getChildren().length, 1);
        assertEquals(childRule2, childRule1.getChildren()[0]);
        assertNull(childRule2.getChildren());

    }

    @Test
    public void shouldRunWithTwoChainRules()
    {
        ChainRule childRule1 = spy(new CustomChainRule());
        ChainRule childRule2 = spy(new CustomChainRule());

        when(childRule1.eval()).thenReturn(true);
        when(childRule2.eval()).thenReturn(true);

        childRule1.addChild(childRule2);

        Engine.chainRunner()
              .run(engineContext, childRule1);

        verify(childRule1).setRuleContext(engineContext);
        verify(childRule1).fire();
        verify(childRule1).eval();
        assertTrue(childRule1.eval());

        verify(childRule2).fire();
        verify(childRule2).eval();
        assertTrue(childRule2.eval());

    }

    @Test
    public void shouldRunWithTwoChainRulesAndJustFirstOneEvalAndExecute()
    {
        ChainRule childRule1 = spy(new CustomChainRule());
        ChainRule childRule2 = spy(new CustomChainRule());

        when(childRule1.eval()).thenReturn(true);
        when(childRule1.fire()).thenCallRealMethod();

        when(childRule2.eval()).thenReturn(false);
        when(childRule2.fire()).thenCallRealMethod();

        childRule1.addChild(childRule2);

        Engine.chainRunner()
              .run(engineContext, childRule1);

        InOrder inOrder = inOrder(childRule1, childRule2);

        inOrder.verify(childRule1).setRuleContext(engineContext);
        inOrder.verify(childRule1).fire();
        inOrder.verify(childRule1).eval();
        assertTrue(childRule1.eval());
        assertTrue(childRule1.fire());

        inOrder.verify(childRule1).onRulePreExecute();
        inOrder.verify(childRule1).execute();

        inOrder.verify(childRule2).setRuleContext(engineContext);
        inOrder.verify(childRule2).fire();
        inOrder.verify(childRule2).eval();
        assertFalse(childRule2.eval());

        inOrder.verify(childRule2, never()).onRulePreExecute();
        inOrder.verify(childRule2, never()).execute();
        inOrder.verify(childRule2, never()).onRulePostExecute();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldCrashIfOnFinishInteractionHasNotBeenUsed()
    {
        AsyncChainRule asyncChainRule = mock(AsyncChainRule.class);

        when(asyncChainRule.fire()).thenCallRealMethod();
        when(asyncChainRule.eval()).thenReturn(true);

        Engine.chainRunner()
              .run(engineContext,
                      asyncChainRule);

    }

    @Test
    public void shouldNotCallRunChildrenWhileWaitingForExternalInteraction()
    {
        AsyncChainRule asyncChainRule = mock(AsyncChainRule.class);

        doCallRealMethod().when(asyncChainRule).getOnUserInteraction();
        when(asyncChainRule.fire()).thenCallRealMethod();
        when(asyncChainRule.eval()).thenReturn(true);

        asyncChainRule.getOnUserInteraction();

        Engine.chainRunner()
              .run(engineContext,
                      asyncChainRule);

        verify(asyncChainRule, never()).runChildren();
    }

    @Test
    public void shouldCallRunChildrenIfNotEvalEvenWhenWaitForExternalInteraction()
    {
        AsyncChainRule asyncChainRule = mock(AsyncChainRule.class);

        doCallRealMethod().when(asyncChainRule).getOnUserInteraction();
        when(asyncChainRule.fire()).thenCallRealMethod();
        when(asyncChainRule.eval()).thenReturn(false);

        asyncChainRule.getOnUserInteraction();

        Engine.chainRunner()
              .run(engineContext,
                      asyncChainRule);

        verify(asyncChainRule).runChildren();
    }
}

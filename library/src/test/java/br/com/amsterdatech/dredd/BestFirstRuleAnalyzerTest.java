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

import br.com.amsterdatech.dredd.engine.BestFirstRule;
import br.com.amsterdatech.dredd.engine.Engine;
import br.com.amsterdatech.dredd.engine.Rule;
import br.com.amsterdatech.dredd.engine.RuleContext;

import static br.com.amsterdatech.dredd.engine.Rule.getRules;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by william on 28/01/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class BestFirstRuleAnalyzerTest
{
    @Mock
    RuleContext ruleContex;
    private List<Rule> testRules;

    @Before
    public void setup()
    {
        initMocks(this);
        testRules = new ArrayList<Rule>();
    }

    @Test
    public void shouldEngineRunWithOnlyOneRuleEvalAndExecute()
    {
        BestFirstRule mockRule1 = mock(BestFirstRule.class);

        when(mockRule1.eval()).thenReturn(true);
        when(mockRule1.fire()).thenCallRealMethod();

        testRules.add(mockRule1);

        Engine.bestFirstRunner()
              .run(ruleContex, getRules(testRules));

        InOrder inOrder = inOrder(mockRule1);

        inOrder.verify(mockRule1).setRuleContext(ruleContex);
        inOrder.verify(mockRule1).fire();
        inOrder.verify(mockRule1).eval();
        assertTrue(mockRule1.eval());
        inOrder.verify(mockRule1).onRulePreExecute();
        inOrder.verify(mockRule1).execute();
        inOrder.verify(mockRule1).onRulePostExecute();
    }

    @Test
    public void shouldRunWithOneRuleNotEvalAndThusNotExecute()
    {
        BestFirstRule mockRule1 = mock(BestFirstRule.class);
        when(mockRule1.eval()).thenReturn(false);
        when(mockRule1.fire()).thenCallRealMethod();

        testRules.add(mockRule1);

        Engine.bestFirstRunner()
              .run(ruleContex, getRules(testRules));

        InOrder inOrder = inOrder(mockRule1);

        inOrder.verify(mockRule1).setRuleContext(ruleContex);
        inOrder.verify(mockRule1).fire();
        inOrder.verify(mockRule1).eval();
        assertFalse(mockRule1.eval());
        verify(mockRule1, never()).onRulePreExecute();
        verify(mockRule1, never()).execute();
        verify(mockRule1, never()).onRulePostExecute();
    }

    @Test
    public void shouldRunWithTwoChildrenEvalAndExecuteOnlyMostLeftChild()
    {
        CustomBestFirstRule parentRule = spy(new CustomBestFirstRule());
        BestFirstRule childRule1 = mock(BestFirstRule.class);
        BestFirstRule childRule2 = mock(BestFirstRule.class);

        when(parentRule.eval()).thenReturn(true);
        when(childRule1.eval()).thenReturn(true);
        when(childRule1.fire()).thenCallRealMethod();
        when(childRule2.eval()).thenReturn(true);
        when(childRule2.fire()).thenCallRealMethod();

        testRules.add(parentRule
                .addChild(childRule1)
                .addChild(childRule2));

        Engine.bestFirstRunner()
              .run(ruleContex, getRules(testRules));

        InOrder inOrder = inOrder(parentRule, childRule1);

        inOrder.verify(parentRule).setRuleContext(ruleContex);
        inOrder.verify(parentRule).fire();
        inOrder.verify(parentRule).eval();
        assertTrue(parentRule.eval());
        inOrder.verify(parentRule).onRulePreExecute();
        inOrder.verify(parentRule).execute();
        inOrder.verify(parentRule).onRulePostExecute();

        inOrder.verify(childRule1).setRuleContext(ruleContex);
        inOrder.verify(childRule1).fire();
        inOrder.verify(childRule1).eval();
        assertTrue(childRule1.eval());
        inOrder.verify(childRule1).onRulePreExecute();
        inOrder.verify(childRule1).execute();
        inOrder.verify(childRule1).onRulePostExecute();

        verify(childRule2, never()).setRuleContext(ruleContex);
        verify(childRule2, never()).fire();
        verify(childRule2, never()).eval();
        verify(childRule2, never()).onRulePreExecute();
        verify(childRule2, never()).execute();
        verify(childRule2, never()).onRulePostExecute();
    }

    @Test
    public void shouldRunWithTwoChildrenEvalAndExecuteOnlyMostRightChild()
    {
        CustomBestFirstRule parentRule = spy(new CustomBestFirstRule());
        BestFirstRule childRule1 = mock(BestFirstRule.class);
        BestFirstRule childRule2 = mock(BestFirstRule.class);

        when(parentRule.eval()).thenReturn(true);
        when(childRule1.eval()).thenReturn(false);
        when(childRule1.fire()).thenCallRealMethod();
        when(childRule2.eval()).thenReturn(true);
        when(childRule2.fire()).thenCallRealMethod();

        testRules.add(parentRule
                .addChild(childRule1)
                .addChild(childRule2));

        Engine.bestFirstRunner()
              .run(ruleContex, getRules(testRules));

        InOrder inOrder = inOrder(parentRule, childRule1, childRule2);

        inOrder.verify(parentRule).setRuleContext(ruleContex);
        inOrder.verify(parentRule).fire();
        inOrder.verify(parentRule).eval();
        assertTrue(parentRule.eval());
        verify(parentRule).onRulePreExecute();
        verify(parentRule).execute();
        verify(parentRule).onRulePostExecute();

        inOrder.verify(childRule1).setRuleContext(ruleContex);
        inOrder.verify(childRule1).fire();
        inOrder.verify(childRule1).eval();
        assertFalse(childRule1.eval());
        verify(childRule1, never()).onRulePreExecute();
        verify(childRule1, never()).execute();
        verify(childRule1, never()).onRulePostExecute();

        inOrder.verify(childRule2).setRuleContext(ruleContex);
        inOrder.verify(childRule2).fire();
        inOrder.verify(childRule2).eval();
        assertTrue(childRule2.eval());
        inOrder.verify(childRule2).onRulePreExecute();
        inOrder.verify(childRule2).execute();
        inOrder.verify(childRule2).onRulePostExecute();

    }

    @Test
    public void shouldRunWithThreeChildrenEvalAndExecuteOnlyThirdChild()
    {
        CustomBestFirstRule parentRule = spy(new CustomBestFirstRule());
        BestFirstRule childRule1 = mock(BestFirstRule.class);
        BestFirstRule childRule2 = mock(BestFirstRule.class);
        BestFirstRule childRule3 = mock(BestFirstRule.class);

        when(parentRule.eval()).thenReturn(true);

        when(childRule1.eval()).thenReturn(false);
        when(childRule1.fire()).thenCallRealMethod();

        when(childRule2.eval()).thenReturn(false);
        when(childRule2.fire()).thenCallRealMethod();

        when(childRule3.eval()).thenReturn(true);
        when(childRule3.fire()).thenCallRealMethod();

        testRules.add(parentRule
                .addChild(childRule1)
                .addChild(childRule2)
                .addChild(childRule3));


        Engine.bestFirstRunner()
              .run(ruleContex, getRules(testRules));

        InOrder inOrder = inOrder(parentRule, childRule1, childRule2, childRule3);

        inOrder.verify(parentRule).setRuleContext(ruleContex);
        inOrder.verify(parentRule).fire();
        inOrder.verify(parentRule).eval();
        assertTrue(parentRule.eval());
        inOrder.verify(parentRule).onRulePreExecute();
        inOrder.verify(parentRule).execute();
        inOrder.verify(parentRule).onRulePostExecute();

        inOrder.verify(childRule1).setRuleContext(ruleContex);
        inOrder.verify(childRule1).fire();
        inOrder.verify(childRule1).eval();
        assertFalse(childRule1.eval());
        verify(childRule1, never()).onRulePreExecute();
        verify(childRule1, never()).execute();
        verify(childRule1, never()).onRulePostExecute();

        inOrder.verify(childRule2).setRuleContext(ruleContex);
        inOrder.verify(childRule2).fire();
        inOrder.verify(childRule2).eval();
        assertFalse(childRule2.eval());
        verify(childRule2, never()).onRulePreExecute();
        verify(childRule2, never()).execute();
        verify(childRule2, never()).onRulePostExecute();

        inOrder.verify(childRule3).setRuleContext(ruleContex);
        inOrder.verify(childRule3).fire();
        inOrder.verify(childRule3).eval();
        assertTrue(childRule3.eval());
        inOrder.verify(childRule3).onRulePreExecute();
        inOrder.verify(childRule3).execute();
        inOrder.verify(childRule3).onRulePostExecute();
    }

    @Test
    public void shouldRunWithThreeChildrenEvalAndExecuteOnlySecondChild()
    {
        CustomBestFirstRule parentRule = spy(new CustomBestFirstRule());
        BestFirstRule childRule1 = mock(BestFirstRule.class);
        BestFirstRule childRule2 = mock(BestFirstRule.class);
        BestFirstRule childRule3 = mock(BestFirstRule.class);

        when(parentRule.eval()).thenReturn(true);

        when(childRule1.eval()).thenReturn(false);
        when(childRule1.fire()).thenCallRealMethod();

        when(childRule2.eval()).thenReturn(true);
        when(childRule2.fire()).thenCallRealMethod();

        when(childRule3.eval()).thenReturn(true);
        when(childRule3.fire()).thenCallRealMethod();

        testRules.add(parentRule
                .addChild(childRule1)
                .addChild(childRule2)
                .addChild(childRule3));

        Engine.bestFirstRunner()
              .run(ruleContex, getRules(testRules));

        InOrder inOrder = inOrder(parentRule, childRule1, childRule2);

        inOrder.verify(parentRule).setRuleContext(ruleContex);
        inOrder.verify(parentRule).fire();
        inOrder.verify(parentRule).eval();
        assertTrue(parentRule.eval());
        inOrder.verify(parentRule).onRulePreExecute();
        inOrder.verify(parentRule).execute();
        inOrder.verify(parentRule).onRulePostExecute();

        inOrder.verify(childRule1).setRuleContext(ruleContex);
        inOrder.verify(childRule1).fire();
        inOrder.verify(childRule1).eval();
        assertFalse(childRule1.eval());
        verify(childRule1, never()).onRulePreExecute();
        verify(childRule1, never()).execute();
        verify(childRule1, never()).onRulePostExecute();

        inOrder.verify(childRule2).setRuleContext(ruleContex);
        inOrder.verify(childRule2).fire();
        inOrder.verify(childRule2).eval();
        assertTrue(childRule2.eval());
        inOrder.verify(childRule2).onRulePreExecute();
        inOrder.verify(childRule2).execute();
        inOrder.verify(childRule2).onRulePostExecute();

        verify(childRule3, never()).setRuleContext(ruleContex);
        verify(childRule3, never()).fire();
        verify(childRule3, never()).eval();
        verify(childRule3, never()).onRulePreExecute();
        verify(childRule3, never()).execute();
        verify(childRule3, never()).onRulePostExecute();
    }
}



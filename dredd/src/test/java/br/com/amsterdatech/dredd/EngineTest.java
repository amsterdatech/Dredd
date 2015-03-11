package br.com.amsterdatech.dredd;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import br.com.amsterdatech.dredd.engine.BestFirstRule;
import br.com.amsterdatech.dredd.engine.BestFirstRuleRunner;
import br.com.amsterdatech.dredd.engine.ChainRuleRunner;
import br.com.amsterdatech.dredd.engine.Engine;
import br.com.amsterdatech.dredd.engine.Rule;
import br.com.amsterdatech.dredd.engine.RuleContext;
import br.com.amsterdatech.dredd.engine.RuleRunner;

import static br.com.amsterdatech.dredd.engine.Rule.getRules;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by williamgouvea on 2/20/15.
 */

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class EngineTest
{
    @Mock
    RuleContext ruleContext;
    private List<Rule> testRules;

    @Before
    public void setup()
    {
        initMocks(this);
        testRules = new ArrayList<Rule>();
    }

    @Test
    public void shouldGetBestFirstRunner()
    {
        RuleRunner runner = Engine.getRunner(Engine.BEST_FIRST);
        assertThat(runner, is(instanceOf(BestFirstRuleRunner.class)));
    }

    @Test
    public void shouldGetChainRunner()
    {
        RuleRunner runner = Engine.getRunner(Engine.CHAIN);
        assertThat(runner, is(instanceOf(ChainRuleRunner.class)));
    }

    @Test
    public void shouldCallAnalyzerOnRuleRunnerWhenEngineStarts()
    {
        BestFirstRule bestFirstRule1 = mock(BestFirstRule.class);
        BestFirstRule bestFirstRule2 = mock(BestFirstRule.class);

        when(bestFirstRule1.fire()).thenCallRealMethod();
        when(bestFirstRule2.fire()).thenCallRealMethod();

        when(bestFirstRule1.fire()).thenReturn(true);
        when(bestFirstRule2.fire()).thenReturn(true);

        testRules.add(bestFirstRule1
                .addChild(bestFirstRule2));

        Engine.bestFirstRunner()
              .run(ruleContext, getRules(testRules));

        verify(bestFirstRule1).fire();
        verify(bestFirstRule2).fire();
    }



    @Test(expected = IllegalStateException.class)
    public void shouldCrashIfNotHasValidRunner()
    {
        BestFirstRule bestFirstRule1 = mock(BestFirstRule.class);
        BestFirstRule bestFirstRule2 = mock(BestFirstRule.class);

        testRules.add(bestFirstRule1
                .addChild(bestFirstRule2));

        Engine.getRunner("DUMMY")
              .run(ruleContext, getRules(testRules));
    }
}

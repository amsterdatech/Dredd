package br.com.amsterdatech.dredd.engine;

import android.os.AsyncTask;

/**
 * Created by alexandre on 06/02/15.
 */
public abstract class AsyncBestFirstRule<Result> extends BestFirstRule
{
    public abstract Result onAsyncRuleExecute();

    public abstract void onAsyncRulePostExecute(Result result);

    @Override
    public final void execute()
    {
    }

    @Override
    public boolean fire()
    {
        if (eval())
        {
            new BreadthTask(this).execute();
            return false;
        }
        return true;
    }

    private class BreadthTask extends AsyncTask<Void, Void, Result>
    {
        private AsyncBestFirstRule<Result> rule;

        private BreadthTask(AsyncBestFirstRule<Result> rule)
        {
            this.rule = rule;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            rule.onRulePreExecute();
        }

        @Override
        protected Result doInBackground(Void... params)
        {
            return rule.onAsyncRuleExecute();
        }

        @Override
        protected void onPostExecute(Result result)
        {
            super.onPostExecute(result);
            rule.onAsyncRulePostExecute(result);
            rule.onRulePostExecute();
            rule.runChildren();
        }
    }
}



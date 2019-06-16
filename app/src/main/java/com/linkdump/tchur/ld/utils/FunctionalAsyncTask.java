package com.linkdump.tchur.ld.utils;

import android.os.AsyncTask;



import java.util.function.Function;

public class FunctionalAsyncTask<T,R,U> extends AsyncTask {

    private T inputObject;
    private U outputObject;

    private Function<T,U> onExecuteInBackGroundFunction;
    private Runnable onUpdatingFunction;
    private Function<T,U> onDoneExecutingFunction;

    public FunctionalAsyncTask() {
        super();
    }


    public FunctionalAsyncTask SetOnBackGroundFunc(Function<T, U> func){
        onExecuteInBackGroundFunction = func;
        return this;
    }

    public FunctionalAsyncTask SetOnUpdatingFunction(Runnable runnable){
        onUpdatingFunction = runnable;
        return this;
    }

    public FunctionalAsyncTask SetOnDoneExecutingFunc(Function<T, U> func){
        onDoneExecutingFunction = func;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        onDoneExecutingFunction.apply(inputObject);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        onUpdatingFunction.run();
    }

    @Override
    protected void onCancelled(Object o) {
        super.onCancelled(o);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected U doInBackground(Object[] objects) {
        return onExecuteInBackGroundFunction.apply(inputObject);
    }
}

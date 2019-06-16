package com.linkdump.tchur.ld.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.linkdump.tchur.ld.abstractions.IViewProvider;

import java.lang.reflect.Type;
import java.util.List;

public abstract class ViewCoordinator implements IViewProvider {


    List<View> viewRepo;
    View rootView;
    Context context;
    AppCompatActivity appCompatActivity;


    public ViewCoordinator(Context context, AppCompatActivity appCompatActivity){
         this.context = context;
         this.appCompatActivity = appCompatActivity;
    }



    public ViewCoordinator initialiseViewFromXml(int xmlId)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(xmlId, null);
        PostViewInit(contentView);
        return this;
    }

    public abstract void PostViewInit(View view);

    public List<View> getViewRepo() {
        return viewRepo;
    }

    public void setViewRepo(List<View> viewRepo) {
        this.viewRepo = viewRepo;
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }



    @Override
    public View getViewByUiName(String moniker)
    {


        return null;
    }



    @Override
    public View getViewByType(Type type)
    {


        return null;
    }
}

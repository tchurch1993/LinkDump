package com.linkdump.tchur.ld.ui.ui_containers;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;






public abstract class ViewCoordinator {


    ArrayList<View> viewRepo;
    View rootView;
    Context context;
    AppCompatActivity appCompatActivity;


    public ViewCoordinator(Context context, AppCompatActivity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }


    public ViewCoordinator initialiseViewFromXml(int xmlId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(xmlId, null);
        PostViewInit(rootView);
        WireEvents(rootView,appCompatActivity);
        return this;
    }



    /*

      Abstracts , implemented in the constuctor

     */
    public abstract void PostViewInit(View view);
    public abstract void WireEvents(View view, AppCompatActivity appCompatActivity);



    /*

        getters and setters

     */
    public ArrayList<View> getViewRepo() {
        return viewRepo;
    }

    public void setViewRepo(ArrayList<View> viewRepo) {
        this.viewRepo = viewRepo;
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }


}

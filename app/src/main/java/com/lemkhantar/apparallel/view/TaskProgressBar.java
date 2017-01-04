package com.lemkhantar.apparallel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lemkhantar.apparallel.R;

public class TaskProgressBar extends LinearLayout {

    public TextView getDuree() {
        return duree;
    }

    public void setDuree(TextView duree) {
        this.duree = duree;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public TextView getTitre() {
        return titre;
    }

    public void setTitre(TextView titre) {
        this.titre = titre;
    }

    View rootView;
    TextView titre;
    TextView duree;
    ProgressBar progressBar;

    public TaskProgressBar(Context context) {
        super(context);
        init(context);
    }

    public TaskProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.customprogress, this);
        titre = (TextView) rootView.findViewById(R.id.titre);
        duree = (TextView) rootView.findViewById(R.id.duree);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


    }
}
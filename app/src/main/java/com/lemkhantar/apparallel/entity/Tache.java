package com.lemkhantar.apparallel.entity;

/**
 * Created by lemkhantar1 on 8/10/16.
 */
public class Tache {

    private int _id;
    private String _titre;
    private int _duree;
    private int  _tempsecoule;

    public Tache(int _id, String _titre, int _duree, int _tempsecoule) {
        this._id = _id;
        this._titre = _titre;
        this._duree = _duree;
        this._tempsecoule = _tempsecoule;
    }

    public Tache() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_titre() {
        return _titre;
    }

    public void set_titre(String _titre) {
        this._titre = _titre;
    }

    public int get_duree() {
        return _duree;
    }

    public void set_duree(int _duree) {
        this._duree = _duree;
    }

    public int get_tempsecoule() {
        return _tempsecoule;
    }

    public void set_tempsecoule(int _tempsecoule) {
        this._tempsecoule = _tempsecoule;
    }
}

package com.openlearning.ilearn.interfaces;

public interface ActivityHooks {

    void callHooks();

    void handleIntent();

    void init();

    void process();

    void loaded();


}

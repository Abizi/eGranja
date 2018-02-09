package com.turkeytech.egranja.session;

class StateManager {

    //TODO: Create private fields

    private static final StateManager ourInstance = new StateManager();

    static StateManager getInstance() {
        return ourInstance;
    }

    private StateManager() {
    }

    //TODO: Create getter and setter methods for the private fields

}

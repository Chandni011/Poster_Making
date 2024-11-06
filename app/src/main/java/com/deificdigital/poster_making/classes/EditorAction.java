package com.deificdigital.poster_making.classes;

import android.view.View;

public class EditorAction {
    public enum ActionType { ADD, DELETE, MODIFY }
    public ActionType type;
    public View view; // the view affected by the action
    public Object oldState; // state before the action (for undo)
    public Object newState; // state after the action (for redo)

    public EditorAction(ActionType type, View view, Object oldState, Object newState) {
        this.type = type;
        this.view = view;
        this.oldState = oldState;
        this.newState = newState;
    }
}
package com.github.topnav_rosjava_kasptom.components.remote_controller.presenter;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;

public interface IRemoteControlPresenter extends IBasePresenter {
    void toggleEnabled();

    void setOnControlCheckboxChangedListener(OnControlCheckboxChangedListener listener);

    interface OnControlCheckboxChangedListener {
        void onCheckboxChanged(boolean isChecked);
    }
}

package com.zl.network;

import android.app.Dialog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by PF-07GLA9 on 2017/4/10.
 */

public class ListHttpCallbackWithDialog<T extends List<?>> implements ListHttpCallback<T> {

    private WeakReference<Dialog> mDialogRef;

    public ListHttpCallbackWithDialog(Dialog dialog) {
        mDialogRef = new WeakReference<>(dialog);
    }

    @Override
    public void onSuccess(T o) {
        handleDialog();
    }

    @Override
    public void onFailure(HttpException e) {
        handleDialog();
    }

    private void handleDialog() {
        Dialog mDialog = mDialogRef.get();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}

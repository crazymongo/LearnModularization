package com.zl.network;

import android.app.Dialog;

import java.lang.ref.WeakReference;

/**
 * Created by PF-07GLA9 on 2017/4/10.
 */

public class HttpCallbackWithDialog<T> implements HttpCallback<T> {

    private WeakReference<Dialog> mDialogRef;

    public HttpCallbackWithDialog(Dialog dialog) {
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

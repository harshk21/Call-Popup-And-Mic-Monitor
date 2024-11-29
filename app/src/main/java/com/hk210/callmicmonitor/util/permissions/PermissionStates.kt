package com.hk210.callmicmonitor.util.permissions

import androidx.annotation.IntDef

class PermissionStates {

    companion object {
        const val PERMISSION_GRANTED = 0
        const val PERMISSION_DENIED = 1
        const val PERMISSION_RATIONALE = 2
        const val PERMISSION_NEVER_ASK_AGAIN = 3
    }

    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE, AnnotationTarget.EXPRESSION)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(PERMISSION_GRANTED, PERMISSION_DENIED, PERMISSION_RATIONALE, PERMISSION_NEVER_ASK_AGAIN)
    annotation class PermissionState
}
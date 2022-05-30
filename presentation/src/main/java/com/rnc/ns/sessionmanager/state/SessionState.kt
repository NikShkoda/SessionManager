package com.rnc.ns.sessionmanager.state

import com.rnc.ns.domain.model.Session

sealed class SessionState {
    object Idle: SessionState()
    object Loading: SessionState()
    data class Loaded(val session: Session): SessionState()
    data class Error(val message: String?): SessionState()
}
package com.rnc.ns.sessionmanager.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.rnc.ns.domain.usecase.GetSessionUseCase
import com.rnc.ns.sessionmanager.enums.Rotation
import com.rnc.ns.sessionmanager.sensor.sensor.GyroscopeSensor
import com.rnc.ns.sessionmanager.state.SessionState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class SessionManagerViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase
): ViewModel() {
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Idle)
    val servicesState: StateFlow<SessionState>
        get() = _sessionState.asStateFlow()

    private val _rotationState = mutableStateOf(Rotation.DEFAULT)
    val rotationState
        get() = _rotationState

    fun startSensorUpdates(sensorUpdates: Flow<Double>) {
        viewModelScope.launch {
            sensorUpdates.onEach { zRotation ->
                updateRotation(zRotation)
            }.catch { throwable ->
                _sessionState.value = SessionState.Error(throwable.message)
            }.launchIn(viewModelScope)
        }
    }

    private fun updateRotation(zRotation: Double) {
        when {
            zRotation <= -GyroscopeSensor.ANGLE -> {
                updateState(Rotation.ROTATED_LEFT)
            }
            zRotation >= GyroscopeSensor.ANGLE -> {
                updateState(Rotation.ROTATED_RIGHT)
            }
            else -> {
                updateState(Rotation.DEFAULT)
            }
        }
    }

    private fun updateState(rotation: Rotation) {
        viewModelScope.launch {
            if(_rotationState.value != rotation) {
                _rotationState.value = rotation
            }
        }
    }

    fun getSession() {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            getSessionUseCase().onEach { session ->
                _sessionState.value = SessionState.Loaded(session)
            }.catch { throwable ->
                _sessionState.value = SessionState.Error(throwable.message)
            }.launchIn(viewModelScope)
        }
    }
}
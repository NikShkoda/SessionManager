package com.rnc.ns.sessionmanager.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rnc.ns.domain.model.Session
import com.rnc.ns.sessionmanager.R
import com.rnc.ns.sessionmanager.enums.Rotation
import com.rnc.ns.sessionmanager.state.SessionState
import com.rnc.ns.sessionmanager.ui.theme.Typography
import com.rnc.ns.sessionmanager.viewmodel.SessionManagerViewModel
import kotlinx.coroutines.launch

@Composable
fun SessionManagerUI(viewModel: SessionManagerViewModel) {
    val sessionState by viewModel.servicesState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.getSession()
    }

    when (sessionState) {
        SessionState.Idle -> Unit
        SessionState.Loading -> {
            SessionManagerUILoading()
        }
        is SessionState.Loaded -> {
            SessionManagerUI(
                session = (sessionState as SessionState.Loaded).session,
                viewModel = viewModel
            )
        }
        is SessionState.Error -> {
            SessionManagerUIError(
                errorMessage = (sessionState as SessionState.Error).message,
                onRetry = {
                    coroutineScope.launch {
                        viewModel.getSession()
                    }
                }
            )
        }
    }
}

@Composable
fun SessionManagerUI(viewModel: SessionManagerViewModel, session: Session) {
    val rotationState by viewModel.rotationState
    val textStyle = when(rotationState) {
        Rotation.DEFAULT -> Typography.Default
        Rotation.ROTATED_LEFT -> Typography.RotatedLeft
        Rotation.ROTATED_RIGHT -> Typography.RotatedRight
    }
    SessionManagerUIText(textStyle = textStyle, session = session)
}

@Composable
fun SessionManagerUIText(textStyle: TextStyle, session: Session) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.format_session_count, session.sessionCount),
            style = textStyle
        )
    }
}

@Preview
@Composable
fun SessionManagerUITextPreview() {
    SessionManagerUIText(textStyle = Typography.Default, session = Session(sessionCount = 1))
}

@Composable
fun SessionManagerUIError(errorMessage: String?, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage ?: stringResource(id = R.string.error_generic),
            color = Color.Red,
            style = Typography.Default,
            modifier = Modifier.clickable {

            }
        )
    }
}

@Preview
@Composable
fun SessionManagerUIErrorPreview() {
    SessionManagerUIError(errorMessage = stringResource(id = R.string.error_generic)) {}
}

@Composable
fun SessionManagerUILoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
fun SessionManagerUILoadingPreview() {
    CircularProgressIndicator()
}

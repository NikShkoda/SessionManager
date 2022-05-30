package com.rnc.ns.domain.usecase

import com.rnc.ns.domain.repository.SessionRepository

class IncrementSessionCountUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke() = sessionRepository.incrementSessionCount()
}
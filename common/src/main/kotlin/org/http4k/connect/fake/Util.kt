package org.http4k.connect.fake

import org.http4k.chaos.Behaviour
import org.http4k.chaos.ChaosBehaviours.ReturnStatus
import org.http4k.chaos.ChaosEngine
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then

abstract class ChaosFake : HttpHandler {

    protected abstract val app: HttpHandler

    private val chaosEngine = ChaosEngine()

    fun behave() = chaosEngine.disable()
    fun misbehave(behaviour: Behaviour = ReturnStatus(Status.INTERNAL_SERVER_ERROR)) = chaosEngine.enable(behaviour)

    override operator fun invoke(request: Request): Response {
        return chaosEngine.then(app).invoke(request)
    }
}

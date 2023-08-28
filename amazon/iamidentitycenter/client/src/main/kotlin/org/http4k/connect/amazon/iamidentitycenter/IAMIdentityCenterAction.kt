package org.http4k.connect.amazon.iamidentitycenter

import dev.forkhandles.result4k.Result4k
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure

interface IAMIdentityCenterAction<R : Any> : Action<Result4k<R, RemoteFailure>>

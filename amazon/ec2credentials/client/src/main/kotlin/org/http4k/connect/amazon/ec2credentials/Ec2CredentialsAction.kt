package org.http4k.connect.amazon.ec2credentials

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure

interface Ec2CredentialsAction<R> : Action<Result<R, RemoteFailure>>

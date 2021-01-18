package org.http4k.connect.amazon.firehose.action

import org.http4k.connect.amazon.AwsJsonAction
import org.http4k.connect.amazon.firehose.Firehose
import org.http4k.connect.amazon.firehose.FirehoseMoshi
import org.http4k.format.AutoMarshalling
import kotlin.reflect.KClass

abstract class FirehoseAction<R : Any>(clazz: KClass<R>, autoMarshalling: AutoMarshalling = FirehoseMoshi) :
    AwsJsonAction<R>(Firehose.awsService, clazz, autoMarshalling)

package org.http4k.connect.amazon.iamidentitycenter.action

import org.http4k.connect.amazon.AwsJsonAction
import org.http4k.connect.amazon.iamidentitycenter.IAMIdentityCenter.Companion.awsService
import org.http4k.connect.amazon.iamidentitycenter.IAMIdentityCenterAction
import org.http4k.connect.amazon.iamidentitycenter.IAMIdentityCenterMoshi
import org.http4k.format.AutoMarshalling
import kotlin.reflect.KClass

abstract class IAMIdentityCenterAutomarshalledAction<R : Any>(
    clazz: KClass<R>,
    autoMarshalling: AutoMarshalling = IAMIdentityCenterMoshi
) : AwsJsonAction<R>(awsService, clazz, autoMarshalling), IAMIdentityCenterAction<R>

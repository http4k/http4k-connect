package org.http4k.connect

@Target(AnnotationTarget.CLASS)
annotation class Http4kConnectAdapter

@Target(AnnotationTarget.CLASS)
/**
 * Marker attached to all actions to drive the adapter code generation.
 *
 * link: Optional information for this action
 */
annotation class Http4kConnectAction(val docs: String = "")

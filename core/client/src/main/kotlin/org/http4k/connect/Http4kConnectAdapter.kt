package org.http4k.connect

@Target(AnnotationTarget.CLASS)
annotation class Http4kConnectAdapter

@Target(AnnotationTarget.CLASS)
/**
 * Marker attached to all actions to drive the adapter code generation.
 *
 * docs: Optional information for this action. Can be link or other notes.
 */
annotation class Http4kConnectAction(val docs: String = "")

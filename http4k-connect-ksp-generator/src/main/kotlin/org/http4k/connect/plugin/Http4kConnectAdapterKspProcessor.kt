package org.http4k.connect.plugin

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter

class Http4kConnectAdapterKspProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation(Http4kConnectAdapter::class.qualifiedName!!)
            .forEach {
                it.accept(
                    Http4kConnectAdapterVisitor { logger.warn(it.toString()) }, resolver
                        .getSymbolsWithAnnotation(Http4kConnectAction::class.qualifiedName!!)
                        .toList()
                )
            }
        return emptyList()
    }
}

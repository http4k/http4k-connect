package org.http4k.connect.plugin

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class Http4kConnectAdapterKspProcessor(
    private val logger: KSPLogger,
    codeGenerator: CodeGenerator
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        System.err.println("FOOBAR")
        logger.info("Http4kConnectAdapterKspProcessor was invoked.")
        return emptyList()
    }
}


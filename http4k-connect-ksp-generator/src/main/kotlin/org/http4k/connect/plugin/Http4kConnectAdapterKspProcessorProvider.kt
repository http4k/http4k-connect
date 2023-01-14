package org.http4k.connect.plugin

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class Http4kConnectAdapterKspProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Http4kConnectAdapterKspProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator
        )
    }
}

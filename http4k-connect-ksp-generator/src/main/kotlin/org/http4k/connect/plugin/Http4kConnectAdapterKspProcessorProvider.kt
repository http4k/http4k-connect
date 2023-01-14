package org.http4k.connect.plugin

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class Http4kConnectAdapterKspProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        Http4kConnectAdapterKspProcessor(environment.logger, environment.codeGenerator)
}

package org.http4k.connect.azure.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.asRemoteFailure
import org.http4k.connect.azure.AzureAIAction
import org.http4k.connect.azure.AzureAIMoshi
import org.http4k.connect.azure.AzureAIMoshi.autoBody
import org.http4k.core.ContentType
import org.http4k.core.Response
import org.http4k.lens.Header
import java.io.BufferedReader
import java.io.InputStreamReader

interface ModelCompletion : AzureAIAction<Sequence<CompletionResponse>> {

    val stream: Boolean
    fun content(): List<Message>

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> when {
                Header.CONTENT_TYPE(response)?.equalsIgnoringDirectives(ContentType.APPLICATION_JSON) == true ->
                    Success(listOf(autoBody<CompletionResponse>().toLens()(response)).asSequence())

                else -> Success(response.toSequence())
            }

            else -> Failure(asRemoteFailure(this))
        }
    }

    private fun Response.toSequence(): Sequence<CompletionResponse> {
        val reader = BufferedReader(InputStreamReader(body.stream, Charsets.UTF_8))
        return sequence {
            while (true) {
                val line = reader.readLine() ?: break
                if (line.startsWith("data: ")) {
                    when (val chunk = line.removePrefix("data: ").trim()) {
                        "[DONE]" -> break
                        else -> yield(AzureAIMoshi.asA<CompletionResponse>(chunk))
                    }
                }
            }
        }
    }
}

package org.http4k.connect.openai.auth.oauth

import java.security.SecureRandom
import java.util.UUID

fun interface SecureStrings : () -> String {
    companion object {
        fun Random(random: SecureRandom = SecureRandom.getInstanceStrong()) = SecureStrings {
            (0..4)
                .map { UUID(random.nextLong(), random.nextLong()) }
                .joinToString("-")
        }
    }
}

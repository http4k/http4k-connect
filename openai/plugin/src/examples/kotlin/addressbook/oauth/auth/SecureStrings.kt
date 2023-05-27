package addressbook.oauth.auth

import java.security.SecureRandom
import java.util.UUID

fun String.Companion.random(random: SecureRandom = SecureRandom.getInstanceStrong()) = (0..2)
    .map { UUID(random.nextLong(), random.nextLong()) }
    .joinToString("-")

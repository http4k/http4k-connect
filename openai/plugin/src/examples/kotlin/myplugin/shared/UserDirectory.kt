package myplugin.shared

import myplugin.user.UserId
import org.http4k.core.Credentials

/**
 * Simple user directory for credentials -> UserDetails
 */
class UserDirectory {
    private val store = mapOf(
        "sherlock" to UserDetails("Sherlock Holmes", "watson", "221b Baker St, London"),
        "paddington" to UserDetails("Paddington Bear", "marmalade", "Waterloo Station, London")
    )

    fun auth(credentials: Credentials) = find(UserId.of(credentials.user))
        ?.password == credentials.password

    fun find(user: UserId): UserDetails? = store[user.value]
}


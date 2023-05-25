package addressbook.shared

import org.http4k.core.Credentials

/**
 * Simple user directory for credentials -> UserDetails
 */
class UserDirectory {
    private val store = mapOf(
        UserId.of("sherlock") to UserDetails("Sherlock Holmes", "watson", "221b Baker St, London"),
        UserId.of("paddington") to UserDetails("Paddington Bear", "marmalade", "Waterloo Station, London")
    )

    fun auth(credentials: Credentials) = find(UserId.of(credentials.user))
        ?.password == credentials.password

    fun find(user: UserId): UserDetails? = store[user]

    fun all() = store.keys
}


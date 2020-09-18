package org.http4k.connect.storage

import org.http4k.core.Uri
import org.http4k.format.Jackson
import org.junit.jupiter.api.Disabled

@Disabled
class RedisStorageTest : StorageContract(Storage.Redis<String>(Uri.of("redis://localhost:7001"), Jackson))

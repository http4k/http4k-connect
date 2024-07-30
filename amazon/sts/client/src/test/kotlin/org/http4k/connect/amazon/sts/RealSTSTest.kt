package org.http4k.connect.amazon.sts

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsContract

class RealSTSTest : STSContract(), RealAwsContract {
    
    override val http = JavaHttpClient()

}

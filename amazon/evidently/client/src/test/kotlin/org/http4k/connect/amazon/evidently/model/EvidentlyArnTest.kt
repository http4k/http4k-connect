package org.http4k.connect.amazon.evidently.model

import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.evidently.Evidently

class ProjectNameTest {

    private val arn = ARN.of(
        awsService = Evidently.awsService,
        region = Region.CA_CENTRAL_1,
        account = AwsAccount.of("1234567890"),
        resourcePath = "projects/my_project/feature/my_feature"
    )
}

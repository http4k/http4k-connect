package org.http4k.lens

val Header.X_GITHUB_DELIVERY get() = Header.uuid().required("X-GitHub-Delivery")
val Header.X_HUB_SIGNATURE_256 get() = Header
    .map({ it.split("sha256=")[1] }, {"sha256=$it"})
    .required("X-Hub-Signature-256")

# Changelog

This list is not intended to be all-encompassing - it will document major and breaking API 
changes with their rationale when appropriate:

### v2.3.0.0
- **http4k-connect-** : Use Kotshi generated adapters instead of Kotlin Reflection, allowing removal of large Kotlin Reflection JAR.

### v2.2.2.0
- **http4k-connect-** : Generate and ship extension functions for all actions. Rename `S3.Bucket` to `S3Bucket`.

### v2.2.1.0
- **http4k-connect-** : Ship Javadoc.

### v2.2.0.0
- **http4k-connect-** : Repackage all action classes.

### v2.1.0.0
- **http4k-connect-** : Repackage all action classes.

### v2.0.2.1
- **http4k-connect-** : Switch all interfaces to use new `invoke()` mechanism.

### v1.1.0.1
- **http4k-connect-** : Upgrade http4k and Values4k.

### v1.0.1.0
- **http4k-connect-amazon-kms-fake** : Simplify signing.

### v1.0.0.0
- **http4k-connect-amazon-kms** : New client module.
- **http4k-connect-amazon-kms-fake** : New client fake module.
- **http4k-connect-amazon-s3** : New client module.
- **http4k-connect-amazon-s3-fake** : New client fake module.
- **http4k-connect-amazon-secretsmanager** : New client module.
- **http4k-connect-amazon-secretsmanager-fake** : New client fake module.
- **http4k-connect-amazon-systemsmanager** : New client module.
- **http4k-connect-amazon-systemsmanager-fake** : New client fake module.
- **http4k-connect-google-analytics** : New client module.
- **http4k-connect-storage-http** : New storage module.
- **http4k-connect-storage-jdbc** : New storage module.
- **http4k-connect-storage-redis** : New storage module.
- **http4k-connect-storage-s3** : New storage module.

### v0.20.0.0
- Initial release.

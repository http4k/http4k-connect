S3
====

The S3 connector consists of 2 interfaces:
=======

- `S3` for global operations - creating/deleting/listing buckets
- `S3.Bucket` for bucket level operations - CRUD key contents + list keys

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

## How the Fake works with bucket-level operations
S3 is a bit of a strange beast in that it each bucket gets it's own virtual hostname. This makes running a Fake an interesting challenge without messing around with DNS and hostname files.
 
 This implementation supports both global and bucket level operations by inspecting the subdomain of the X-Forwarded-For header, which is populated by the S3 client built into this module. 
 
 In the case of a missing header (if for instance a non-http4k client attempts to push some data into it without the x-forwarded-for header, it creates a global bucket which is then used to store all of the data for these unknown requests.

### Default Fake ports:
- Global: default port: 26467
- Bucket: default port: 42628

```
FakeS3().start()
```

# Kafka Schema Registry

The main `SchemaRegistry` connector provides the following Actions:

- CheckSchemaRegistered
- GetSchemaById
- GetSubjects
- GetSubjectVersion
- GetSubjectVersions
- RegisterSchema

## # Fake
The Fake provides the above actions.

### Default Fake port: 41466
To start:

```
FakeSchemaRegistry().start()
```

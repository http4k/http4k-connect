# http4k-connect contributions

### Notes for adding a new Client & Fake
- Use the `Example` project client and fake as a template module.
- The naming of the modules is: `http4k-connect-<vendor>-<system>`. We are also grouping the systems by vendor in directory structure. To add the modules in the right place in `settings.gradle.kts` use the functions provided.
- The work for adding other `http4k-connect` Gradle dependencies is already done in the core `build.gradle` file. You just need to add external dependencies into the module gradle file if there are any. If not, feel free to omit it.
- Fakes should extend `ChaoticHttpHandler`, which adds in the `misbehave()` and `behave()` functions to enable the Chaotic behaviour.
- Each Fake should implement the `FakeSystemContract`.
- Tests against external systems should be added wherever possible to prove the contracts are in place, or adding Docker* setup to run them.  *This is work in progress.

### Notes for adding Storage implementations
- The naming of the modules is: `http4k-connect-storage-<type>`. To add the module in the right place in `settings.gradle.kts` use the function provided.
- There is a contract `StorageContract` to prove that the implementation.
- Testcontainers can be used to prove out testing for various storage backends

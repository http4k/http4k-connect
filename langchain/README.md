# LangChain

LangChain4J is a versatile library that simplifies the creation and management of language processing workflows., It provides many integrations but does not allow for using http4k clients or http4k-connect adapters. This module gives you some of these integrations by writing LangChain model adapters.

Current adapters support http4k client integrations for the following models, allowing you to use them in your http4k applications:

- OpenAiChatLanguageModel
- OpenAiChatImageModel
- OpenAiChatEmbeddingModel
- S3 Document Loaders

Using these adapters is as simple as:

```kotlin
val model: ChatLanguageModel = OpenAiChatLanguageModel(OpenAI.Http(OpenAIToken.of("hello"), FakeOpenAI()))
val chat: Response<> = model.generate("hello kitty")
```

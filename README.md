# Shachi: High-Level HBase Client Abstraction Layer

## Summary

**Shachi** is a high-level abstraction layer which sits atop [Apache's Java client for HBase](https://mvnrepository.com/artifact/org.apache.hbase/hbase-client). Shachi provides the following features to simplify common HBase usage patterns:

- **Fluent API for reading and writing:** Shachi provides a fluent, chainable Java API, allowing the client to specify a full HBase get or put operation -- from defining the operation's HBase coordinates to reading the result -- within an easy-to-read chain of method calls.
- **High-level versioning model:** Designed for use cases involving the persistence of time-series data, or where it is important to maintain a "lineage" which documents the history of a data cell, Shachi's versioning model allows the client to specify how version information is to be encoded with the cell, and provides an easy-to-use API abstraction for specifying read/write version independent of the storage implementation details. Shachi versioning can be configured to be encoded either:
    - within the column qualifier
    - using the HBase timestamp
- **Resource pooling:** Shachi's `PoolingHBaseResourceManager` uses [Apache Commons Pool](https://commons.apache.org/proper/commons-pool/) to cache and reuse expensive-to-instantiate HBase admin and table objects, freeing the client from the responsibility for them (and from having to accept the performance impact of repeated reinitialization).
- **Strong typing of HBase structures:** Shachi includes classes which wrap HBase's native byte arrays and provide for semantically-specific objects, which are in turn required by the operations of the Shachi API. This approach aids in code self-documentation, and helps to reduce the errors which might result from confusing the untyped byte arrays intended for different parts of the HBase operation pipeline.
- **Asynchronous execution:** Execution of GETs and PUTs within Shachi is synchronous/blocking by default, but can be made asynchronous (populating results in a `Future`) simply by invoking the `.async()` API call prior to executing an operation.
- **Custom serialization and deserialization:** Per functional design principles, Shachi allows users of its API to (optionally) specify custom serializers and deserializers in the form of functions (or method references) which perform arbitrary `Object`-to-`byte[]` conversions (on the serialization side) or `byte[]`-to-`Object` conversions (on the deserialization side). This approach permits the user to execute write operations where fields are automatically serialized in a way consistent with a given schema, and execute read operations where the result set byte arrays are automatically converted to user-defined types.
- **Batching of operations:** For ease of execution, Shachi allows multiple operations to be batched together in a single chain of API calls. 

## Naming

The name **"Shachi" (シャチ)** is Japanese for orca (killer whale), which is the mascot for Apache HBase: https://hbase.apache.org/book.html#orca

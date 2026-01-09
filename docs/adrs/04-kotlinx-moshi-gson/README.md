# ADR: JSON serialization choice (Kotlinx Serialization vs Moshi vs Gson)

- Status: Accepted
- Date: 9/1/2026
- Deciders: Fakhry
- Drivers: Kotlin-first support, performance and type safety (nullability).

## Table of contents
- [Context](#context)
- [Decision](#decision)
- [Options considered](#options-considered)
- [Pros and cons](#pros-and-cons)
  - [Kotlinx Serialization](#kotlinx-serialization)
  - [Moshi](#moshi)
  - [Gson](#gson)
- [Comparison](#comparison)
- [Performance and size](#performance-and-size)
- [Consequences and guidelines](#consequences-and-guidelines)
- [References](#references)

## Context
We need a single JSON serialization library for Android projects to ensure consistent models, nullability handling, and integration with networking (Retrofit/Ktor). The choice should favor Kotlin-first support and, if relevant, potential multiplatform reuse.

## Decision
We will use Kotlinx Serialization as the default JSON serialization library for new Android Kotlin code.

## Options considered
1) Kotlinx Serialization as the default (Retrofit/Ktor converters)  
2) Moshi with Kotlin codegen/reflection as needed  
3) Gson (legacy/avoid for new code)

## Pros and cons

### Kotlinx Serialization
- Pros:
  - Kotlin-first, multiplatform ready; no reflection by default.
  - Strong nullability handling and sealed/class support.
  - Works with Retrofit and Ktor via converters; integrates with coroutines/Flow.
- Cons:
  - Requires `@Serializable` annotations and plugin; adds a Gradle plugin step.
  - Smaller ecosystem than Moshi for some custom adapters.

### Moshi
- Pros:
  - Mature, widely used on Android; good nullability handling with Kotlin codegen.
  - Rich ecosystem of adapters; reflection mode available for flexibility.
  - Retrofit support is first-class.
- Cons:
  - Codegen or reflection adds overhead; reflection is slower.
  - Not multiplatform; JVM/Android focused.

### Gson
- Pros:
  - Ubiquitous and simple to set up; no codegen plugin.
  - Large set of examples and legacy support.
- Cons:
  - Reflection-heavy; slower and less strict about nullability/types.
  - Poorer sealed/class support; easier to hide bugs.
  - Considered legacy for new Kotlin-first codebases.

## Comparison
| Criteria                 | Kotlinx Serialization                       | Moshi                                      | Gson                              |
|--------------------------|---------------------------------------------|--------------------------------------------|-----------------------------------|
| Kotlin-first/KMP         | Yes                                         | No                                         | No                                |
| Nullability/strictness   | Strong                                      | Strong with codegen                        | Weak (reflection, lenient)        |
| Performance              | Good; no reflection by default              | Good with codegen; slower with reflection  | Slower (reflection)               |
| Ecosystem/adapters       | Growing; fewer adapters than Moshi          | Rich adapters                              | Legacy; fewer modern adapters     |
| Retrofit/Ktor support    | Yes (converters)                            | Yes (Retrofit converters)                  | Yes (Retrofit converters)         |
| Setup                    | Plugin + annotations                        | Codegen or reflection                      | Simple, reflection only           |
| Multiplatform            | Yes                                         | No                                         | No                                |

## Performance and size
- Kotlinx Serialization: no reflection; generally fast and small. Requires plugin but code size is modest.
- Moshi: good with codegen; reflection mode slower and adds overhead.
- Gson: reflection-heavy; slower and more lenient, generally not recommended for new Kotlin code.

## Consequences and guidelines
- Default to Kotlinx Serialization for new Kotlin/Compose/KMP-aligned modules.
- Use Moshi only when a legacy codebase or specific adapters require it; prefer Moshi codegen over reflection.
- Avoid introducing Gson in new code; keep it only where legacy compatibility is mandatory and plan migration.
- Provide shared converters/adapters and nullability rules; add tests for polymorphic/edge cases.

## References
- Kotlinx Serialization: https://github.com/Kotlin/kotlinx.serialization
- Moshi: https://github.com/square/moshi
- Gson: https://github.com/google/gson

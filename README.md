# Topp Gradle Convention

Reusable Gradle convention plugins in Kotlin DSL for large multi-module Java builds:
JPMS, strict compilation, testing, publishing to Maven Central, SBOMs, and more.
Use them standalone or in a hierarchy. They're ready for the configuration cache and Gradle 10.

It can be used together with [Topp-Gradle-Bootstrap](https://github.com/sabroe/Topp-Gradle-Bootstrap),
which keeps the conventions up to date automatically. This is optional: a local clone included with `includeBuild(...)` works just as well.

For an example of combining bootstrap and conventions — and the build principles behind them —
see the [Topp Standard README](https://github.com/sabroe/Topp-Standard/blob/main/README.md#setup).

**-- Morten Sabroe Mortensen**

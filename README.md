# Topp Gradle Convention

[![Used by Topp Standard](https://img.shields.io/badge/Used_by-Topp_Standard-0366D6?logo=github)](https://github.com/sabroe/Topp-Standard)
[![Gradle](https://img.shields.io/badge/Gradle-9_%7C_10--ready-02303A?logo=gradle)](https://github.com/sabroe/Topp-Standard/blob/main/README.md#built-for-whats-next)
[![Configuration Cache](https://img.shields.io/badge/Configuration_Cache-on-2EA44F?logo=gradle)](https://github.com/sabroe/Topp-Standard/blob/main/README.md#built-for-whats-next)
[![License](https://img.shields.io/github/license/sabroe/Topp-Gradle-Convention)](LICENSE)
[![Last commit](https://img.shields.io/github/last-commit/sabroe/Topp-Gradle-Convention)](https://github.com/sabroe/Topp-Gradle-Convention/commits/main)

Reusable Gradle convention plugins in Kotlin DSL for large multi-module Java builds:
JPMS, strict compilation, testing, publishing to Maven Central, SBOMs, and more.
Use them standalone or in a hierarchy. They're ready for the configuration cache and Gradle 10.

It can be used together with [Topp-Gradle-Bootstrap](https://github.com/sabroe/Topp-Gradle-Bootstrap),
which keeps the conventions up to date automatically. This is optional: a local clone included with `includeBuild(...)` works just as well.

For an example of combining bootstrap and conventions — and the build principles behind them —
see the [Topp Standard README](https://github.com/sabroe/Topp-Standard/blob/main/README.md#setup).

**-- Morten Sabroe Mortensen**

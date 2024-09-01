# Scribe - a library to help with writing Babashka scripts

A few utility namespaces extracted from working Babashka scripts and designed to solve common problems simply.

Expanded and refined from [this idea](https://endot.org/2023/12/30/writing-babashka-scripts/#combining-power-with-ease-of-use).

* [API documentation](./API.md)
* [CHANGELOG](./CHANGELOG.md)

Namespaces included:

* [scribe.highlight](./API.md#scribe.highlight) - Highlights strings with color by regex
* [scribe.opts](./API.md#scribe.opts) - Handle command line options functionally
* [scribe.main](./API.md#scribe.main) - High level script helpers
* [scribe.string](./API.md#scribe.string) - String utilities

# Build/release

```shell
clojure -A:deps -T:build help/doc
```

# License

Copyright © 2024 Nate Jones

Distributed under the EPL License. See LICENSE.

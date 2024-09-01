# Scribe - a library to help with writing Babashka scripts

A few utility namespaces extracted from working Babashka scripts and designed to solve common problems simply.

* [API documentation](./API.md)
* [CHANGELOG](./CHANGELOG.md)

Namespaces included:

* [scribe.highlight](./API.md#scribe.highlight) - Highlights strings with color by regex
* [scribe.opts](./API.md#scribe.opts) - Handle command line options functionally
* [scribe.main](./API.md#scribe.main) - High level script helpers
* [scribe.string](./API.md#scribe.string) - String utilities
* [scribe.parse](./API.md#scribe.parse) - Parse incoming data resiliently

# Build/release

```shell
clojure -A:deps -T:build help/doc
```

# License

Copyright © 2024 Nate Jones

Distributed under the EPL License. See LICENSE.

# Table of contents
-  [`scribe.highlight`](#scribe.highlight)  - Utilities for highlighting portions of strings with color.
    -  [`add`](#scribe.highlight/add) - Highlight regex matches in line string by adding color.
    -  [`bg`](#scribe.highlight/bg) - Return a string wrapped in the proper escape codes to set the background color in the passed string.
    -  [`colors-for-dark`](#scribe.highlight/colors-for-dark)
    -  [`colors-for-light`](#scribe.highlight/colors-for-light)
    -  [`default-opts`](#scribe.highlight/default-opts) - Default options for add function.
    -  [`fg`](#scribe.highlight/fg) - Return a string wrapped in the proper escape codes to set the foreground color in the passed string.
    -  [`rgb-code`](#scribe.highlight/rgb-code) - Generate 8-bit RGB code.
-  [`scribe.main`](#scribe.main)  - Top-level script runner utilities.
    -  [`basic`](#scribe.main/basic) - Single-file script helper.
-  [`scribe.opts`](#scribe.opts)  - A set of functions to handle command line options in an opinionated functional manner.
    -  [`detect-script-name`](#scribe.opts/detect-script-name) - Detect the name of the currently running script, for usage in the printed help.
    -  [`format-help`](#scribe.opts/format-help) - Take an error (as returned from <code>validate</code>) and format the help message that will be printed to the end user.
    -  [`print-and-exit`](#scribe.opts/print-and-exit) - Print help message and exit.
    -  [`validate`](#scribe.opts/validate) - Look for the most common of errors: * <code>--help</code> was passed * clojure.tools.cli detected errors To detect other errors specific to a given script, wrap the call with an <code>or</code>, like this: (or (opts/validate parsed usage-text) (script-specific-validate parsed)) The script-specific-validate function should return a map with information about the error that occurred.
-  [`scribe.parse`](#scribe.parse) 
    -  [`custom-parser`](#scribe.parse/custom-parser)
    -  [`default-opts`](#scribe.parse/default-opts)
    -  [`default-parse`](#scribe.parse/default-parse) - Parse a single line using default options.
-  [`scribe.string`](#scribe.string)  - String utilities.
    -  [`dedent`](#scribe.string/dedent) - Remove leading indent on strings.

-----
# <a name="scribe.highlight">scribe.highlight</a>


Utilities for highlighting portions of strings with color. Primary
  entrypoint is the [`add`](#scribe.highlight/add) function.




## <a name="scribe.highlight/add">`add`</a><a name="scribe.highlight/add"></a>
``` clojure

(add string regex)
(add string regex opts)
```

Highlight regex matches in line string by adding color. All instances of the
  same match are colored the same. The color is picked by hashing the match
  into an index into available colors, which makes the coloring stable across
  multiple runs.

  Options include:
  * :colors   - Which set of colors to use. Default is colors suitable for a
                dark background (colors-for-dark). For light backgrounds, use
                colors-for-light.
  * :explicit - Explicit colors for specific matched strings. Map of string to
                color code.
  * :offset   - Additional offset after calculating color code. Defaults to 0.
  * :reverse? - Should matches be reversed before selecting a color. Setting
                this to true can help differentiate matches that share a common
                prefix.
  
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/highlight.clj#L76-L97">Source</a></sub></p>

## <a name="scribe.highlight/bg">`bg`</a><a name="scribe.highlight/bg"></a>
``` clojure

(bg string color)
```

Return a string wrapped in the proper escape codes to set the background
  color in the passed string.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/highlight.clj#L49-L53">Source</a></sub></p>

## <a name="scribe.highlight/colors-for-dark">`colors-for-dark`</a><a name="scribe.highlight/colors-for-dark"></a>



<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/highlight.clj#L28-L28">Source</a></sub></p>

## <a name="scribe.highlight/colors-for-light">`colors-for-light`</a><a name="scribe.highlight/colors-for-light"></a>



<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/highlight.clj#L31-L31">Source</a></sub></p>

## <a name="scribe.highlight/default-opts">`default-opts`</a><a name="scribe.highlight/default-opts"></a>




Default options for add function.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/highlight.clj#L69-L74">Source</a></sub></p>

## <a name="scribe.highlight/fg">`fg`</a><a name="scribe.highlight/fg"></a>
``` clojure

(fg string color)
```

Return a string wrapped in the proper escape codes to set the foreground
  color in the passed string.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/highlight.clj#L43-L47">Source</a></sub></p>

## <a name="scribe.highlight/rgb-code">`rgb-code`</a><a name="scribe.highlight/rgb-code"></a>
``` clojure

(rgb-code r g b)
```

Generate 8-bit RGB code.

  From: https://en.wikipedia.org/wiki/ANSI_escape_code#8-bit
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/highlight.clj#L9-L17">Source</a></sub></p>

-----
# <a name="scribe.main">scribe.main</a>


Top-level script runner utilities. These abstract the boilerplate of common
  option parsing and error printing.




## <a name="scribe.main/basic">`basic`</a><a name="scribe.main/basic"></a>
``` clojure

(basic opts)
(basic opts args)
```

Single-file script helper. This function will parse command line arguments
  and take care of displaying help and any errors.

  Takes a single map of options:
  * :usage - Usage information to show in `--help` output. Can be multiple
             lines. Any occurances of 'SCRIPT_NAME' in this string will be
             replaced with the actual script's name.
  * :cli-options - Argument parsing configuration for clojure.tools.cli/parse-opts.
  * :validate-fn - (optional) A function that takes the result of
                   clojure.tools.cli/parse-opts to further validate the command
                   line arguments. This function should return the same
                   information that the built-in validate (in [`scribe.opts`](#scribe.opts))
                   function returns.
  * :script-name - (optional) The name of the script, inferred from the script
                   filename if not passed.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/main.clj#L7-L35">Source</a></sub></p>

-----
# <a name="scribe.opts">scribe.opts</a>


A set of functions to handle command line options in an opinionated
  functional manner. Here is the general strategy:

  1. Args are parsed by clojure.tools.cli.
  2. The parsed args are examined for errors and the --help flag with a pure
     function.
  3. If errors are found, an appropriate message (optionally with usage) is
     assembled with a pure function.
  4. The message is printed and the script exits.

  Most of the above is pure, and therefore testable. Here's an example main
  function:

    (defn -main
      [& args]
      (let [parsed (parse-opts args [["-h" "--help" "Show help"]
                                     ["-n" "--name NAME" "Name to use" :default "world"]])
            {:keys [name]} (:options parsed)]
        (or (some-> (opts/validate parsed usage-text)
                    (opts/format-help parsed)
                    (opts/print-and-exit))
            (println "Hello" name))))

  For a more complete sample script, check out `samples` in the repository.




## <a name="scribe.opts/detect-script-name">`detect-script-name`</a><a name="scribe.opts/detect-script-name"></a>
``` clojure

(detect-script-name)
(detect-script-name filename)
```

Detect the name of the currently running script, for usage in the printed
  help.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/opts.clj#L61-L70">Source</a></sub></p>

## <a name="scribe.opts/format-help">`format-help`</a><a name="scribe.opts/format-help"></a>
``` clojure

(format-help errors parsed)
(format-help errors script-name-or-ns parsed)
```

Take an error (as returned from [`validate`](#scribe.opts/validate)) and format the help message
  that will be printed to the end user.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/opts.clj#L81-L96">Source</a></sub></p>

## <a name="scribe.opts/print-and-exit">`print-and-exit`</a><a name="scribe.opts/print-and-exit"></a>
``` clojure

(print-and-exit {:keys [help exit]})
```

Print help message and exit. Accepts a map with `:help`
  and `:exit` keys.

  Uses the :babashka/exit ex-info trick to exit Babashka.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/opts.clj#L98-L104">Source</a></sub></p>

## <a name="scribe.opts/validate">`validate`</a><a name="scribe.opts/validate"></a>
``` clojure

(validate parsed usage)
```

Look for the most common of errors:
  * `--help` was passed
  * clojure.tools.cli detected errors

  To detect other errors specific to a given script, wrap the call with an
  `or`, like this:

  (or (opts/validate parsed usage-text)
      (script-specific-validate parsed))

  The script-specific-validate function should return a map with information
  about the error that occurred. The keys are:
  * :message - (optional) Message to be printed
  * :exit - The numeric exit code that should be returned
  * :wrap-context - Whether or not to wrap the message with script help heading
                    and options documentation
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/opts.clj#L30-L59">Source</a></sub></p>

-----
# <a name="scribe.parse">scribe.parse</a>






## <a name="scribe.parse/custom-parser">`custom-parser`</a><a name="scribe.parse/custom-parser"></a>
``` clojure

(custom-parser opts)
```
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/parse.clj#L26-L45">Source</a></sub></p>

## <a name="scribe.parse/default-opts">`default-opts`</a><a name="scribe.parse/default-opts"></a>



<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/parse.clj#L5-L6">Source</a></sub></p>

## <a name="scribe.parse/default-parse">`default-parse`</a><a name="scribe.parse/default-parse"></a>
``` clojure

(default-parse line)
```




Parse a single line using default options.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/parse.clj#L47-L51">Source</a></sub></p>

-----
# <a name="scribe.string">scribe.string</a>


String utilities.




## <a name="scribe.string/dedent">`dedent`</a><a name="scribe.string/dedent"></a>
``` clojure

(dedent string)
(dedent indent string)
```

Remove leading indent on strings. Typically called on strings defined in
  scripts that are to be printed to the terminal. If leading indent is not
  passed, it will be detected from the first line with leading whitespace.
<p><sub><a href="https://github.com/justone/scribe/blob/master/src/scribe/string.clj#L14-L23">Source</a></sub></p>

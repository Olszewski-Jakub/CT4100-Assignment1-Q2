# Running on macOS

Install JDK 17, then run these commands from the project directory:

```sh
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew run
```

Run tests:

```sh
./gradlew test
```

Edit `src/main/kotlin/dev/jakubolszewski/DemoArticles.kt` to change the sample articles and comparison IDs.

## Stopword source

The stopword list in `src/main/resources/stopwords-onix.txt` comes from [Lars Yencken's `stopwords.txt` gist](https://gist.github.com/larsyencken/1440509), which credits the ONIX stopword list from Lextek.
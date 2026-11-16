#!/usr/bin/env bash
# Targeted client validation using unchanged production services and real contracts.
# Requires a graphical session, Maven, ripgrep and JAVA_HOME (JDK 21+).
# Internal COM launchers/mocks are excluded; this is not a full Maven build.
set -euo pipefail
cd "$(dirname "$0")/../../.."
: "${JAVA_HOME:?Set JAVA_HOME to a JDK 21 or newer}"
mkdir -p target/ihm-client-tests
mvn -q dependency:build-classpath -Dmdep.outputFile=target/ihm-client-tests/classpath.txt
mvn -q dependency:get -Dartifact=org.junit.platform:junit-platform-console-standalone:1.12.1
console_jar="$HOME/.m2/repository/org/junit/platform/junit-platform-console-standalone/1.12.1/junit-platform-console-standalone-1.12.1.jar"
classpath="$(cat target/ihm-client-tests/classpath.txt):$console_jar"
classes="$(mktemp -d "$(pwd)/target/ihm-client-tests/classes.XXXXXX")"
sources=()
while IFS= read -r source; do sources+=("$source"); done < <(
  rg --files src/main/java src/test/java/client/ihmmain src/test/java/client/main -g '*.java' \
    -g '!module-info.java' -g '!comm_integration_tests/**' -g '!**/comm_integration_tests/**' \
    -g '!src/main/java/client/communication/Launcher.java'
)
"$JAVA_HOME/bin/javac" --release 21 -cp "$classpath" -d "$classes" "${sources[@]}"
"$JAVA_HOME/bin/java" --enable-native-access=ALL-UNNAMED -cp "$classes:src/main/resources:$classpath" \
  org.junit.platform.console.ConsoleLauncher execute --scan-class-path="$classes" \
  --reports-dir=target/ihm-client-tests/reports --disable-banner --fail-if-no-tests

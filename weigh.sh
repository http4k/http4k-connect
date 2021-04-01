find . -name "http4k-connect-amazon*.jar" | grep -v fake | grep -v test | xargs ls -l

./gradlew weighDependencies | grep org | grep -v fake | grep -v test | grep LOCAL
./gradlew weighDependencies | grep software

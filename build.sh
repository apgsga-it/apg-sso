#!/bin/sh

echo "Building keycloak ..."
(
    cd keycloak
    ./mvnw clean verify -DskipTests
)

echo
echo "Building vaadin8-app ..."
(
    cd vaadin8-app
    ./mvnw clean verify -DskipTests
)

echo
echo "Building angular-app ..."
(
    cd angular-app
    ./mvnw clean verify -DskipTests
)

echo
echo "done"

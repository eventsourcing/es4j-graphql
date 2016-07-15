[![Download](https://api.bintray.com/packages/eventsourcing/maven/eventsourcing-graphql/images/download.svg)](https://bintray.com/eventsourcing/maven/eventsourcing-graphql/_latestVersion)

# ES4J (Eventsourcing for Java) GraphQL adaptor

This module implements a Relay.js-compatible GraphQL server intended to be used with [EJS4](https://github.com/eventsourcing/es4j) commands. It also supports
OSGi out of the box.

# Downloading

You can download es4j-graphql from bintray (Gradle syntax):

```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile 'com.eventsourcing:eventsourcing-graphql:0.4.0'
}
```

# Usage

The are a few important components this package provides (some of them come from [graphql-java-servlet](https://github.com/yrashk/graphql-java-servlet)):

* GraphQLQueryProvider/GraphQLMutationProvider interfaces. These will allow you
  to define which "domain model" views and which commands you are going to expose.
* GraphQLServlet as an entry point servlet. Use `bindQueryProvider`/`bindMutationProvider` i or automatically wire
them in OSGi.

## Commands as mutations

```java
@Accessors(fluent = true)
@GraphQLName("test")
@Value
public static class TestCommand extends StandardCommand<String> {
    private String value;
}
```

There are two ways to expose such commands, one is to use `PackageGrapgQLMutationProvider` to scan
all relevant packages to find subclasses of `GraphQLCommand` in those packages. For OSGi environments,
`BundleGraphQLMutationProvider` should be used to scan relevant bundles.

## Domain models as queries

```java
public static class TestQueryProvider implements GraphQLQueryProvider {

    @GraphQLName("testObject")
    public static class TestObject {
        @GraphQLField
        public String field;
    }

    @Override @SneakyThrows
    public GraphQLObjectType getQuery() {
        return GraphQLAnnotations.object(Query.class);
    }

    @Override
    public Object context() {
        return new Query();
    }
}
```

Since it implements `GraphQLQueryProvider`, it can be bound to the servlet
and will be automatically exposed as `testObject { field }`

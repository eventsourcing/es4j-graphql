![Download](https://api.bintray.com/packages/eventsourcing/maven/eventsourcing-graphql/images/download.svg) ](https://bintray.com/eventsourcing/maven/eventsourcing-graphql/_latestVersion)

# ES4J (Eventsourcing for Java) GraphQL adaptor

This module implements a Relay.js-compatible GraphQL server intended to be used with [EJS4](https://github.com/eventsourcing/es4j) commands. It also supports
OSGi out of the box.

# Downloading

You can download es4j-graphql from bintray (Gradle syntax):

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/eventsourcing/maven"
    }
}

dependencies {
  compile 'com.eventsourcing:eventsourcing-graphql:0.1.1'
}
```

# Usage

The are a few important components this package provides:

* GraphQLQueryProvider/GrapgQLMutationProvider interfaces. These will allow you
  to define which "domain model" views and which commands you are going to expose.
* GraphQLServlet as an entry point servlet. Use `bindQueryProvider`/`bindMutationProvider` in a subclass or automatically wire
them in OSGi.
* GraphQLCommand is a superclass for all ES$J command-backed mutations.

## Commands as mutations

```java
@Accessors(fluent = true)
@GraphQLName("test")
public static class TestCommand extends GraphQLCommand<String> implements GraphQLMutationProvider {
    @Getter(onMethod = @__(@GraphQLField)) @Setter
    private String value;

    public TestCommand() {
        super(Result.class);
    }

    public Stream<Event> events(Repository repository) {
         return Stream.of(...);
    }

    public String onCompletion() {
         return value;
    }
}
```

Since it implements `GraphQLMutationProvider`, it can be bound to the servlet
and will be automatically exposed as `test(input: {value: <string>, clientMutationId: <string>})`

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

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import com.eventsourcing.Repository;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.annotations.EnhancedExecutionStrategy;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLSchema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GraphQLCommandTest {

    @AllArgsConstructor
    public static class Result {
        @Getter(onMethod = @__(@GraphQLField))
        private String value;
    }

    @Accessors(fluent = true)
    @GraphQLName("test")
    public static class TestCommand extends GraphQLCommand<Result>  {
        @Getter(onMethod = @__(@GraphQLField)) @Setter
        private String value;
    }

    @Test
    public void test() {
        Repository repository = mock(Repository.class);
        when(repository.publish(any())).thenReturn(CompletableFuture.completedFuture(new Result("passed")));
        GraphQLContext<TestCommand> context = new GraphQLContext<>(repository, Optional.empty(), Optional.empty());
        Collection<GraphQLFieldDefinition> mutations = new PackageGraphQLMutationProvider(
                new Package[]{this.getClass().getPackage()}).getMutations();
        GraphQLSchema schema = newSchema().query(newObject().name("query").build()).
                mutation(newObject().name("mutation").fields(new ArrayList(mutations)).build()).build();
        GraphQL graphQL = new GraphQL(schema, new EnhancedExecutionStrategy());
        ExecutionResult result = graphQL.execute(
                "mutation { test(input: {value: \"test\", clientMutationId: \"1\"}) { clientMutationId, value} }",
                context);
        assertTrue(result.getErrors().isEmpty());
        Map<String, Object> data = (Map<String, Object>) result.getData();
        Map<String, Object> test = (Map<String, Object>) data.get("test");
        assertEquals(test.get("value"), "passed");
        assertEquals(test.get("clientMutationId"), "1");
    }

}
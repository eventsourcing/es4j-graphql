/**
 * Copyright 2016 Eventchain team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package org.eventchain.graphql;

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
import org.eventchain.Repository;
import org.testng.annotations.Test;

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
    public static class TestCommand extends GraphQLCommand<Result> implements GraphQLMutationProvider {
        @Getter(onMethod = @__(@GraphQLField)) @Setter
        private String value;

        public TestCommand() {
            super(Result.class);
        }
    }

    @Test
    public void test() {
        TestCommand command = new TestCommand();
        Repository repository = mock(Repository.class);
        when(repository.publish(any())).thenReturn(CompletableFuture.completedFuture(new Result("passed")));
        GraphQLContext<TestCommand> context = new GraphQLContext<>(repository, Optional.empty(), Optional.empty(), command);
        GraphQLFieldDefinition mutation = command.getMutation();
        GraphQLSchema schema = newSchema().query(newObject().name("query").build()).
                mutation(newObject().name("mutation").field(mutation).build()).build();
        GraphQL graphQL = new GraphQL(schema, new EnhancedExecutionStrategy());
        ExecutionResult result = graphQL.execute("mutation { test(input: {value: \"test\", clientMutationId: \"1\"}) { clientMutationId, value} }", context);
        assertTrue(result.getErrors().isEmpty());
        Map<String, Object> data = (Map<String, Object>) result.getData();
        Map<String, Object> test = (Map<String, Object>) data.get("test");
        assertEquals(test.get("value"), "passed");
        assertEquals(test.get("clientMutationId"), "1");
    }

}
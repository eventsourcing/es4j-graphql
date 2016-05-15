/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import com.eventsourcing.Entity;
import com.eventsourcing.layout.Layout;
import com.eventsourcing.layout.Property;
import graphql.annotations.GraphQLAnnotations;
import graphql.schema.*;
import graphql.servlet.GraphQLMutationProvider;
import lombok.SneakyThrows;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

public abstract class AbstractGraphQLMutationProvider implements GraphQLMutationProvider {

    protected Predicate<Class<? extends Entity>> classPredicate = klass ->
            Modifier.isPublic(klass.getModifiers()) &&
                    (!klass.isMemberClass() || (klass.isMemberClass() && Modifier
                            .isStatic(klass.getModifiers()))) &&
                    !Modifier.isAbstract(klass.getModifiers());

    @SneakyThrows
    protected GraphQLFieldDefinition getMutation(Class<? extends GraphQLCommand> klass) {
        return getMutation(klass, (Class) ((ParameterizedType) klass.getAnnotatedSuperclass().getType())
                .getActualTypeArguments()[0]);
    }

    @SneakyThrows
    protected GraphQLFieldDefinition getMutation(Class<? extends GraphQLCommand> klass, Class resultClass) {
        GraphQLObjectType objectType = GraphQLAnnotations.object(klass);
        GraphQLObjectType resultType = GraphQLAnnotations.objectBuilder(resultClass)
                                                         .name(getClass().getSimpleName())
                                                         .field(newFieldDefinition().name("clientMutationId")
                                                                                    .type(GraphQLString)
                                                                                    .dataFetcher(
                                                                                            e -> ((GraphQLCommand) ((GraphQLContext) e
                                                                                                    .getContext())
                                                                                                    .getCommand())
                                                                                                    .getClientMutationId())
                                                                                    .build()).build();

        Layout<? extends GraphQLCommand> layout = new Layout<>(klass);
        GraphQLFieldDefinition.Builder builder = newFieldDefinition()
                .name(objectType.getName())
                .type(resultType)
                .argument(newArgument().name("input")
                                       .type(new Mutation(
                                               objectType))
                                       .build())
                .dataFetcher(new DataFetcher() {
                    @SneakyThrows
                    @Override public Object get(DataFetchingEnvironment environment) {
                        GraphQLCommand instance = klass.newInstance();
                        instance.environment(environment);

                        Map<String, Object> input = (Map<String, Object>) environment.getArguments().values()
                                                                                     .toArray()[0];

                        instance.setClientMutationId((String) input.get("clientMutationId"));

                        for (Property property : layout.getProperties()) {
                            Object value = input.get(property.getName());
                            property.set(instance,
                                         property.getType().isInstanceOf(Optional.class) ? Optional.of(value) : value);
                        }

                        GraphQLContext context = (GraphQLContext) environment.getContext();
                        context.setCommand(instance);

                        instance.beforePublishing();
                        CompletableFuture future = context.getRepository().publish(instance);
                        return future.get();
                    }
                });

        return builder.build();
    }

    public static class Mutation extends GraphQLInputObjectType {
        public Mutation(GraphQLObjectType objectType) {
            super(objectType.getName() + "Input", objectType.getDescription(), fields(objectType));
        }

        private static List<GraphQLInputObjectField> fields(GraphQLObjectType objectType) {
            List<GraphQLInputObjectField> fields = new ArrayList<>();
            for (GraphQLFieldDefinition field : objectType.getFieldDefinitions()) {
                GraphQLInputObjectField inputField = newInputObjectField()
                        .name(field.getName())
                        .description(field.getDescription())
                        .type(field.getType() instanceof
                                      GraphQLObjectType ? GraphQLAnnotations
                                .inputObject(
                                        (GraphQLObjectType) field
                                                .getType()) : (GraphQLInputType) field
                                .getType())
                        .build();
                fields.add(inputField);
            }
            return fields;
        }
    }
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import graphql.Scalars;
import graphql.annotations.TypeFunction;
import graphql.schema.GraphQLType;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Component
public class UUIDFunction implements TypeFunction {
    @Override
    public GraphQLType apply(Class<?> aClass, AnnotatedType annotatedType) {
        return Scalars.GraphQLString;
    }

    @Override public Collection<Class<?>> getAcceptedTypes() {
        return Collections.singletonList(UUID.class);
    }
}

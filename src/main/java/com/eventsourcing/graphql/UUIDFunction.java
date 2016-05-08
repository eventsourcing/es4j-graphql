/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import graphql.Scalars;
import graphql.annotations.TypeFunction;
import graphql.schema.GraphQLType;

import java.lang.reflect.AnnotatedType;

public class UUIDFunction implements TypeFunction {
    @Override
    public GraphQLType apply(Class<?> aClass, AnnotatedType annotatedType) {
        return Scalars.GraphQLString;
    }
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import com.eventsourcing.Command;
import graphql.schema.GraphQLFieldDefinition;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class PackageGraphQLMutationProvider extends AbstractGraphQLMutationProvider {

    private final String[] packages;
    private final ClassLoader[] classLoaders;

    public PackageGraphQLMutationProvider(Package[] packages) {
        this(packages, new ClassLoader[]{});
    }

    public PackageGraphQLMutationProvider(Package[] packages, ClassLoader[] classLoaders) {
        this.packages = Arrays.asList(packages).stream().map(Package::getName).toArray(String[]::new);
        this.classLoaders = classLoaders;
    }


    @Override public Collection<GraphQLFieldDefinition> getMutations() {
        Configuration configuration = ConfigurationBuilder.build((Object[]) packages).addClassLoaders(classLoaders);
        Reflections reflections = new Reflections(configuration);
        return reflections.getSubTypesOf(Command.class).stream()
                          .filter(classPredicate)
                          .map(this::getMutation)
                          .collect(Collectors.toSet());
    }

}

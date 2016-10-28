/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import com.eventsourcing.Command;
import graphql.annotations.GraphQLMutation;
import graphql.schema.GraphQLFieldDefinition;
import graphql.servlet.GraphQLMutationProvider;
import lombok.Getter;
import lombok.SneakyThrows;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component(immediate = true)
public class OSGiMutationProvider extends AbstractGraphQLMutationProvider implements GraphQLMutationProvider {

    private BundleTracker<Object> tracker;


    @Activate
    protected void activate(ComponentContext context) {
        tracker = new BundleTracker<>(context.getBundleContext(), Bundle.ACTIVE,
                                      new ScannerBundleTracker());
        tracker.open();
    }

    @Deactivate
    protected void deactivate() {
        tracker.close();
    }

    <T> List<Class<? extends T>> findSubTypesOf(Bundle bundle, Collection<Class<T>> superclasses) {
        BundleWiring wiring = bundle.adapt(BundleWiring.class);
        Collection<String> names = wiring
                .listResources("/", "*", BundleWiring.LISTRESOURCES_RECURSE);
        return names.stream().map(new Function<String, Class<?>>() {
            @Override @SneakyThrows
            public Class<?> apply(String name) {
                String n = name.replaceAll("\\.class$", "").replace('/', '.');
                try {
                    return bundle.loadClass(n);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    return null;
                }
            }
        }).filter(c -> c != null)
                    .filter(c -> superclasses.stream().anyMatch(sc -> sc.isAssignableFrom(c)))
                    .filter(c -> c.isAnnotationPresent(GraphQLMutation.class))
                    .filter(c -> !c.isInterface() && !Modifier.isAbstract(c.getModifiers()))
                    .map((Function<Class<?>, Class<? extends T>>) aClass -> (Class<? extends T>) aClass)
                    .collect(Collectors.toList());
    }


    @Getter
    private List<Class<? extends Command>> commands = new ArrayList<>();

    @Override public Collection<GraphQLFieldDefinition> getMutations() {
        return commands.stream()
                        .filter(classPredicate)
                        .map(this::getMutation)
                        .collect(Collectors.toSet());
    }


    private class ScannerBundleTracker implements BundleTrackerCustomizer<Object> {

        @Override public Object addingBundle(Bundle bundle,
                                             BundleEvent event) {
            commands.addAll(findSubTypesOf(bundle, Collections.singletonList(Command.class)));
            return null;
        }

        @Override
        public void modifiedBundle(Bundle bundle, BundleEvent event,
                                   Object object) {
        }

        @Override
        public void removedBundle(Bundle bundle, BundleEvent event,
                                  Object object) {
            commands.removeAll(findSubTypesOf(bundle, Collections.singletonList(Command.class)));
        }
    }
}

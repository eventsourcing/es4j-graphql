/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import graphql.annotations.DefaultTypeFunction;
import org.osgi.framework.BundleContext;

import java.util.UUID;

public class BundleActivator implements org.osgi.framework.BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        DefaultTypeFunction.register(UUID.class, new UUIDFunction());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}

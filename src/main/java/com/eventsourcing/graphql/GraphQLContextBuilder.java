/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class GraphQLContextBuilder implements graphql.servlet.GraphQLContextBuilder {

    private GraphQLRepositoryProvider repositoryProvider;

    @Reference
    public void setRepositoryProvider(GraphQLRepositoryProvider repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    @Override
    public GraphQLContext build(Optional<HttpServletRequest> req, Optional<HttpServletResponse> resp) {
        return new GraphQLContext<>(repositoryProvider.getRepository(), req, resp);
    }
}

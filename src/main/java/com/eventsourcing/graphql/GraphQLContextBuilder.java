/**
 * Copyright 2016 Eventsourcing team
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

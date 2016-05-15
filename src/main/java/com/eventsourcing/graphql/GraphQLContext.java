/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import com.eventsourcing.Command;
import com.eventsourcing.Repository;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class GraphQLContext<C extends Command> extends graphql.servlet.GraphQLContext {
    @Getter @Setter
    private Repository repository;
    @Getter @Setter
    private C command;
    @Getter @Setter
    private String clientMutationId;

    public GraphQLContext(Repository repository, Optional<HttpServletRequest> request,
                          Optional<HttpServletResponse> response) {
        super(request, response);
        this.repository = repository;
    }

}

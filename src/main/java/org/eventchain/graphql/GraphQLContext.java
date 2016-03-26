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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eventchain.Command;
import org.eventchain.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@AllArgsConstructor
public class GraphQLContext<C extends Command> {
    @Getter @Setter
    private Repository repository;
    @Getter @Setter
    private Optional<HttpServletRequest> request;
    @Getter @Setter
    private Optional<HttpServletResponse> response;
    @Getter @Setter
    private C command;
}

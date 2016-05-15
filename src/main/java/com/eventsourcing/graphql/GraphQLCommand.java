/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.eventsourcing.graphql;

import com.eventsourcing.Command;
import com.eventsourcing.layout.LayoutIgnore;
import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GraphQLCommand<R> extends Command<R> {

    protected void beforePublishing() {}

    @Setter @Getter(onMethod = @__(@LayoutIgnore)) @Accessors(fluent = true)
    private DataFetchingEnvironment environment;

    @Getter(onMethod = @__({@GraphQLField, @LayoutIgnore})) @Setter
    private String clientMutationId;
}

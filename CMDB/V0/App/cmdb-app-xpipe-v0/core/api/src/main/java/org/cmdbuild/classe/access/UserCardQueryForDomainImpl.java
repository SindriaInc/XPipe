/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import org.cmdbuild.dao.beans.RelationDirection;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class UserCardQueryForDomainImpl implements UserCardQueryForDomain {

    private final String domainName;
    private final RelationDirection direction;
    private final long originId;

    private UserCardQueryForDomainImpl(UserCardQueryForDomainImplBuilder builder) {
        this.domainName = checkNotBlank(builder.domainName);
        this.direction = checkNotNull(builder.direction);
        this.originId = checkNotNullAndGtZero(builder.originId);
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public RelationDirection getDirection() {
        return direction;
    }

    @Override
    public long getOriginId() {
        return originId;
    }

    public static UserCardQueryForDomainImplBuilder builder() {
        return new UserCardQueryForDomainImplBuilder();
    }

    public static UserCardQueryForDomainImplBuilder copyOf(UserCardQueryForDomain source) {
        return new UserCardQueryForDomainImplBuilder()
                .withDomainName(source.getDomainName())
                .withDirection(source.getDirection())
                .withOriginId(source.getOriginId());
    }

    public static class UserCardQueryForDomainImplBuilder implements Builder<UserCardQueryForDomainImpl, UserCardQueryForDomainImplBuilder> {

        private String domainName;
        private RelationDirection direction;
        private Long originId;

        public UserCardQueryForDomainImplBuilder withDomainName(String domainName) {
            this.domainName = domainName;
            return this;
        }

        public UserCardQueryForDomainImplBuilder withDirection(RelationDirection direction) {
            this.direction = direction;
            return this;
        }

        public UserCardQueryForDomainImplBuilder withOriginId(Long originId) {
            this.originId = originId;
            return this;
        }

        @Override
        public UserCardQueryForDomainImpl build() {
            return new UserCardQueryForDomainImpl(this);
        }

    }
}

package com.hyuuny.nonfaceauthservice.domain.repository;

import com.hyuuny.nonfaceauthservice.common.constants.AuthProvider;
import com.hyuuny.nonfaceauthservice.common.jpa.support.Querydsl4RepositorySupport;
import com.hyuuny.nonfaceauthservice.domain.entity.Account;
import com.hyuuny.nonfaceauthservice.domain.entity.AccountAuthProvider;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.hyuuny.nonfaceauthservice.domain.entity.QAccount.account;
import static com.hyuuny.nonfaceauthservice.domain.entity.QAccountAuthProvider.accountAuthProvider;
import static com.hyuuny.nonfaceauthservice.domain.entity.QExchangeCode.exchangeCode;
import static com.hyuuny.nonfaceauthservice.domain.entity.QRole.role;

@Repository
public class AccountDomainRepository extends Querydsl4RepositorySupport {

    public AccountDomainRepository() {
        super(Account.class);
    }

    public Optional<Account> findAccountByExchangeCodeAndUserId(final String code, final Long userId) {
        Account existingAccount = selectFrom(account)
                .join(exchangeCode).on(exchangeCode.userId.eq(account.id))
                .join(role).on(role.id.eq(account.role.id))
                .where(
                        exchangeCode.code.eq(code),
                        exchangeCode.userId.eq(userId)
                )
                .fetchOne();
        return Optional.ofNullable(existingAccount);
    }

    public Optional<AccountAuthProvider> findAccountAuthProvider(
            AuthProvider authProvider,
            String providerUserIdentifier
    ) {
        AccountAuthProvider foundAccountAuthProvider = selectFrom(accountAuthProvider)
                .join(account).on(account.id.eq(accountAuthProvider.account.id)).fetchJoin()
                .where(
                        accountAuthProvider.provider.eq(authProvider),
                        accountAuthProvider.providerUserIdentifier.eq(providerUserIdentifier)
                )
                .fetchOne();
        return Optional.ofNullable(foundAccountAuthProvider);
    }

}

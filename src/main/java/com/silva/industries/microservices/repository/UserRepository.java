package com.silva.industries.microservices.repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import com.silva.industries.microservices.repository.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Async
    CompletableFuture<Optional<User>> findAsyncById(@Param("id") Long id);

    @Async
    CompletableFuture<Iterable<User>> findAllByIdIsGreaterThan(Long id);

}

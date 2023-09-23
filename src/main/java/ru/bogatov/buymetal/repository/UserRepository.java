package ru.bogatov.buymetal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bogatov.buymetal.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Modifying
    @Query(value = "update usr set refresh = :refresh_token where id = :id", nativeQuery = true)
    void updateRefreshToken(@Param("id") UUID id, @Param("refresh_token") String refresh);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}

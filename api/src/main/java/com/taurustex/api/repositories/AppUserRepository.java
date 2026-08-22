package com.taurustex.api.repositories;



import com.taurustex.api.basis.BaseRepository;
import com.taurustex.api.models.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends BaseRepository<AppUser, String> {
    boolean existsByUsername(String adminUsername);

    Optional<AppUser> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM AppUser u WHERE " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :kw, '%'))")
    Page<AppUser> findByKeyword(@Param("kw") String keyword, Pageable pageable);

    Optional<AppUser> findByUsername(String username);
}

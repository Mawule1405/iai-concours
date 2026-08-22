package com.taurustex.api.basis;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id")
    void deletePhysicallyById(@Param("id") ID id);

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} e WHERE e.id IN :ids ")
    void deleteAllPhysically(@Param("ids") List<ID> ids);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} t SET t.deleted = true, t.deletedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    void deleteById(@Param("id") ID id);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} t SET t.deleted = true, t.deletedAt= CURRENT_TIMESTAMP WHERE t = :entity")
    void delete(@Param("entity") T entity);


}

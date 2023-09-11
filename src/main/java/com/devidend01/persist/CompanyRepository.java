package com.devidend01.persist;

import com.devidend01.persist.entitiy.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
    // springBoot에서 Repository 에 정해진 규칙에 맞는 naming으로 메서드 이름을 적었기 때문에 따로 내용을 작성하지 않아도 작동함.

    Optional<CompanyEntity> findByName(String name);
    // Optional 넣은 이유는 nullPointException 방지 및 깔끔한 정리
    Optional<CompanyEntity> findByTicker(String ticker);

    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}

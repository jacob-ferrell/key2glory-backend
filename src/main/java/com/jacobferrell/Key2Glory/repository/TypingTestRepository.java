package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.model.TypingTestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypingTestRepository extends JpaRepository<TypingTest, Long> {
    Page<TypingTest> findByType(TypingTestType type, Pageable pageable);
    Page<TypingTest> findAll(Specification<TypingTest> spec, Pageable pageable);
}

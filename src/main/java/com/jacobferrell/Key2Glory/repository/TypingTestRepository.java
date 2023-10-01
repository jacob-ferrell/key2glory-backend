package com.jacobferrell.Key2Glory.repository;

import com.jacobferrell.Key2Glory.model.Score;
import com.jacobferrell.Key2Glory.model.TypingTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypingTestRepository extends JpaRepository<TypingTest, Long> {

}

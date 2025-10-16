package com.inixindo.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inixindo.market.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    
}

package com.inixindo.market.service;

import org.springframework.stereotype.Service;

import com.inixindo.market.dto.MemberRequest;
import com.inixindo.market.model.Member;
import com.inixindo.market.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MemberService {
    // repo
    private final MemberRepository repo;

    // fungsi tambah data
    @Transactional
    public String tambah(MemberRequest request){
        Member baru = new Member();
        baru.setNama(request.nama());
        baru.setAlamat(request.alamat());
        baru.setFavorit(request.alamat());
        baru.setNohp(request.nohp());

        // simpan
        repo.save(baru);

        return "Berhasil input member";
    }
}

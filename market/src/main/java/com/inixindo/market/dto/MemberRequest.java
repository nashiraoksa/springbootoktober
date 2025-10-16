package com.inixindo.market.dto;

import io.micrometer.common.lang.NonNull;

public record MemberRequest(@NonNull String nama, String alamat, String favorit, String nohp) {
    
}

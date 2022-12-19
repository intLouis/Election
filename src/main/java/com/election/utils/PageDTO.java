package com.election.utils;

import lombok.*;
import lombok.experimental.SuperBuilder;

public interface PageDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @EqualsAndHashCode(callSuper = false)
    class Request {

        /**
         * 游标查询方向
         */
        private Boolean direction;

        /**
         * 游标，可以是id，也可以是一串json
         */
        private String anchor;

        /**
         * 页大小
         */
        @NonNull
        private Integer pageSize;

        /**
         * 页码
         */
        private Integer pageNo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    class Response {

        /**
         * 游标
         */
        private String anchor;

    }

}

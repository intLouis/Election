package com.election.service.utils;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import org.springframework.stereotype.Component;

@Component
public class BloomFilter {

    public final static BitMapBloomFilter bitMapBloomFilter = new BitMapBloomFilter(1000);

}

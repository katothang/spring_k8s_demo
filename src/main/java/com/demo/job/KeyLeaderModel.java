package com.demo.job;

import io.etcd.jetcd.ByteSequence;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KeyLeaderModel {
    private ByteSequence key;
    private ByteSequence value;
}

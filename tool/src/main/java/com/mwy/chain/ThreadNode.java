package com.mwy.chain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThreadNode {
    private int mainId;
    private int selfId;
    private int parentId;

    private String className;
}

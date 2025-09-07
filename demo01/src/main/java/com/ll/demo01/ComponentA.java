package com.ll.demo01;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComponentA {
    private final ComponentB componentB;
    private final ComponentC componentC;
    private final ComponentC componentD;
    private final ComponentC componentE;


    @Autowired
    private ComponentB ComponentB;
    public String action() {
        return "ComponentA action / " + ComponentB.getAction();
    }
}

package ru.nsu.fit.subsplayer.entities;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Test {
    private final String name;
    private final List<Long> phraseIds;
}

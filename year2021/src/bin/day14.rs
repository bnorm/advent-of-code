use std::collections::HashMap;
use std::collections::hash_map::Entry;
use std::fs;
use std::time::Instant;

use itertools::Itertools;
use recap::Recap;
use serde::Deserialize;

#[derive(Debug, PartialEq, Eq, Hash, Deserialize, Recap)]
#[recap(regex = r#"(?P<template>[A-Z]+)"#)]
struct Template {
    template: String,
}

#[derive(Debug, PartialEq, Eq, Hash, Deserialize, Recap)]
#[recap(regex = r#"(?P<first>[A-Z])(?P<last>[A-Z]) -> (?P<middle>[A-Z])"#)]
struct Instruction {
    first: char,
    last: char,
    middle: char,
}

#[derive(Debug, PartialEq, Eq, Hash)]
struct DpKey {
    first: char,
    last: char,
    depth: u32,
}

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u64> {
    let instant = Instant::now();
    let (template, instructions) = read_input()?;

    let counts = counts(&10, template, &instructions);

    let min = counts.values().min().unwrap();
    let max = counts.values().max().unwrap();

    println!("[part1] time={:?}", instant.elapsed());
    return Some(max - min);
}

fn part2() -> Option<u64> {
    let instant = Instant::now();
    let (template, instructions) = read_input()?;

    let counts = counts(&40, template, &instructions);

    let min = counts.values().min().unwrap();
    let max = counts.values().max().unwrap();

    println!("[part2] time={:?}", instant.elapsed());
    return Some(max - min);
}

fn read_input() -> Option<(String, HashMap<(char, char), char>)> {
    let filename = "res/input14.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let template = contents.lines()
        .filter_map(|line| line.parse::<Template>().ok())
        .next()?;

    let instructions = contents.lines()
        .filter_map(|line| line.parse::<Instruction>().ok())
        .collect_vec();

    let mut map: HashMap<(char, char), char> = HashMap::new();
    for i in instructions {
        map.insert((i.first, i.last), i.middle);
    }

    return Some((template.template, map));
}

fn counts(iterations: &u32, template: String, instructions: &HashMap<(char, char), char>) -> HashMap<char, u64> {
    let chars = template.chars().collect_vec();
    let table = build_table(iterations, &instructions);

    let mut counts: HashMap<char, u64> = table.get(&key(iterations, &chars[0], &chars[1])).unwrap().clone();
    for i in 1..template.len() - 1 {
        counts = merge(&chars[i], &counts, table.get(&key(iterations, &chars[i], &chars[i + 1])).unwrap());
    }

    return counts;
}

fn build_table(iterations: &u32, instructions: &HashMap<(char, char), char>) -> HashMap<DpKey, HashMap<char, u64>> {
    let mut table: HashMap<DpKey, HashMap<char, u64>> = HashMap::new();

    for ((first, last), _) in instructions {
        let mut counts: HashMap<char, u64> = HashMap::new();
        increment_by(&mut counts, first, &1);
        increment_by(&mut counts, last, &1);
        table.insert(key(&0, first, last), counts);
    }

    for i in 1..(iterations + 1) {
        let depth_1 = i - 1;
        for ((first, last), middle) in instructions {
            let left = table.get(&key(&depth_1, first, middle)).unwrap();
            let right = table.get(&key(&depth_1, middle, last)).unwrap();
            table.insert(key(&i, first, last), merge(middle, left, right));
        }
    }

    return table;
}

fn key(depth: &u32, first: &char, last: &char) -> DpKey {
    return DpKey { first: first.clone(), last: last.clone(), depth: depth.clone() };
}

fn merge(middle: &char, left: &HashMap<char, u64>, right: &HashMap<char, u64>) -> HashMap<char, u64> {
    let mut counts: HashMap<char, u64> = left.clone();

    for (key, value) in right {
        increment_by(&mut counts, key, value);
    }

    let count = counts.get_mut(middle).unwrap();
    *count -= 1;

    return counts;
}

fn increment_by(counts: &mut HashMap<char, u64>, key: &char, value: &u64) {
    let count = match counts.entry(key.clone()) {
        Entry::Occupied(o) => o.into_mut(),
        Entry::Vacant(v) => v.insert(0)
    };
    *count += value;
}

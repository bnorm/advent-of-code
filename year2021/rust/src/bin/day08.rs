use std::collections::{HashMap, HashSet};
use std::fs;
use std::time::Instant;

use itertools::Itertools;

use recap::Recap;
use serde::Deserialize;

#[derive(Debug, Deserialize, Recap)]
#[recap(regex = r#"(?P<segments>[a-z ]+) \| (?P<output>[a-z ]+)"#)]
struct Input {
    segments: String,
    output: String,
}

#[derive(Debug)]
struct Display {
    segments: Vec<u8>,
    output: Vec<u8>,
}

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<usize> {
    let start = Instant::now();
    let input = read_input()?;

    let result = input
        .iter()
        .flat_map(|d| d.output.iter())
        .filter(|o| {
            o.count_ones() == 2 || o.count_ones() == 3 || o.count_ones() == 4 || o.count_ones() == 7
        })
        .count();

    println!("[part1] time={:?}", start.elapsed());
    return Some(result);
}

fn part2() -> Option<u32> {
    let start = Instant::now();
    let input = read_input()?;

    let result = input.iter().filter_map(|d| decode_display(d)).sum();

    println!("[part2] time={:?}", start.elapsed());
    return Some(result);
}

fn read_input() -> Option<Vec<Display>> {
    let filename = "res/input08.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let input = contents
        .lines()
        .filter_map(|line| line.parse::<Input>().ok())
        .map(|input| Display {
            segments: input
                .segments
                .split_whitespace()
                .map(|v| segment_to_binary(&v.to_string()))
                .collect_vec(),
            output: input
                .output
                .split_whitespace()
                .map(|v| segment_to_binary(&v.to_string()))
                .collect_vec(),
        })
        .collect_vec();

    return Some(input);
}

fn decode_display(display: &Display) -> Option<u32> {
    let mut known: HashMap<u8, u32> = HashMap::new();
    let segments = display
        .segments
        .iter()
        .chain(display.output.iter())
        .copied()
        .collect_vec();

    // 2 ones count -> 1
    // 3 ones count -> 7
    // 4 ones count -> 4
    // 7 ones count -> 8
    let one = *segments.iter().find(|&s| s.count_ones() == 2).unwrap();
    known.insert(one, 1);
    let seven = *segments.iter().find(|&s| s.count_ones() == 3).unwrap();
    known.insert(seven, 7);
    let four = *segments.iter().find(|&s| s.count_ones() == 4).unwrap();
    known.insert(four, 4);
    let eight = *segments.iter().find(|&s| s.count_ones() == 7).unwrap();
    known.insert(eight, 8);

    // 5 ones count -> 2, 3, or 5
    // 3 will have all the values of 1
    // 5 will only be missing 1 segment of 4
    // 2 will only be missing 2 segments of 4
    let three = *segments
        .iter()
        .find(|&s| s.count_ones() == 5 && s & &one == one)
        .unwrap();
    known.insert(three, 3);
    let five = *segments
        .iter()
        .find(|&s| s.count_ones() == 5 && s != &three && (s & &four).count_ones() == 3)
        .unwrap();
    known.insert(five, 5);
    let two = *segments
        .iter()
        .find(|&s| s.count_ones() == 5 && s != &three && (s & &four).count_ones() == 2)
        .unwrap();
    known.insert(two, 2);

    // 6 ones count -> 0, 6, or 9
    // 9 will have all the segments of 4
    // 6 will not have all the segments of 1
    // 0 will not have all the segments of 4
    let nine = *segments
        .iter()
        .find(|&s| s.count_ones() == 6 && s & &four == four)
        .unwrap();
    known.insert(nine, 9);
    let six = *segments
        .iter()
        .find(|&s| s.count_ones() == 6 && (s & &one).count_ones() == 1)
        .unwrap();
    known.insert(six, 6);
    let zero = *segments
        .iter()
        .find(|&s| s.count_ones() == 6 && s != &nine && s != &six)
        .unwrap();
    known.insert(zero, 0);

    let mut value = 0;
    for c in &display.output {
        value *= 10;
        value += known.get(c).unwrap()
    }

    return Some(value);
}

fn segment_to_binary(segment: &String) -> u8 {
    let mut binary = 0;
    for c in segment.chars() {
        binary |= 1 << (c as u32 - 'a' as u32);
    }
    return binary;
}

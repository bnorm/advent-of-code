use std::fs;
use std::collections::{HashMap, VecDeque};
use std::io::repeat;

use itertools::Itertools;

use year2021::geo::{intersection, Line, Point};

fn main() {
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
}

fn part1() -> Option<i64> {
    let fishes = read_input()?;
    return Some(propagate(80, &fishes));
}

fn part2() -> Option<i64> {
    let fishes = read_input()?;
    return Some(propagate(256, &fishes));
}

fn read_input() -> Option<Vec<i32>> {
    let filename = "res/input06.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let input = contents.lines()
        .flat_map(|line| line.split(","))
        .filter_map(|value| value.parse::<i32>().ok())
        .collect_vec();

    return Some(input);
}

fn propagate(days: i32, fishes: &Vec<i32>) -> i64 {
    let mut stack: VecDeque<i64> = VecDeque::new();
    for _ in 0..9 {
        stack.push_back(0)
    }

    for f in fishes {
        stack[*f as usize] += 1;
    }

    for _ in 0..days {
        let born = stack.pop_front().unwrap_or(0);
        stack.push_back(born);
        stack[6] += born;
    }

    return stack.iter().sum();
}

use std::fs;

use itertools::Itertools;
use recap::Recap;
use serde::Deserialize;

#[derive(Debug, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"(?P<op>[a-z]+) (?P<n>\d+)"#)]
struct Command {
    op: Op,
    n: i32,
}

#[derive(Debug, PartialEq, Deserialize)]
#[serde(rename_all = "lowercase")]
enum Op {
    Forward,
    Down,
    Up,
}

fn main() {
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
}

fn read_input() -> Vec<Command> {
    let filename = "res/input02.txt";
    let contents = fs::read_to_string(filename).unwrap();
    return contents
        .lines()
        .filter_map(|line| line.parse::<Command>().ok())
        .collect_vec();
}

fn part1() -> Option<i32> {
    let commands = read_input();

    let mut depth: i32 = 0;
    let mut position: i32 = 0;
    for command in commands {
        match command {
            Command { op: Op::Forward, n } => position += n,
            Command { op: Op::Down, n } => depth += n,
            Command { op: Op::Up, n } => depth -= n,
        }
    }

    println!("[part1] position={} depth={}", position, depth);
    return Some(position * depth);
}

fn part2() -> Option<i32> {
    let commands = read_input();

    let mut depth = 0;
    let mut position = 0;
    let mut aim = 0;
    for command in commands {
        match command {
            Command { op: Op::Forward, n } => {
                position += n;
                depth += n * aim;
            }
            Command { op: Op::Down, n } => aim += n,
            Command { op: Op::Up, n } => aim -= n,
        }
    }

    println!("[part2] position={} depth={}", position, depth);
    return Some(position * depth);
}

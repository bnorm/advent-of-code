use std::fs;

use itertools::Itertools;

fn main() {
    part1();
    part2();
}

fn part1() {
    let filename = "input.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let commands = contents.lines()
        .map(|x| {
            let split = x.split_whitespace().collect_vec();
            return (split[0], split[1].parse::<i32>().expect("Unable to parse file line as int"));
        })
        .collect_vec();

    let mut depth = 0;
    let mut position = 0;
    for (op, n) in commands {
        if op == "forward" {
            position = position + n;
        } else if op == "down" {
            depth = depth + n
        } else if op == "up" {
            depth = depth - n
        }
    }

    println!("[part1] position={} depth={} answer={}", position, depth, position * depth);
}

fn part2() {
    let filename = "input.txt";
    let contents = fs::read_to_string(filename)
        .expect("Something went wrong reading the file");

    let commands = contents.lines()
        .map(|x| {
            let split = x.split_whitespace().collect_vec();
            return (split[0], split[1].parse::<i32>().expect("Unable to parse file line as int"));
        })
        .collect_vec();

    let mut depth = 0;
    let mut position = 0;
    let mut aim = 0;
    for (op, n) in commands {
        if op == "forward" {
            position = position + n;
            depth = depth + n * aim;
        } else if op == "down" {
            aim = aim + n;
        } else if op == "up" {
            aim = aim - n;
        }
    }

    println!("[part2] position={} depth={} answer={}", position, depth, position * depth);
}

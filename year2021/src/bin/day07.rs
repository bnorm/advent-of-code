use std::fs;
use std::time::Instant;

use itertools::Itertools;

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u64> {
    let start = Instant::now();
    let input = read_input()?;

    let &max = input.iter().max()?;
    let &min = input.iter().min()?;

    let result = (min..max).map(|l| cost_part1(l, &input)).min();

    println!("[part1] time={:?}", start.elapsed());
    return result;
}

fn part2() -> Option<u64> {
    let start = Instant::now();
    let input = read_input()?;

    let &max = input.iter().max()?;
    let &min = input.iter().min()?;

    let result = (min..max).map(|l| cost_part2(l, &input)).min();

    println!("[part2] time={:?}", start.elapsed());
    return result;
}

fn read_input() -> Option<Vec<i32>> {
    let filename = "res/input07.txt";
    let contents = fs::read_to_string(filename).ok()?;

    let input = contents.lines()
        .flat_map(|line| line.split(","))
        .filter_map(|value| value.parse::<i32>().ok())
        .collect_vec();

    return Some(input);
}

fn cost_part1(l: i32, crabs: &Vec<i32>) -> u64 {
    let mut cost: u64 = 0;
    for c in crabs {
        cost += (l - c).abs() as u64;
    }
    return cost;
}

fn cost_part2(l: i32, crabs: &Vec<i32>) -> u64 {
    let mut cost: u64 = 0;
    for c in crabs {
        let distance = (l - c).abs() as u64;
        cost += distance * (distance + 1) / 2;
    }
    return cost;
}

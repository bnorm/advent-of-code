use std::fs;
use std::time::Instant;

use itertools::Itertools;

fn main() {
    let start = Instant::now();
    println!("[part1] answer={:?}", part1());
    println!("[part2] answer={:?}", part2());
    println!("finished in {:?}", start.elapsed());
}

fn part1() -> Option<u128> {
    let start = Instant::now();
    let fishes = read_input()?;
    let result = Some(propagate(80, &fishes));
    println!("[part1] time={:?}", start.elapsed());
    return result;
}

fn part2() -> Option<u128> {
    let start = Instant::now();
    let fishes = read_input()?;
    let result = Some(propagate(256, &fishes));
    println!("[part2] time={:?}", start.elapsed());
    return result;
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

fn propagate(days: i32, fishes: &Vec<i32>) -> u128 {
    let mut fish: [u128; 9] = [0; 9];
    for f in fishes {
        fish[*f as usize] += 1;
    }

    for day in 0..days as usize {
        fish[(day + 7) % 9] += fish[day % 9]
    }

    return fish.iter().sum();
}

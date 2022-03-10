use itertools::Itertools;
use std::cmp::max;
use std::collections::{HashMap, HashSet, VecDeque};
use std::fs;
use std::time::Instant;

use year2021::scanner::{parse_scanner_report, Point, Scanner};

fn main() {
    let start = Instant::now();

    let scanners = read_input();
    let (first, rest) = scanners.split_at(1);
    let located_scanners = first[0].locate(rest);

    println!("[part1] answer={:?}", part1(&located_scanners));
    println!("[part2] answer={:?}", part2(&located_scanners));
    println!("finished in {:?}", start.elapsed());
}

fn read_input() -> Vec<Scanner> {
    let filename = "res/input19.txt";
    let contents = fs::read_to_string(filename).unwrap();
    return parse_scanner_report(&contents);
}

fn part1(located_scanners: &[Scanner]) -> Option<usize> {
    let instant = Instant::now();

    let mut located_beacons = HashSet::<Point>::new();
    for scanner in located_scanners {
        located_beacons.extend(scanner.beacons.iter().map(|b| b + &scanner.location));
    }

    println!("[part1] time={:?}", instant.elapsed());
    return Some(located_beacons.len());
}

fn part2(located_scanners: &[Scanner]) -> Option<isize> {
    let instant = Instant::now();

    let mut max_dist = 0;
    for i in 0..located_scanners.len() {
        for j in i + 1..located_scanners.len() {
            let dist = located_scanners[i]
                .location
                .manhattan_distance(&located_scanners[j].location);
            max_dist = max(max_dist, dist);
        }
    }

    println!("[part2] time={:?}", instant.elapsed());
    return Some(max_dist);
}

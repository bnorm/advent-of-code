use std::cmp::{max, min};
use std::collections::VecDeque;
use std::fmt::Debug;
use std::fs;
use std::time::Instant;

use itertools::Itertools;
use recap::Recap;
use serde::Deserialize;

use year2021::cube::{Cube, Position, Region};

#[derive(Debug, PartialEq, Deserialize, Recap)]
#[recap(regex = r#"(?P<op>[a-z]+) x=(?P<x1>-?\d+)..(?P<x2>-?\d+),y=(?P<y1>-?\d+)..(?P<y2>-?\d+),z=(?P<z1>-?\d+)..(?P<z2>-?\d+)"#)]
struct Instruction {
    op: Op,
    x1: isize,
    x2: isize,
    y1: isize,
    y2: isize,
    z1: isize,
    z2: isize,
}

#[derive(Debug, PartialEq, Deserialize)]
#[serde(rename_all = "lowercase")]
enum Op {
    On,
    Off,
}

type Input = Vec<Instruction>;

fn main() {
    let start = Instant::now();
    let input = read_input();
    println!("[part1] answer={:?}", part1(&input));
    println!("[part2] answer={:?}", part2(&input));
    println!("finished in {:?}", start.elapsed());
}

fn read_input() -> Input {
    let filename = "res/input22.txt";
    let contents = fs::read_to_string(filename).unwrap();
    return contents.lines()
        .filter_map(|line| line.parse::<Instruction>().ok())
        .collect_vec();
}

fn part1(input: &Input) -> Option<usize> {
    let instant = Instant::now();

    let mut cube = Cube::new(vec![vec![vec![false; 101]; 101]; 101], -50..51, -50..51, -50..51);
    for instruction in input {
        let x1 = max(instruction.x1, -50);
        let x2 = min(instruction.x2, 50);
        let y1 = max(instruction.y1, -50);
        let y2 = min(instruction.y2, 50);
        let z1 = max(instruction.z1, -50);
        let z2 = min(instruction.z2, 50);

        for x in x1..x2 + 1 {
            for y in y1..y2 + 1 {
                for z in z1..z2 + 1 {
                    let p = Position::new(x, y, z);
                    cube[&p] = instruction.op == Op::On;
                }
            }
        }
    }

    let mut count = 0;
    for x in cube.x_range.clone() {
        for y in cube.y_range.clone() {
            for z in cube.z_range.clone() {
                count += if cube[&Position::new(x, y, z)] { 1 } else { 0 };
            }
        }
    }

    println!("[part1] time={:?}", instant.elapsed());
    return Some(count);
}

fn part2(input: &Input) -> Option<u128> {
    let instant = Instant::now();

    let mut regions = VecDeque::<Region>::new();
    for instruction in input {
        let region = Region::new(instruction.x1..=instruction.x2, instruction.y1..=instruction.y2, instruction.z1..=instruction.z2);
        // println!("op={:?} region={:?} ", instruction.op, region);
        if instruction.op == Op::On {
            for a in add_region(&regions, region) {
                regions.push_back(a);
            }
        } else {
            remove_region(&mut regions, region);
        }
    }

    let mut count = 0;
    for region in regions {
        count += ((region.x_range.end() + 1) - region.x_range.start()) as u128
            * ((region.y_range.end() + 1) - region.y_range.start()) as u128
            * ((region.z_range.end() + 1) - region.z_range.start()) as u128
    }

    println!("[part2] time={:?}", instant.elapsed());
    return Some(count);
}

fn add_region(regions: &VecDeque<Region>, region: Region) -> Vec<Region> {
    return regions.iter().fold(vec![region], |additions, r| {
        let mut next = Vec::<Region>::new();
        for addition in additions {
            next.extend(r.carve(&addition));
        }
        return next;
    });
}

fn remove_region(regions: &mut VecDeque<Region>, region: Region) {
    let mut intersections = Vec::<Region>::new();

    let region_count = regions.len();
    for _ in 0..region_count {
        if let Some(r) = regions.pop_front() {
            if r.intersect(&region) {
                intersections.push(r);
            } else {
                regions.push_back(r);
            }
        }
    }

    for intersection in intersections {
        for c in region.carve(&intersection) {
            regions.push_back(c);
        }
    }
}
